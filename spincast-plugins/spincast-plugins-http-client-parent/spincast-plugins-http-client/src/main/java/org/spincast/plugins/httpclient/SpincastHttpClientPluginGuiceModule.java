package org.spincast.plugins.httpclient;

import java.lang.reflect.Type;

import org.spincast.core.guice.SpincastGuiceModuleBase;
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

import com.google.inject.assistedinject.FactoryModuleBuilder;

/**
 * Guice module for the Spincast Http Client plugin.
 */
public class SpincastHttpClientPluginGuiceModule extends SpincastGuiceModuleBase {

    private final Type requestContextType;

    /**
     * Constructor.
     */
    public SpincastHttpClientPluginGuiceModule(Type requestContextType) {
        this.requestContextType = requestContextType;
    }

    @Override
    protected Type getRequestContextType() {
        return this.requestContextType;
    }

    @Override
    protected void configure() {
        bindHttpClientFactory();
        bindHttpResponseFactory();
    }

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
                                          .build(IHttpClient.class));
    }

    protected void bindHttpResponseFactory() {
        install(new FactoryModuleBuilder().implement(IHttpResponse.class, SpincastHttpResponse.class)
                                          .build(IHttpResponseFactory.class));
    }

}
