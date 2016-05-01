package org.spincast.plugins.httpclient.builders;

import org.spincast.core.cookies.ICookieFactory;
import org.spincast.plugins.httpclient.IHttpResponseFactory;
import org.spincast.shaded.org.apache.http.client.methods.HttpRequestBase;
import org.spincast.shaded.org.apache.http.client.methods.HttpTrace;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

/**
 * Implementation for the Http Client builder for a TRACE request.
 */
public class TraceRequestBuilder extends SpincastRequestBuilderBase<ITraceRequestBuilder>
                                 implements ITraceRequestBuilder {

    @AssistedInject
    public TraceRequestBuilder(@Assisted String url,
                               ICookieFactory cookieFactory,
                               IHttpResponseFactory spincastHttpResponseFactory) {
        super(url, cookieFactory, spincastHttpResponseFactory);
    }

    @Override
    protected HttpRequestBase createMethodSpecificHttpRequest(String url) {
        return new HttpTrace(url);
    }
}
