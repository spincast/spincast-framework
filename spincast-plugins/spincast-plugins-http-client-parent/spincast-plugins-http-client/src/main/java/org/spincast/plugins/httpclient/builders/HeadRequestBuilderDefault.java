package org.spincast.plugins.httpclient.builders;

import org.spincast.core.cookies.CookieFactory;
import org.spincast.plugins.httpclient.HttpResponseFactory;
import org.spincast.plugins.httpclient.SpincastHttpClientConfig;
import org.spincast.plugins.httpclient.utils.SpincastHttpClientUtils;
import org.spincast.shaded.org.apache.http.client.methods.HttpHead;
import org.spincast.shaded.org.apache.http.client.methods.HttpRequestBase;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

/**
 * Implementation for the Http Client builder for a HEAD request.
 */
public class HeadRequestBuilderDefault extends HttpRequestBuilderBase<HeadRequestBuilder>
                                       implements HeadRequestBuilder {

    @AssistedInject
    public HeadRequestBuilderDefault(@Assisted String url,
                                     CookieFactory cookieFactory,
                                     HttpResponseFactory spincastHttpResponseFactory,
                                     SpincastHttpClientUtils spincastHttpClientUtils,
                                     SpincastHttpClientConfig spincastHttpClientConfig) {
        super(url, cookieFactory, spincastHttpResponseFactory, spincastHttpClientUtils, spincastHttpClientConfig);
    }

    @Override
    protected HttpRequestBase createMethodSpecificHttpRequest(String url) {
        return new HttpHead(url);
    }

}
