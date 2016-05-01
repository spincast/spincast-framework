package org.spincast.plugins.httpclient.builders;

import org.spincast.core.cookies.ICookieFactory;
import org.spincast.plugins.httpclient.IHttpResponseFactory;
import org.spincast.shaded.org.apache.http.client.methods.HttpGet;
import org.spincast.shaded.org.apache.http.client.methods.HttpRequestBase;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

/**
 * Implementation for the Http Client builder for a GET request.
 */
public class GetRequestBuilder extends SpincastRequestBuilderBase<IGetRequestBuilder>
                               implements IGetRequestBuilder {

    @AssistedInject
    public GetRequestBuilder(@Assisted String url,
                             ICookieFactory cookieFactory,
                             IHttpResponseFactory spincastHttpResponseFactory) {
        super(url, cookieFactory, spincastHttpResponseFactory);
    }

    @Override
    protected HttpRequestBase createMethodSpecificHttpRequest(String url) {
        return new HttpGet(url);
    }

}
