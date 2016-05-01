package org.spincast.plugins.httpclient.builders;

import org.spincast.core.cookies.ICookieFactory;
import org.spincast.plugins.httpclient.IHttpResponseFactory;
import org.spincast.shaded.org.apache.http.client.methods.HttpOptions;
import org.spincast.shaded.org.apache.http.client.methods.HttpRequestBase;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

/**
 * Implementation for the Http Client builder for a OPTIONS request.
 */
public class OptionsRequestBuilder extends SpincastRequestBuilderBase<IOptionsRequestBuilder>
                                   implements IOptionsRequestBuilder {

    @AssistedInject
    public OptionsRequestBuilder(@Assisted String url,
                                 ICookieFactory cookieFactory,
                                 IHttpResponseFactory spincastHttpResponseFactory) {
        super(url, cookieFactory, spincastHttpResponseFactory);
    }

    @Override
    protected HttpRequestBase createMethodSpecificHttpRequest(String url) {
        return new HttpOptions(url);
    }

}
