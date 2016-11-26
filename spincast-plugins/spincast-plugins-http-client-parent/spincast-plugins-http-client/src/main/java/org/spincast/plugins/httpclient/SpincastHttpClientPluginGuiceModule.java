package org.spincast.plugins.httpclient;

import java.lang.reflect.Type;

import org.spincast.core.guice.SpincastPluginGuiceModuleBase;
import org.spincast.plugins.httpclient.builders.ConnectRequestBuilderDefault;
import org.spincast.plugins.httpclient.builders.DeleteRequestBuilderDefault;
import org.spincast.plugins.httpclient.builders.GetRequestBuilderDefault;
import org.spincast.plugins.httpclient.builders.HeadRequestBuilderDefault;
import org.spincast.plugins.httpclient.builders.ConnectRequestBuilder;
import org.spincast.plugins.httpclient.builders.DeleteRequestBuilder;
import org.spincast.plugins.httpclient.builders.GetRequestBuilder;
import org.spincast.plugins.httpclient.builders.HeadRequestBuilder;
import org.spincast.plugins.httpclient.builders.OptionsRequestBuilder;
import org.spincast.plugins.httpclient.builders.PatchRequestBuilder;
import org.spincast.plugins.httpclient.builders.PostRequestBuilder;
import org.spincast.plugins.httpclient.builders.PutRequestBuilder;
import org.spincast.plugins.httpclient.builders.TraceRequestBuilder;
import org.spincast.plugins.httpclient.builders.OptionsRequestBuilderDefault;
import org.spincast.plugins.httpclient.builders.PatchRequestBuilderDefault;
import org.spincast.plugins.httpclient.builders.PostRequestBuilderDefault;
import org.spincast.plugins.httpclient.builders.PutRequestBuilderDefault;
import org.spincast.plugins.httpclient.builders.TraceRequestBuilderDefault;
import org.spincast.plugins.httpclient.utils.SpincastHttpClientUtils;
import org.spincast.plugins.httpclient.utils.SpincastHttpClientUtilsDefault;

import com.google.inject.Scopes;
import com.google.inject.assistedinject.FactoryModuleBuilder;

/**
 * Guice module for the Spincast Http Client plugin.
 */
public class SpincastHttpClientPluginGuiceModule extends SpincastPluginGuiceModuleBase {

    /**
     * Constructor.
     */
    public SpincastHttpClientPluginGuiceModule(Type requestContextType, Type websocketContextType) {
        super(requestContextType, websocketContextType);
    }

    @Override
    protected void configure() {
        bindHttpClientFactory();
        bindHttpResponseFactory();
        bindSpincastHttpClientUtils();
    }

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
                                          .build(HttpClient.class));
    }

    protected void bindHttpResponseFactory() {
        install(new FactoryModuleBuilder().implement(HttpResponse.class, HttpResponseDefault.class)
                                          .build(HttpResponseFactory.class));
    }

    protected void bindSpincastHttpClientUtils() {
        bind(SpincastHttpClientUtils.class).to(getSpincastHttpClientUtilsClass()).in(Scopes.SINGLETON);
    }

    protected Class<? extends SpincastHttpClientUtils> getSpincastHttpClientUtilsClass() {
        return SpincastHttpClientUtilsDefault.class;
    }

}
