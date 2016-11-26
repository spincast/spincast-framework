package org.spincast.plugins.undertow;

import java.lang.reflect.Type;

import org.spincast.core.guice.SpincastPluginGuiceModuleBase;
import org.spincast.core.server.Server;

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

        bind(Server.class).to(getSpincastUndertowServerClass()).in(Scopes.SINGLETON);
        bindSpincastUndertowUtils();
        bindCorsHandlerFactory();
        bindGzipCheckerHandlerFactory();
        bindSpincastResourceHandlerFactory();
        bindCacheBusterRemovalHandlerFactory();
        bindFileClassPathResourceManagerFactory();
        bindHttpAuthIdentityManagerFactory();
        bindUndertowWebsocketEndpointWriterFactory();
        bindWebsocketEndpointFactory();
    }

    protected void bindSpincastUndertowUtils() {
        bind(SpincastUndertowUtils.class).to(getSpincastUndertowUtilsClass()).in(Scopes.SINGLETON);
    }

    protected Class<? extends SpincastUndertowUtils> getSpincastUndertowUtilsClass() {
        return SpincastUndertowUtilsDefault.class;
    }

    protected Class<? extends Server> getSpincastUndertowServerClass() {
        return SpincastUndertowServer.class;
    }

    protected void bindCorsHandlerFactory() {

        install(new FactoryModuleBuilder().implement(CorsHandler.class, getCorsHandlerClass())
                                          .build(CorsHandlerFactory.class));
    }

    protected Class<? extends CorsHandler> getCorsHandlerClass() {
        return CorsHandlerDefault.class;
    }

    protected void bindGzipCheckerHandlerFactory() {
        install(new FactoryModuleBuilder().implement(GzipCheckerHandler.class, getGzipCheckerHandlerClass())
                                          .build(GzipCheckerHandlerFactory.class));
    }

    protected Class<? extends GzipCheckerHandler> getGzipCheckerHandlerClass() {
        return GzipCheckerHandlerDefault.class;
    }

    protected void bindSpincastResourceHandlerFactory() {
        install(new FactoryModuleBuilder().implement(SpincastResourceHandler.class, getSpincastResourceHandlerClass())
                                          .build(SpincastResourceHandlerFactory.class));
    }

    protected Class<? extends SpincastResourceHandler> getSpincastResourceHandlerClass() {
        return SpincastResourceHandlerDefault.class;
    }

    protected void bindCacheBusterRemovalHandlerFactory() {
        install(new FactoryModuleBuilder().implement(CacheBusterRemovalHandler.class, getCacheBusterRemovalHandlerClass())
                                          .build(CacheBusterRemovalHandlerFactory.class));
    }

    protected Class<? extends CacheBusterRemovalHandler> getCacheBusterRemovalHandlerClass() {
        return CacheBusterRemovalHandlerDefault.class;
    }

    protected void bindFileClassPathResourceManagerFactory() {
        install(new FactoryModuleBuilder().implement(FileClassPathResourceManager.class, getFileClassPathResourceManagerClass())
                                          .build(FileClassPathResourceManagerFactory.class));
    }

    protected Class<? extends FileClassPathResourceManager> getFileClassPathResourceManagerClass() {
        return FileClassPathResourceManagerDefault.class;
    }

    protected void bindHttpAuthIdentityManagerFactory() {
        install(new FactoryModuleBuilder().implement(SpincastHttpAuthIdentityManager.class,
                                                     getSpincastHttpAuthIdentityManagerClass())
                                          .build(SpincastHttpAuthIdentityManagerFactory.class));
    }

    protected Class<? extends SpincastHttpAuthIdentityManager> getSpincastHttpAuthIdentityManagerClass() {
        return SpincastHttpAuthIdentityManagerDefault.class;
    }

    protected void bindUndertowWebsocketEndpointWriterFactory() {
        install(new FactoryModuleBuilder().implement(UndertowWebsocketEndpointWriter.class,
                                                     getUndertowWebsocketEndpointWriterClass())
                                          .build(UndertowWebsocketEndpointWriterFactory.class));
    }

    protected Class<? extends UndertowWebsocketEndpointWriter> getUndertowWebsocketEndpointWriterClass() {
        return SpincastUndertowWebsocketEndpointWriter.class;
    }

    protected void bindWebsocketEndpointFactory() {
        install(new FactoryModuleBuilder().implement(WebsocketEndpoint.class,
                                                     getWebsocketEndpointClass())
                                          .build(WebsocketEndpointFactory.class));
    }

    protected Class<? extends WebsocketEndpoint> getWebsocketEndpointClass() {
        return SpincastWebsocketEndpoint.class;
    }

}
