package org.spincast.plugins.httpclient.builders;

import org.spincast.core.cookies.ICookieFactory;
import org.spincast.plugins.httpclient.IHttpResponseFactory;
import org.spincast.plugins.httpclient.ISpincastHttpClientConfig;
import org.spincast.plugins.httpclient.utils.ISpincastHttpClientUtils;
import org.spincast.shaded.org.apache.http.client.methods.HttpRequestBase;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

/**
 * Implementation for the Http Client builder for a CONNECT request.
 */
public class ConnectRequestBuilder extends SpincastHttpRequestBuilderBase<IConnectRequestBuilder>
                                   implements IConnectRequestBuilder {

    @AssistedInject
    public ConnectRequestBuilder(@Assisted String url,
                                 ICookieFactory cookieFactory,
                                 IHttpResponseFactory spincastHttpResponseFactory,
                                 ISpincastHttpClientUtils spincastHttpClientUtils,
                                 ISpincastHttpClientConfig spincastHttpClientConfig) {
        super(url, cookieFactory, spincastHttpResponseFactory, spincastHttpClientUtils, spincastHttpClientConfig);
    }

    @Override
    protected HttpRequestBase createMethodSpecificHttpRequest(String url) {
        throw new RuntimeException("'Connect' method is not available using Apache Common's HttpClient!");
    }

}
