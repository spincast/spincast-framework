package org.spincast.plugins.httpclient.builders;

import org.spincast.core.cookies.ICookieFactory;
import org.spincast.plugins.httpclient.IHttpResponseFactory;
import org.spincast.plugins.httpclient.ISpincastHttpClientConfig;
import org.spincast.plugins.httpclient.utils.ISpincastHttpClientUtils;
import org.spincast.shaded.org.apache.http.client.methods.HttpGet;
import org.spincast.shaded.org.apache.http.client.methods.HttpRequestBase;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

/**
 * Implementation for the Http Client builder for a GET request.
 */
public class GetRequestBuilder extends SpincastHttpRequestBuilderBase<IGetRequestBuilder>
                               implements IGetRequestBuilder {

    @AssistedInject
    public GetRequestBuilder(@Assisted String url,
                             ICookieFactory cookieFactory,
                             IHttpResponseFactory spincastHttpResponseFactory,
                             ISpincastHttpClientUtils spincastHttpClientUtils,
                             ISpincastHttpClientConfig spincastHttpClientConfig) {
        super(url, cookieFactory, spincastHttpResponseFactory, spincastHttpClientUtils, spincastHttpClientConfig);
    }

    @Override
    protected HttpRequestBase createMethodSpecificHttpRequest(String url) {
        return new HttpGet(url);
    }

}
