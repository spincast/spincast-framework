package org.spincast.plugins.httpclient.builders;

import org.spincast.core.cookies.ICookieFactory;
import org.spincast.plugins.httpclient.IHttpRequestBuilder;
import org.spincast.plugins.httpclient.IHttpResponseFactory;
import org.spincast.plugins.httpclient.ISpincastHttpClientConfig;
import org.spincast.plugins.httpclient.utils.ISpincastHttpClientUtils;

public abstract class HttpSpincastRequestBuilderBase<T extends IHttpRequestBuilder<?>>
                                                    extends SpincastHttpRequestBuilderBase<T>
                                                    implements IHttpRequestBuilder<T> {

    public HttpSpincastRequestBuilderBase(String url, ICookieFactory cookieFactory,
                                          IHttpResponseFactory spincastHttpResponseFactory,
                                          ISpincastHttpClientUtils spincastHttpClientUtils,
                                          ISpincastHttpClientConfig spincastHttpClientConfig) {
        super(url, cookieFactory, spincastHttpResponseFactory, spincastHttpClientUtils, spincastHttpClientConfig);
    }

}
