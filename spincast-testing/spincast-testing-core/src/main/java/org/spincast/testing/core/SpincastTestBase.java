package org.spincast.testing.core;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.config.SpincastConfig;
import org.spincast.core.guice.GuiceTweaker;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.guice.SpincastPlugin;
import org.spincast.shaded.org.apache.commons.io.FileUtils;
import org.spincast.testing.core.utils.SpincastConfigTestingDefault;
import org.spincast.testing.junitrunner.BeforeAfterClassMethodsProvider;
import org.spincast.testing.junitrunner.CanBeDisabled;
import org.spincast.testing.junitrunner.RepeatedClassAfterMethodProvider;
import org.spincast.testing.junitrunner.SpincastJUnitRunner;
import org.spincast.testing.junitrunner.TestFailureListener;

import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Scopes;

/**
 * Base class for Spincast test classes.
 *
 * <p>
 * Uses a custom Junit runner,
 * {@link org.spincast.testing.junitrunner.SpincastJUnitRunner SpincastJUnitRunner}.
 * <p>
 * This runner create a single instance of the test class for all of its tests,
 * instead of a new instance for each test. It also calls a <code>beforeClass()</code>
 * method before the tests are run and an <code>afterClass()</code> method after
 * they are run.
 * <p>
 * A class extending this will be part of a Guice context
 * (created using the {@link #createInjector() createInjector} method) and
 * the required dependencies will be injected into it.
 * <p>
 * A {@link GuiceTweaker} instance is used to
 * be able to tweak a Guice context automagically. This for example
 * allows you to start your actual application, using its main() method.
 * Note that the Guice tweaker only works when the Guice context is created
 * using the standard <code>Spincast Bootstrapper</code> .
 * <p>
 * Note that you can annotate a test class with:
 * <pre>
 * {@literal @}FixMethodOrder(MethodSorters.NAME_ASCENDING)
 * </pre>
 * if you need its tests to be ran in order (using the
 * alphabetical order of the test methods' names).
 */
@RunWith(SpincastJUnitRunner.class)
public abstract class SpincastTestBase implements BeforeAfterClassMethodsProvider,
                                       TestFailureListener,
                                       RepeatedClassAfterMethodProvider,
                                       CanBeDisabled {

    protected static final Logger logger = LoggerFactory.getLogger(SpincastTestBase.class);

    private Injector guice;
    private File testingWritableTempDir;
    private GuiceTweaker previousGuiceTweaker;
    private Map<String, String> extraSystemProperties;
    private Map<String, String> extraSystemPropertiesOriginal;

    @Inject
    protected SpincastConfig spincastConfig;

    @Override
    public boolean isTestClassDisabledPreBeforeClass() {
        // Not disabled by default
        return false;
    }

    @Override
    public boolean isTestClassDisabledPostBeforeClass() {
        // Not disabled by default
        return false;
    }

    @Override
    public void beforeClass() {
        //==========================================
        // Extra System properties to add?
        //==========================================
        addExtraSystemProperties();

        //==========================================
        // Creates a GuiceTweaker
        // as a ThreadLocal variable to tweak some
        // bindings during the Injector creation, if
        // "Spincast.configure()" if used to create that
        // Injector.
        //==========================================
        this.previousGuiceTweaker = GuiceTweaker.threadLocal.get();
        GuiceTweaker.threadLocal.set(createGuiceTweaker());
        try {
            this.guice = createInjector();
        } finally {
            if (this.previousGuiceTweaker != null) {
                GuiceTweaker.threadLocal.set(this.previousGuiceTweaker);
            } else {
                GuiceTweaker.threadLocal.remove();
            }
        }

        validateCreatedInjector(this.guice);
        this.guice.injectMembers(this);
    }

    /**
     * Adds System properties as they are provided by the
     * {@link #getExtraSystemProperties()} method.
     */
    protected void addExtraSystemProperties() {

        this.extraSystemProperties = getExtraSystemProperties();
        if (this.extraSystemProperties != null && this.extraSystemProperties.size() > 0) {
            this.extraSystemPropertiesOriginal = new HashMap<String, String>();
            for (Entry<String, String> entry : this.extraSystemProperties.entrySet()) {

                String key = entry.getKey();
                String original = System.getProperty(key);
                if (original != null) {
                    this.extraSystemPropertiesOriginal.put(entry.getKey(), System.getProperty(key));
                }
                System.setProperty(key, entry.getValue());
            }
        }
    }

    /**
     * Extra System properties to set before the tests are run.
     * Those will be automatically reset once the tests are done.
     */
    protected Map<String, String> getExtraSystemProperties() {
        return new HashMap<String, String>();
    }

    /**
     * Extra exact bindings to remvoe before the
     * plugins are applied.
     */
    protected Set<Key<?>> getExtraExactBindingsToRemoveBeforePlugins() {
        return new HashSet<Key<?>>();
    }

    /**
     * Resets System properties.
     */
    protected void resetSystemProperties() {

        if (this.extraSystemProperties != null && this.extraSystemProperties.size() > 0) {
            for (Entry<String, String> entry : this.extraSystemProperties.entrySet()) {
                String key = entry.getKey();
                String original = this.extraSystemPropertiesOriginal.get(key);
                if (original != null) {
                    System.setProperty(key, original);
                } else {
                    System.clearProperty(key);
                }
            }
        }
    }

    protected GuiceTweaker getGuiceTweakerFromThreadLocal() {
        return GuiceTweaker.threadLocal.get();
    }

    /**
     * Validates the created Injector, before the
     * dependencies are injected in the test class.
     */
    protected void validateCreatedInjector(Injector guice) {
        assertNotNull(guice);
    }

    protected GuiceTweaker createGuiceTweaker() {

        GuiceTweaker guiceTweaker = new GuiceTweaker();

        //==========================================
        // Extra plugins to add?
        //==========================================
        List<SpincastPlugin> plugins = getGuiceTweakerExtraPlugins();
        if (plugins != null) {
            for (SpincastPlugin plugin : plugins) {
                guiceTweaker.plugin(plugin);
            }
        }

        //==========================================
        // Tweaks configurations
        //==========================================
        tweakConfigurations(guiceTweaker);

        //==========================================
        // Extra Module to add?
        //==========================================
        if (getGuiceTweakerExtraOverridingModule() != null) {
            guiceTweaker.overridingModule(getGuiceTweakerExtraOverridingModule());
        }

        //==========================================
        // Disable bind current class?
        //==========================================
        if (isDisableBindCurrentClass()) {
            guiceTweaker.disableBindCurrentClass();
        }

        //==========================================
        // Plugins to disable?
        //==========================================
        Set<String> pluginIdsToDisable = getGuiceTweakerPluginsToDisable();
        if (pluginIdsToDisable != null && pluginIdsToDisable.size() > 0) {
            for (String pluginId : getGuiceTweakerPluginsToDisable()) {
                guiceTweaker.pluginToDisable(pluginId);
            }
        }

        return guiceTweaker;
    }

    /**
     * Ids of plugins to disable.
     * <p>
     * Example:
     * <p>
     * <pre>
     * Set<String> pluginIdsToIgnore = super.getGuiceTweakerPluginsToDisable();
     * pluginIdsToIgnore.add(XXXXXX);
     * return pluginIdsToIgnore;
     * </pre>
     */
    protected Set<String> getGuiceTweakerPluginsToDisable() {
        return Sets.newHashSet();
    }

    protected void tweakConfigurations(GuiceTweaker guiceTweaker) {
        final Class<? extends SpincastConfig> configImplClass = getTestingConfigImplementationClass();
        if (configImplClass == null) {
            throw new RuntimeException("The 'getSpincastConfigTestingImplementationClass()' can't return null.");
        }

        //==========================================
        // Tells GuiceTweaker to remove the current
        // Spincast configuration binding.
        //==========================================
        guiceTweaker.bindingHierarchyToRemove(SpincastConfig.class);

        //==========================================
        // Some extra exact bindings to remove?
        //==========================================
        Set<Key<?>> extraExactBindingsToRemove = getExtraExactBindingsToRemoveBeforePlugins();
        if (extraExactBindingsToRemove != null) {
            for (Key<?> key : extraExactBindingsToRemove) {
                guiceTweaker.exactBindingToRemove(key);
            }
        }

        guiceTweaker.overridingModule(new SpincastGuiceModuleBase() {

            @Override
            protected void configure() {
                bind(configImplClass).in(Scopes.SINGLETON);
                bind(SpincastConfig.class).to(configImplClass).in(Scopes.SINGLETON);
            }
        });
    }

    protected boolean isDisableBindCurrentClass() {
        return false;
    }

    /**
     * Extra plugins to be added by the Guice Tweaker.
     */
    protected List<SpincastPlugin> getGuiceTweakerExtraPlugins() {
        // None by default.
        return new ArrayList<SpincastPlugin>();
    }

    /**
     * If an overriding Module is to be added using the
     * Guice tweaker.
     * <p>
     *
     * Can be overriden with something like :
     *
     * <pre>
     * return Modules.override(super.getGuiceTweakerExtraOverridingModule()).with(new SpincastGuiceModuleBase() {
     *     protected void configure() {
     *         // ...
     *     }
     * });
     * </pre>
     */
    protected Module getGuiceTweakerExtraOverridingModule() {

        //==========================================
        // Empty Module, so Modules.combine() and
        // Modules.override() can be used by the extending
        // classes.
        //==========================================
        return new SpincastGuiceModuleBase() {

            @Override
            protected void configure() {
                // nothing
            }
        };
    }

    /**
     * The implementation to use for the <code>SpincastConfig</code> binding,
     * when running tests.
     */
    protected Class<? extends SpincastConfig> getTestingConfigImplementationClass() {
        return SpincastConfigTestingDefault.class;
    }

    /**
     * This method will be called before each test.
     */
    @Before
    public void beforeTest() {
        // nothing by default
    }

    /**
     * This method will be called after each test.
     */
    @After
    public void afterTest() {
        // nothing by default
    }

    @Override
    public void afterClass() {

        //==========================================
        // Resets System variables.
        //==========================================
        try {
            resetSystemProperties();
        } catch (Exception ex) {
            logger.warn(ex.getMessage());
        }

        deleteTestingWritableTempDir();
    }

    @Override
    public void beforeClassException(Throwable ex) {

        //==========================================
        // Resets System variables.
        //==========================================
        resetSystemProperties();
    }

    @Override
    public void afterClassLoops() {
        // nothing by default
    }

    /**
     * You can override this method to be
     * informed when a test fails.
     */
    @Override
    public void testFailure(Failure failure) {
        // nothing
    }

    /**
     * Returns the Guice injector.
     */
    protected Injector getInjector() {
        return this.guice;
    }

    /**
     * Deletes the testing writable temp directory.
     */
    protected void deleteTestingWritableTempDir() {
        try {
            if (this.testingWritableTempDir != null) {
                FileUtils.deleteDirectory(this.testingWritableTempDir);
            }
        } catch (Exception ex) {
            System.err.println(ex);
        }
    }

    /**
     * Returns the directory that can be used to create files
     * and subdirectories during testing.
     * <p>
     * This directory will be deleted when the tests are done.
     */
    protected File getTestingWritableTempDir() {
        if (this.testingWritableTempDir == null || !this.testingWritableTempDir.exists()) {

            //==========================================
            // We don't use the configurations to find a writable
            // directory since, sometimes, tests may want to create
            // files *before* the Guice context is created!
            //==========================================
            //this.testingWritableDir = new File(getSpincastConfig().getSpincastWritableDir().getAbsolutePath() +
            //                                   "/testing");

            File baseDir = new File(System.getProperty("java.io.tmpdir"));
            if (!baseDir.isDirectory()) {
                throw new RuntimeException("Temporary directory doesn't exist : " + baseDir.getAbsolutePath());
            }

            this.testingWritableTempDir = new File(baseDir, "/spincast/testing");
            if (!this.testingWritableTempDir.isDirectory()) {
                boolean mkdirs = this.testingWritableTempDir.mkdirs();
                assertTrue(mkdirs);
            }
            assertTrue(this.testingWritableTempDir.canWrite());
        }
        return this.testingWritableTempDir;
    }

    protected SpincastConfig getSpincastConfig() {
        return this.spincastConfig;
    }

    /**
     * Returns the absolute path to use, given the relative one,
     * to create a temporary test file.
     * <p>
     * This file will be deleted when the tests are done.
     */
    protected String createTestingFilePath(String relativePath) {
        return getTestingWritableTempDir().getAbsolutePath() + "/" + relativePath;
    }

    /**
     * Returns a unique path to use to create a temporary test file.
     * <p>
     * This file will be deleted when the tests are done.
     */
    protected String createTestingFilePath() {
        return createTestingFilePath(UUID.randomUUID().toString());
    }

    /**
     * Create a temp directory.
     * <p>
     * This directory will be deleted when the tests are done.
     */
    protected File createTestingDir() {

        String path = createTestingFilePath();
        File dir = new File(path);
        boolean result = dir.mkdirs();
        if (!result) {
            throw new RuntimeException("Unable to create testing temp dir at: " + path);
        }
        return dir;
    }

    /**
     * The test class must implement this method to create
     * the Guice injector. It can be done by starting a real
     * application (with a <code>main(...)</code> method) or by
     * creating a custom Injector.
     */
    protected abstract Injector createInjector();

}
