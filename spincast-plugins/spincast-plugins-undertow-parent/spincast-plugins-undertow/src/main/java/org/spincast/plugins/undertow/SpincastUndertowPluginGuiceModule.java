package org.spincast.plugins.undertow;

import java.lang.reflect.Type;

import org.spincast.core.guice.SpincastPluginGuiceModuleBase;
import org.spincast.core.server.IServer;

import com.google.inject.Scopes;
import com.google.inject.assistedinject.FactoryModuleBuilder;

public class SpincastUndertowPluginGuiceModule extends SpincastPluginGuiceModuleBase {

    /**
     * Constructor.
     */
    public SpincastUndertowPluginGuiceModule(Type requestContextType,
                                             Type websocketContextType) {
        super(requestContextType, websocketContextType);
    }

    @Override
    protected void configure() {

        bind(IServer.class).to(getSpincastUndertowServerClass()).in(Scopes.SINGLETON);
        bindSpincastUndertowUtils();
        bindCorsHandlerFactory();
        bindGzipCheckerHandlerFactory();
        bindFileClassPathResourceManagerFactory();
        bindHttpAuthIdentityManagerFactory();
        bindUndertowWebsocketEndpointWriterFactory();
        bindWebsocketEndpointFactory();
    }

    protected void bindSpincastUndertowUtils() {
        bind(ISpincastUndertowUtils.class).to(getSpincastUndertowUtilsClass()).in(Scopes.SINGLETON);
    }

    protected Class<? extends ISpincastUndertowUtils> getSpincastUndertowUtilsClass() {
        return SpincastUndertowUtils.class;
    }

    protected Class<? extends IServer> getSpincastUndertowServerClass() {
        return SpincastUndertowServer.class;
    }

    protected void bindCorsHandlerFactory() {

        install(new FactoryModuleBuilder().implement(ICorsHandler.class, getCorsHandlerClass())
                                          .build(ICorsHandlerFactory.class));
    }

    protected Class<? extends ICorsHandler> getCorsHandlerClass() {
        return CorsHandler.class;
    }

    protected void bindGzipCheckerHandlerFactory() {
        install(new FactoryModuleBuilder().implement(IGzipCheckerHandler.class, getGzipCheckerHandlerClass())
                                          .build(IGzipCheckerHandlerFactory.class));
    }

    protected Class<? extends IGzipCheckerHandler> getGzipCheckerHandlerClass() {
        return GzipCheckerHandler.class;
    }

    protected void bindFileClassPathResourceManagerFactory() {
        install(new FactoryModuleBuilder().implement(IFileClassPathResourceManager.class, getFileClassPathResourceManagerClass())
                                          .build(IFileClassPathResourceManagerFactory.class));
    }

    protected Class<? extends IFileClassPathResourceManager> getFileClassPathResourceManagerClass() {
        return FileClassPathResourceManager.class;
    }

    protected void bindHttpAuthIdentityManagerFactory() {
        install(new FactoryModuleBuilder().implement(ISpincastHttpAuthIdentityManager.class,
                                                     getSpincastHttpAuthIdentityManagerClass())
                                          .build(ISpincastHttpAuthIdentityManagerFactory.class));
    }

    protected Class<? extends ISpincastHttpAuthIdentityManager> getSpincastHttpAuthIdentityManagerClass() {
        return SpincastHttpAuthIdentityManager.class;
    }

    protected void bindUndertowWebsocketEndpointWriterFactory() {
        install(new FactoryModuleBuilder().implement(IUndertowWebsocketEndpointWriter.class,
                                                     getUndertowWebsocketEndpointWriterClass())
                                          .build(IUndertowWebsocketEndpointWriterFactory.class));
    }

    protected Class<? extends IUndertowWebsocketEndpointWriter> getUndertowWebsocketEndpointWriterClass() {
        return SpincastUndertowWebsocketEndpointWriter.class;
    }

    protected void bindWebsocketEndpointFactory() {
        install(new FactoryModuleBuilder().implement(IWebsocketEndpoint.class,
                                                     getWebsocketEndpointClass())
                                          .build(IWebsocketEndpointFactory.class));
    }

    protected Class<? extends IWebsocketEndpoint> getWebsocketEndpointClass() {
        return SpincastWebsocketEndpoint.class;
    }

}
