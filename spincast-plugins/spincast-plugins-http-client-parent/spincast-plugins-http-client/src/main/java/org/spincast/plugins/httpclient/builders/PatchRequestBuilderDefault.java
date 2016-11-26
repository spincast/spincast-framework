package org.spincast.plugins.httpclient.builders;

import org.spincast.core.cookies.CookieFactory;
import org.spincast.core.json.JsonManager;
import org.spincast.core.xml.XmlManager;
import org.spincast.plugins.httpclient.HttpResponseFactory;
import org.spincast.plugins.httpclient.SpincastHttpClientConfig;
import org.spincast.plugins.httpclient.utils.SpincastHttpClientUtils;
import org.spincast.shaded.org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.spincast.shaded.org.apache.http.client.methods.HttpPatch;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

/**
 * Implementation for the Http Client builder for a PATCH request.
 */
public class PatchRequestBuilderDefault extends EntitySenderRequestBuilderBaseDefault<PatchRequestBuilder>
                                        implements PatchRequestBuilder {

    @AssistedInject
    public PatchRequestBuilderDefault(@Assisted String url,
                                      CookieFactory cookieFactory,
                                      HttpResponseFactory spincastHttpResponseFactory,
                                      JsonManager jsonManager,
                                      XmlManager xmlManager,
                                      SpincastHttpClientUtils spincastHttpClientUtils,
                                      SpincastHttpClientConfig spincastHttpClientConfig) {
        super(url,
              cookieFactory,
              spincastHttpResponseFactory,
              jsonManager,
              xmlManager,
              spincastHttpClientUtils,
              spincastHttpClientConfig);
    }

    @Override
    protected HttpEntityEnclosingRequestBase getHttpEntityEnclosingRequestBase(String url) {
        return new HttpPatch(url);
    }
}
