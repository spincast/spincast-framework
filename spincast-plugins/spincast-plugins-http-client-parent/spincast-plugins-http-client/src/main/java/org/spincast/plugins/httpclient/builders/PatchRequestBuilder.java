package org.spincast.plugins.httpclient.builders;

import org.spincast.core.cookies.ICookieFactory;
import org.spincast.core.json.IJsonManager;
import org.spincast.core.xml.IXmlManager;
import org.spincast.plugins.httpclient.IHttpResponseFactory;
import org.spincast.shaded.org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.spincast.shaded.org.apache.http.client.methods.HttpPatch;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

/**
 * Implementation for the Http Client builder for a PATCH request.
 */
public class PatchRequestBuilder extends EntitySenderRequestBuilderBase<IPatchRequestBuilder>
                                 implements IPatchRequestBuilder {

    @AssistedInject
    public PatchRequestBuilder(@Assisted String url,
                               ICookieFactory cookieFactory,
                               IHttpResponseFactory spincastHttpResponseFactory,
                               IJsonManager jsonManager,
                               IXmlManager xmlManager) {
        super(url, cookieFactory, spincastHttpResponseFactory, jsonManager, xmlManager);
    }

    @Override
    protected HttpEntityEnclosingRequestBase getHttpEntityEnclosingRequestBase(String url) {
        return new HttpPatch(url);
    }
}
