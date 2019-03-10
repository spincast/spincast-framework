package org.spincast.testing.defaults;

import java.util.List;

import org.spincast.core.exchange.RequestContext;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.guice.SpincastPlugin;
import org.spincast.core.websocket.WebsocketContext;
import org.spincast.defaults.bootstrapping.Spincast;
import org.spincast.testing.core.AppBasedTestingBase;
import org.spincast.testing.core.SpincastTestBase;

import com.google.common.collect.Lists;
import com.google.inject.Injector;
import com.google.inject.Module;

public abstract class NoAppCustomContextTypesTestingBase<R extends RequestContext<?>, W extends WebsocketContext<?>>
                                                        extends SpincastTestBase {

    @Override
    protected Injector createInjector() {

        return Spincast.configure()
                       .bindCurrentClass(false)
                       .requestContextImplementationClass(getRequestContextImplementationClass())
                       .websocketContextImplementationClass(getWebsocketContextImplementationClass())
                       .init(getMainArgs());
    }

    protected String[] getMainArgs() {
        return null;
    }

    /**
     * We make this final to stay consistent with the
     * {@link AppBasedTestingBase} testing hierarchy:
     * to add extra plugins, you also need to override the
     * {@link #getExtraPlugins()} method here...
     */
    @Override
    protected final List<SpincastPlugin> getGuiceTweakerExtraPlugins() {
        return getExtraPlugins();
    }

    /**
     * The extra required plugins.
     * Example:
     * <pre>
     * List&lt;SpincastPlugin&gt; extraPlugins = super.getExtraPlugins();
     * extraPlugins.add(new XXX());
     * return extraPlugins;
     * </pre>
     */
    protected List<SpincastPlugin> getExtraPlugins() {
        return Lists.newArrayList();
    }

    /**
     * We make this final to stay consistent with the
     * {@link AppBasedTestingBase} testing hierarchy:
     * to add an extra Module, you also need to override the
     * {@link #getExtraOverridingModule()} method here...
     */
    @Override
    protected final Module getGuiceTweakerExtraOverridingModule() {
        return getExtraOverridingModule();
    }

    /**
     * Can be overriden with something like :
     * 
     * <pre>
     * return Modules.override(super.getExtraOverridingModule()).with(new SpincastGuiceModuleBase() {
     *     protected void configure() {
     *         // ...
     *     }
     * });
     * </pre>
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


    protected abstract Class<? extends RequestContext<?>> getRequestContextImplementationClass();

    protected abstract Class<? extends WebsocketContext<?>> getWebsocketContextImplementationClass();

}
