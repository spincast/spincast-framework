package org.spincast.plugins.httpclient.builders;

import org.spincast.core.cookies.CookieFactory;
import org.spincast.core.json.JsonManager;
import org.spincast.core.xml.XmlManager;
import org.spincast.plugins.httpclient.HttpResponseFactory;
import org.spincast.plugins.httpclient.SpincastHttpClientConfig;
import org.spincast.plugins.httpclient.utils.SpincastHttpClientUtils;
import org.spincast.shaded.org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.spincast.shaded.org.apache.http.client.methods.HttpPut;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

/**
 * Implementation for the Http Client builder for a PUT request.
 */
public class PutRequestBuilderDefault extends EntitySenderRequestBuilderBaseDefault<PutRequestBuilder>
                                      implements PutRequestBuilder {

    @AssistedInject
    public PutRequestBuilderDefault(@Assisted String url,
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
        return new HttpPut(url);
    }
}
