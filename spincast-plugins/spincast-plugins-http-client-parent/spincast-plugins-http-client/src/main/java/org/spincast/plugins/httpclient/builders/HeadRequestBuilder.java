package org.spincast.plugins.httpclient.builders;

import org.spincast.core.cookies.ICookieFactory;
import org.spincast.plugins.httpclient.IHttpResponseFactory;
import org.spincast.plugins.httpclient.ISpincastHttpClientConfig;
import org.spincast.plugins.httpclient.utils.ISpincastHttpClientUtils;
import org.spincast.shaded.org.apache.http.client.methods.HttpHead;
import org.spincast.shaded.org.apache.http.client.methods.HttpRequestBase;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

/**
 * Implementation for the Http Client builder for a HEAD request.
 */
public class HeadRequestBuilder extends SpincastHttpRequestBuilderBase<IHeadRequestBuilder>
                                implements IHeadRequestBuilder {

    @AssistedInject
    public HeadRequestBuilder(@Assisted String url,
                              ICookieFactory cookieFactory,
                              IHttpResponseFactory spincastHttpResponseFactory,
                              ISpincastHttpClientUtils spincastHttpClientUtils,
                              ISpincastHttpClientConfig spincastHttpClientConfig) {
        super(url, cookieFactory, spincastHttpResponseFactory, spincastHttpClientUtils, spincastHttpClientConfig);
    }

    @Override
    protected HttpRequestBase createMethodSpecificHttpRequest(String url) {
        return new HttpHead(url);
    }

}
