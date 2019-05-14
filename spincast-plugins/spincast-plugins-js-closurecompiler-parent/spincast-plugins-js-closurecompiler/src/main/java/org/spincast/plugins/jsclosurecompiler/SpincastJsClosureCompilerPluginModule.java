package org.spincast.plugins.jsclosurecompiler;

import org.spincast.core.exchange.RequestContext;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.websocket.WebsocketContext;
import org.spincast.plugins.jsclosurecompiler.config.SpincastJsClosureCompilerConfig;
import org.spincast.plugins.jsclosurecompiler.config.SpincastJsClosureCompilerConfigDefault;

import com.google.inject.Scopes;
import com.google.inject.multibindings.Multibinder;
import com.mitchellbosecke.pebble.extension.Extension;

/**
 * Spincast JS Closure Compiler plugin module.
 */
public class SpincastJsClosureCompilerPluginModule extends SpincastGuiceModuleBase {

    public SpincastJsClosureCompilerPluginModule() {
        super();
    }

    public SpincastJsClosureCompilerPluginModule(Class<? extends RequestContext<?>> requestContextImplementationClass,
                                                 Class<? extends WebsocketContext<?>> websocketContextImplementationClass) {
        super(requestContextImplementationClass, websocketContextImplementationClass);
    }

    @Override
    protected void configure() {
        bind(SpincastJsClosureCompilerManager.class).to(getSpincastJsCLosureCompilerManagerImpl()).in(Scopes.SINGLETON);
        bind(SpincastJsClosureCompilerConfig.class).to(getSpincastJsClosureCompilerConfigImpl()).in(Scopes.SINGLETON);

        bindPebbleExtension();
    }

    protected Class<? extends SpincastJsClosureCompilerManager> getSpincastJsCLosureCompilerManagerImpl() {
        return SpincastJsClosureCompilerManagerDefault.class;
    }

    protected Class<? extends SpincastJsClosureCompilerConfig> getSpincastJsClosureCompilerConfigImpl() {
        return SpincastJsClosureCompilerConfigDefault.class;
    }

    protected void bindPebbleExtension() {
        Multibinder<Extension> pebbleExtensionsMultibinder = Multibinder.newSetBinder(binder(), Extension.class);
        pebbleExtensionsMultibinder.addBinding().to(SpincastJsClosureCompilerPebbleExtension.class).in(Scopes.SINGLETON);
    }

}
