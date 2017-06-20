package org.spincast.testing.core;

import static org.junit.Assert.assertNotNull;

import org.spincast.core.config.SpincastConfig;
import org.spincast.core.exchange.RequestContext;
import org.spincast.core.guice.GuiceTweaker;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.websocket.WebsocketContext;

import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Scopes;
import com.google.inject.util.Modules;

/**
 * Base for integration test classes that use an existing
 * Application to start the Server (calling its <code>main()</code>
 * method).
 * <p>
 * For tests using this as a base, a {@link GuiceTweaker} mecanism is
 * available. This allows you to tweak the bindings of the application
 * you are about to test. Doing so, you can start your application using
 * its real <code>main()</code> method. Note that the GuuiceTweaker only
 * works with applications started using the standard Bootstrapper
 * (<code>Spincast.configure(...)</code>).
 * <p>
 * This class needs to be parametrized with the <code>Request context</code> type
 * and <code>WebSocket Context</code> type to use.
 */
public abstract class IntegrationTestAppBase<R extends RequestContext<?>, W extends WebsocketContext<?>>
                                            extends IntegrationTestBase<R, W> {


    /**
     * When a Spincast application is tested, the default testing
     * configurations are always enabled, and {@link #getAppTestingConfigInfo()}
     * must be used to configure them.
     */
    @Override
    protected final boolean isGuiceTweakerAutoTestingConfigBindings() {

        AppTestingConfigInfo info = getAppTestingConfigInfo();
        if (info == null || info.getSpincastConfigTestingImplementationClass() == null) {
            return false;
        }
        return true;
    }

    /**
     * Final : the implementation class will be taken from the
     * {@link #getAppTestingConfigInfo()} return.
     */
    @Override
    protected final Class<? extends SpincastConfig> getGuiceTweakerConfigImplementationClass() {
        return getAppTestingConfigInfo().getSpincastConfigTestingImplementationClass();
    }

    @Override
    protected Module getGuiceTweakerOverridingModule() {

        //==========================================
        // If required, we bind the app testing configurations.
        //==========================================
        if (!isGuiceTweakerAutoTestingConfigBindings() || (getAppTestingConfigInfo().getAppConfigInterface() == null &&
                                                           getAppTestingConfigInfo().getAppConfigTestingImplementationClass() == null)) {
            return super.getGuiceTweakerOverridingModule();
        }

        final AppTestingConfigInfo testingConfigs = getAppTestingConfigInfo();

        if (testingConfigs.getAppConfigTestingImplementationClass() == null) {
            throw new RuntimeException("The testing app configuration implementation " +
                                       "can't be null for the the specified interface " +
                                       testingConfigs.getAppConfigInterface().getName());
        }

        //==========================================
        // Validation
        //==========================================
        if (testingConfigs.getAppConfigInterface() != null &&
            !testingConfigs.getAppConfigInterface().isAssignableFrom(testingConfigs.getAppConfigTestingImplementationClass())) {

            throw new RuntimeException("The testing app configuration implementation \"" +
                                       testingConfigs.getAppConfigTestingImplementationClass() +
                                       "\" doesn't implement the specified interface " +
                                       testingConfigs.getAppConfigInterface().getName());
        }

        return Modules.override(super.getGuiceTweakerOverridingModule())
                      .with(new SpincastGuiceModuleBase() {

                          @SuppressWarnings({"unchecked", "rawtypes"})
                          @Override
                          protected void configure() {

                              bind(testingConfigs.getAppConfigTestingImplementationClass()).in(Scopes.SINGLETON);

                              if (testingConfigs.getAppConfigInterface() != null) {
                                  bind(testingConfigs.getAppConfigInterface()).to((Class)testingConfigs.getAppConfigTestingImplementationClass())
                                                                              .in(Scopes.SINGLETON);
                              }

                          }
                      });
    }

    /**
     * We force test classes to provide information about
     * the required testing configurations.
     * <p>
     * The bindings for those components will be automatically
     * created.
     * 
     * @return the testing configs informations or <code>null</code>
     * to disable this process (you will then have to add the
     * required config bindings by yourself).
     */
    protected abstract AppTestingConfigInfo getAppTestingConfigInfo();

    /**
     * At this level, <code>createInjector()</code> expects
     * your application to use the <code>Spincast</code> utility class
     * from the <code>spincast-default</code> artifact to initialize 
     * your app. It is going to use a
     * {@link GuiceTweaker} ThreadLocal variable to
     * tweak how the Guice context is created and to retrieve
     * the resulting Guice injector.
     * <p>
     * If your application doesn't use the <code>Spincast</code> utility class
     * from the <code>spincast-default</code> artifact to initialize 
     * your app, you should probably override this method and it's up to you to
     * return the proper Guice Injector to use.
     */
    @Override
    protected Injector createInjector() {

        //==========================================
        // Starts the app!
        //==========================================
        initApp();

        //==========================================
        // The Guice injector should now have been added
        // to the SpincastPluginThreadLocal...
        //==========================================
        Injector injector = getGuiceTweakerFromThreadLocal().getInjector();
        assertNotNull(injector);

        return injector;
    }

    /**
     * Starts the application.
     */
    protected abstract void initApp();

}
