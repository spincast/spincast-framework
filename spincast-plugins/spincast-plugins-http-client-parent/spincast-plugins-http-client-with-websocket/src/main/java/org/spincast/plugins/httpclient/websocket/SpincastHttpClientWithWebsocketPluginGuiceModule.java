package org.spincast.plugins.httpclient.websocket;

import java.lang.reflect.Type;

import org.spincast.plugins.httpclient.SpincastHttpClientPluginGuiceModule;
import org.spincast.plugins.httpclient.builders.ConnectRequestBuilder;
import org.spincast.plugins.httpclient.builders.DeleteRequestBuilder;
import org.spincast.plugins.httpclient.builders.GetRequestBuilder;
import org.spincast.plugins.httpclient.builders.HeadRequestBuilder;
import org.spincast.plugins.httpclient.builders.IConnectRequestBuilder;
import org.spincast.plugins.httpclient.builders.IDeleteRequestBuilder;
import org.spincast.plugins.httpclient.builders.IGetRequestBuilder;
import org.spincast.plugins.httpclient.builders.IHeadRequestBuilder;
import org.spincast.plugins.httpclient.builders.IOptionsRequestBuilder;
import org.spincast.plugins.httpclient.builders.IPatchRequestBuilder;
import org.spincast.plugins.httpclient.builders.IPostRequestBuilder;
import org.spincast.plugins.httpclient.builders.IPutRequestBuilder;
import org.spincast.plugins.httpclient.builders.ITraceRequestBuilder;
import org.spincast.plugins.httpclient.builders.OptionsRequestBuilder;
import org.spincast.plugins.httpclient.builders.PatchRequestBuilder;
import org.spincast.plugins.httpclient.builders.PostRequestBuilder;
import org.spincast.plugins.httpclient.builders.PutRequestBuilder;
import org.spincast.plugins.httpclient.builders.TraceRequestBuilder;
import org.spincast.plugins.httpclient.utils.ISpincastHttpClientUtils;
import org.spincast.plugins.httpclient.websocket.builders.IWebsocketRequestBuilder;
import org.spincast.plugins.httpclient.websocket.builders.WebsocketRequestBuilder;
import org.spincast.plugins.httpclient.websocket.utils.ISpincastHttpClientWithWebsocketUtils;
import org.spincast.plugins.httpclient.websocket.utils.SpincastHttpClientWithWebsocketUtils;

import com.google.inject.Scopes;
import com.google.inject.assistedinject.FactoryModuleBuilder;

/**
 * Guice module for the Spincast Http Client with Websocket support plugin.
 */
public class SpincastHttpClientWithWebsocketPluginGuiceModule extends SpincastHttpClientPluginGuiceModule {

    /**
     * Constructor.
     */
    public SpincastHttpClientWithWebsocketPluginGuiceModule(Type requestContextType, Type websocketContextType) {
        super(requestContextType, websocketContextType);
    }

    @Override
    protected void configure() {
        super.configure();
        bindSpincastHttpClientWithWebsocketUtils();
    }

    /**
     * Override to bind IWebsocketRequestBuilder too. 
     */
    @Override
    protected void bindHttpClientFactory() {

        install(new FactoryModuleBuilder().implement(IGetRequestBuilder.class, GetRequestBuilder.class)
                                          .implement(IPostRequestBuilder.class, PostRequestBuilder.class)
                                          .implement(IPutRequestBuilder.class, PutRequestBuilder.class)
                                          .implement(IDeleteRequestBuilder.class, DeleteRequestBuilder.class)
                                          .implement(ITraceRequestBuilder.class, TraceRequestBuilder.class)
                                          .implement(IOptionsRequestBuilder.class, OptionsRequestBuilder.class)
                                          .implement(IHeadRequestBuilder.class, HeadRequestBuilder.class)
                                          .implement(IConnectRequestBuilder.class, ConnectRequestBuilder.class)
                                          .implement(IPatchRequestBuilder.class, PatchRequestBuilder.class)
                                          .implement(IWebsocketRequestBuilder.class, WebsocketRequestBuilder.class)
                                          .build(IHttpClient.class));
    }

    protected void bindSpincastHttpClientWithWebsocketUtils() {
        bind(ISpincastHttpClientWithWebsocketUtils.class).to(getSpincastHttpClientUtilsWithWebsocketClass()).in(Scopes.SINGLETON);
    }

    protected Class<? extends ISpincastHttpClientWithWebsocketUtils> getSpincastHttpClientUtilsWithWebsocketClass() {
        return SpincastHttpClientWithWebsocketUtils.class;
    }

    @Override
    protected Class<? extends ISpincastHttpClientUtils> getSpincastHttpClientUtilsClass() {
        return SpincastHttpClientWithWebsocketUtils.class;
    }

}
