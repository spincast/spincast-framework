package org.spincast.testing.core;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;
import org.junit.runners.MethodSorters;
import org.spincast.core.config.SpincastConfig;
import org.spincast.core.guice.GuiceModuleUtils;
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
 * The NAME_ASCENDING option is used to sort the tests. This means you
 * can force the order in which tests are run by prefixeing them 
 * with something like : "t01_firstTest", "t02_secondTest", etc.
 * <p>
 * A {@link GuiceTweaker} instance is used to
 * be able to tweak a Guice context automagaically. This for example
 * allows you to start your actual application, using its main() method,
 * but still be able to mock some components for testing purposes. A
 * Guice tweaker is enabled by default if your test class extend this
 * base class. If {@link SpincastConfig} is bound, its method will be automatically
 * intercepted and testing values are going to be used instead (by default using the
 * {@link SpincastConfigTestingDefault} class).
 * <p>
 * Note that the Guice tweaker only works when the Guice context is created
 * using the <code>Spincast</code> class or the <code>SpincastBootstrapper</code>
 * classes from the <code>spincast-default</code> artifact.
 * 
 */
@RunWith(SpincastJUnitRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public abstract class SpincastTestBase implements BeforeAfterClassMethodsProvider,
                                       TestFailureListener,
                                       RepeatedClassAfterMethodProvider {

    private Injector guice;
    private File testingWritableDir;
    private SpincastConfig spincastConfig;
    private GuiceTweaker previousGuiceTweaker;

    /**
     * Should a GuiceTweaker ThreadLocal be
     * created?
     * <p>
     * Is <code>true</code> by default.
     */
    protected boolean isEnableGuiceTweaker() {
        return true;
    }

    /**
     * If <code>true</code>, a AOP interceptor will be
     * bound so a call to any of any of {@link SpincastConfig}'s
     * method will be intercepted and will use the
     * methods from the <em>testing</em> config instead, 
     * as returned by {@link #getSpincastConfigTestingImplementation()}.
     * <p>
     * Is <code>true</code> by default.
     */
    protected boolean isEnableGuiceTweakerTestingConfigMecanism() {
        return true;
    }

    /**
     * Should we add the plugings n general required during
     * testing?
     * <p>
     * Is <code>true</code> by default.
     */
    protected boolean isEnableGuiceTweakerExtraPlugins() {
        return true;
    }

    @Override
    public void beforeClass() {

        //==========================================
        // Creates a GuiceTweaker 
        // as a ThreadLocal variable to tweak some 
        // bindings during the Injector creation, if
        // "Spincast.configure()" if used to create that
        // Injector.
        //
        // If you build the Injector by yourself, this
        // tweaker won't have any effect.
        //==========================================
        boolean useGuiceTweaker = isEnableGuiceTweaker();
        if (useGuiceTweaker) {
            this.previousGuiceTweaker = GuiceTweaker.threadLocal.get();
            GuiceTweaker.threadLocal.set(createGuiceTweaker());
        }
        try {
            this.guice = createInjector();
        } finally {
            if (useGuiceTweaker) {
                if (this.previousGuiceTweaker != null) {
                    GuiceTweaker.threadLocal.set(this.previousGuiceTweaker);
                } else {
                    GuiceTweaker.threadLocal.remove();
                }
            }
        }

        assertNotNull(this.guice);
        this.guice.injectMembers(this);
    }

    /**
     * Create the Guice Tweaker.
     * <p>
     * This is only useful when the Guice Injector is created
     * starting with <code>Spincast.configure(...)</code>. If
     * you create the Guice Injector by yourself, using 
     * <code>Guice.createInjector(...)</code>, this won't
     * have any effect.
     */
    protected GuiceTweaker createGuiceTweaker() {

        GuiceTweaker guiceTweaker = new GuiceTweaker();

        if (isEnableGuiceTweakerTestingConfigMecanism()) {
            setupSpincastConfigTesting(guiceTweaker);
        }

        if (isEnableGuiceTweakerExtraPlugins()) {
            List<SpincastPlugin> plugins = getGuiceTweakerExtraPlugins();
            if (plugins != null) {
                for (SpincastPlugin plugin : plugins) {
                    guiceTweaker.plugin(plugin);
                }
            }
        }
        return guiceTweaker;
    }

    /**
     * The extra plugins added by the Guice Tweaker.
     */
    protected List<SpincastPlugin> getGuiceTweakerExtraPlugins() {

        // None by default.
        List<SpincastPlugin> plugins = new ArrayList<SpincastPlugin>();
        return plugins;
    }

    /**
     * The testing configuration class implementation to use. You can
     * override {@link #isEnableGuiceTweakerTestingConfigMecanism()} if you
     * want to disable this class from being used.
     */
    protected Class<? extends SpincastConfigTesting> getSpincastConfigTestingImplementation() {
        return SpincastConfigTestingDefault.class;
    }

    /**
     * Adds an OAP interceptor so calls to methods of the original
     * {@link SpincastConfig} onject will be redirected to the
     * <em>testing</em> version (as returned by {@link #getSpincastConfigTestingImplementation()}).
     */
    protected void setupSpincastConfigTesting(GuiceTweaker guiceTweaker) {

        final Class<? extends SpincastConfigTesting> testingConfigClass = getSpincastConfigTestingImplementation();
        if (testingConfigClass == null) {
            return;
        }

        //==========================================
        // Binds the SpincastConfigTesting key to
        // the config implementation
        //==========================================
        guiceTweaker.module(new SpincastGuiceModuleBase() {

            @Override
            protected void configure() {
                bind(SpincastConfigTesting.class).to(testingConfigClass)
                                                 .in(Scopes.SINGLETON);

            }
        });

        //==========================================
        // "Binds" the SpincastConfig key to
        // the testing implementation using AOP.
        //==========================================
        SpincastGuiceModuleBase interceptModule =
                GuiceModuleUtils.createInterceptorModule(SpincastConfig.class, testingConfigClass);
        guiceTweaker.module(interceptModule);
    }

    protected GuiceTweaker getSpincastPluginFromThreadLocal() {
        return GuiceTweaker.threadLocal.get();
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
        deleteTempDir();
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
        // nothing by default
    }

    @Inject
    protected void setSpincastConfig(SpincastConfig spincastConfig) {
        this.spincastConfig = spincastConfig;
    }

    protected SpincastConfig getSpincastConfig() {
        return this.spincastConfig;
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
