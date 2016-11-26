package org.spincast.plugins.httpclient.builders;

import org.spincast.core.cookies.CookieFactory;
import org.spincast.plugins.httpclient.HttpRequestBuilder;
import org.spincast.plugins.httpclient.HttpResponseFactory;
import org.spincast.plugins.httpclient.SpincastHttpClientConfig;
import org.spincast.plugins.httpclient.utils.SpincastHttpClientUtils;

public abstract class HttpSpincastRequestBuilderBase<T extends HttpRequestBuilder<?>>
                                                    extends HttpRequestBuilderBase<T>
                                                    implements HttpRequestBuilder<T> {

    public HttpSpincastRequestBuilderBase(String url, CookieFactory cookieFactory,
                                          HttpResponseFactory spincastHttpResponseFactory,
                                          SpincastHttpClientUtils spincastHttpClientUtils,
                                          SpincastHttpClientConfig spincastHttpClientConfig) {
        super(url, cookieFactory, spincastHttpResponseFactory, spincastHttpClientUtils, spincastHttpClientConfig);
    }

}
