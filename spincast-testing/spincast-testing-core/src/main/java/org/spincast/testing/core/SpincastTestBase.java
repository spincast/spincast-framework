package org.spincast.testing.core;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
import org.spincast.testing.utils.BeforeAfterClassMethodsProvider;
import org.spincast.testing.utils.RepeatedClassAfterMethodProvider;
import org.spincast.testing.utils.SpincastJUnitRunner;
import org.spincast.testing.utils.TestFailureListener;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Scopes;

/**
 * Base class for Spincast test classes.
 * 
 * <p>
 * Uses a custom Junit runner, 
 * {@link org.spincast.testing.utils.SpincastJUnitRunner SpincastJUnitRunner}.
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
 */
@RunWith(SpincastJUnitRunner.class)
public abstract class SpincastTestBase implements BeforeAfterClassMethodsProvider,
                                       TestFailureListener,
                                       RepeatedClassAfterMethodProvider {

    protected final Logger logger = LoggerFactory.getLogger(SpincastTestBase.class);

    private Injector guice;
    private File testingWritableDir;
    private GuiceTweaker previousGuiceTweaker;
    private Map<String, String> extraSystemProperties;
    private Map<String, String> extraSystemPropertiesOriginal;

    @Inject
    protected SpincastConfig spincastConfig;

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
        List<SpincastPlugin> plugins = getExtraPlugins();
        if (plugins != null) {
            for (SpincastPlugin plugin : plugins) {
                guiceTweaker.plugin(plugin);
            }
        }

        //==========================================
        // Tweaks configurations
        //==========================================
        final Class<? extends SpincastConfig> configImplClass = getTestingConfigImplementationClass();
        if (configImplClass == null) {
            throw new RuntimeException("The 'getSpincastConfigTestingImplementationClass()' can't return null.");
        }

        //==========================================
        // Tells GuiceTweaker to remove the current
        // configuration bindings.
        //==========================================
        guiceTweaker.bindingHierarchyToRemove(SpincastConfig.class);

        guiceTweaker.overridingModule(new SpincastGuiceModuleBase() {

            @Override
            protected void configure() {
                bind(configImplClass).in(Scopes.SINGLETON);
                bind(SpincastConfig.class).to(configImplClass).in(Scopes.SINGLETON);
            }
        });

        //==========================================
        // Extra Module to add?
        //==========================================
        if (getExtraOverridingModule() != null) {
            guiceTweaker.overridingModule(getExtraOverridingModule());
        }

        //==========================================
        // Disable bind current class?
        //==========================================
        if (isDisableBindCurrentClass()) {
            guiceTweaker.disableBindCurrentClass();
        }

        return guiceTweaker;
    }

    protected boolean isDisableBindCurrentClass() {
        return false;
    }

    /**
     * Extra plugins to be added by the Guice Tweaker.
     * <p>
     * Most of the time, you want to make sure you
     * keep the plugins already added by base classes.
     * For example :
     * <p>
     * <code>
     * List&lt;SpincastPlugin&gt; plugins = super.getGuiceTweakerPlugins();
     * 
     * plugins.add(new YourPlugin());
     * 
     * return plugins;
     * </code>
     */
    protected List<SpincastPlugin> getExtraPlugins() {
        // None by default.
        return new ArrayList<SpincastPlugin>();
    }

    /**
     * If an overriding Module is to be added using the
     * Guice tweaker.
     */
    protected Module getExtraOverridingModule() {

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

    @Before
    public void beforeTest() {
        // nothing by default
    }

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
            this.logger.warn(ex.getMessage());
        }

        deleteTempDir();
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

    protected Injector getInjector() {
        return this.guice;
    }

    protected void deleteTempDir() {
        try {
            if (this.testingWritableDir != null) {
                FileUtils.deleteDirectory(this.testingWritableDir);
            }
        } catch (Exception ex) {
            System.err.println(ex);
        }
    }

    protected File getTestingWritableDir() {
        if (this.testingWritableDir == null) {

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

            this.testingWritableDir = new File(baseDir, "/spincast/testing");
            if (!this.testingWritableDir.isDirectory()) {
                boolean mkdirs = this.testingWritableDir.mkdirs();
                assertTrue(mkdirs);
            }
            assertTrue(this.testingWritableDir.canWrite());
        }
        return this.testingWritableDir;
    }

    protected SpincastConfig getSpincastConfig() {
        return this.spincastConfig;
    }

    /**
     * Create a temporary test file, using the given relative path.
     */
    protected String createTestingFilePath(String relativePath) {
        return getTestingWritableDir().getAbsolutePath() + "/" + relativePath;
    }

    /**
     * Create a temporary test file.
     */
    protected String createTestingFilePath() {
        return createTestingFilePath(UUID.randomUUID().toString());
    }

    /**
     * The test class must implement this method to create
     * the Guice injector. It can be done by starting a real
     * application (with a <code>main(...)</code> method) or by 
     * creating a custom Injector.
     */
    protected abstract Injector createInjector();

}
