package org.spincast.plugins.httpclient.websocket;

import org.spincast.core.exchange.RequestContext;
import org.spincast.core.websocket.WebsocketContext;
import org.spincast.plugins.httpclient.SpincastHttpClientPluginModule;
import org.spincast.plugins.httpclient.builders.ConnectRequestBuilder;
import org.spincast.plugins.httpclient.builders.ConnectRequestBuilderDefault;
import org.spincast.plugins.httpclient.builders.DeleteRequestBuilder;
import org.spincast.plugins.httpclient.builders.DeleteRequestBuilderDefault;
import org.spincast.plugins.httpclient.builders.GetRequestBuilder;
import org.spincast.plugins.httpclient.builders.GetRequestBuilderDefault;
import org.spincast.plugins.httpclient.builders.HeadRequestBuilder;
import org.spincast.plugins.httpclient.builders.HeadRequestBuilderDefault;
import org.spincast.plugins.httpclient.builders.OptionsRequestBuilder;
import org.spincast.plugins.httpclient.builders.OptionsRequestBuilderDefault;
import org.spincast.plugins.httpclient.builders.PatchRequestBuilder;
import org.spincast.plugins.httpclient.builders.PatchRequestBuilderDefault;
import org.spincast.plugins.httpclient.builders.PostRequestBuilder;
import org.spincast.plugins.httpclient.builders.PostRequestBuilderDefault;
import org.spincast.plugins.httpclient.builders.PutRequestBuilder;
import org.spincast.plugins.httpclient.builders.PutRequestBuilderDefault;
import org.spincast.plugins.httpclient.builders.TraceRequestBuilder;
import org.spincast.plugins.httpclient.builders.TraceRequestBuilderDefault;
import org.spincast.plugins.httpclient.utils.SpincastHttpClientUtils;
import org.spincast.plugins.httpclient.websocket.builders.WebsocketRequestBuilder;
import org.spincast.plugins.httpclient.websocket.builders.WebsocketRequestBuilderDefault;
import org.spincast.plugins.httpclient.websocket.utils.SpincastHttpClientWithWebsocketUtils;
import org.spincast.plugins.httpclient.websocket.utils.SpincastHttpClientWithWebsocketUtilsDefault;

import com.google.inject.Scopes;
import com.google.inject.assistedinject.FactoryModuleBuilder;

/**
 * Guice module for the Spincast Http Client with Websocket support plugin.
 */
public class SpincastHttpClientWithWebsocketPluginModule extends SpincastHttpClientPluginModule {

    public SpincastHttpClientWithWebsocketPluginModule() {
        super();
    }

    public SpincastHttpClientWithWebsocketPluginModule(Class<? extends RequestContext<?>> requestContextImplementationClass,
                                                       Class<? extends WebsocketContext<?>> websocketContextImplementationClass) {
        super(requestContextImplementationClass, websocketContextImplementationClass);
    }

    @Override
    protected void configure() {
        super.configure();
        bindSpincastHttpClientWithWebsocketUtils();
    }

    /**
     * Override to bind WebsocketRequestBuilder too. 
     */
    @Override
    protected void bindHttpClientFactory() {

        install(new FactoryModuleBuilder().implement(GetRequestBuilder.class, GetRequestBuilderDefault.class)
                                          .implement(PostRequestBuilder.class, PostRequestBuilderDefault.class)
                                          .implement(PutRequestBuilder.class, PutRequestBuilderDefault.class)
                                          .implement(DeleteRequestBuilder.class, DeleteRequestBuilderDefault.class)
                                          .implement(TraceRequestBuilder.class, TraceRequestBuilderDefault.class)
                                          .implement(OptionsRequestBuilder.class, OptionsRequestBuilderDefault.class)
                                          .implement(HeadRequestBuilder.class, HeadRequestBuilderDefault.class)
                                          .implement(ConnectRequestBuilder.class, ConnectRequestBuilderDefault.class)
                                          .implement(PatchRequestBuilder.class, PatchRequestBuilderDefault.class)
                                          .implement(WebsocketRequestBuilder.class, WebsocketRequestBuilderDefault.class)
                                          .build(HttpClient.class));
    }

    protected void bindSpincastHttpClientWithWebsocketUtils() {
        bind(SpincastHttpClientWithWebsocketUtils.class).to(getSpincastHttpClientUtilsWithWebsocketClass()).in(Scopes.SINGLETON);
    }

    protected Class<? extends SpincastHttpClientWithWebsocketUtils> getSpincastHttpClientUtilsWithWebsocketClass() {
        return SpincastHttpClientWithWebsocketUtilsDefault.class;
    }

    @Override
    protected Class<? extends SpincastHttpClientUtils> getSpincastHttpClientUtilsClass() {
        return SpincastHttpClientWithWebsocketUtilsDefault.class;
    }

}
