package org.spincast.plugins.httpclient.builders;

import org.spincast.core.cookies.ICookieFactory;
import org.spincast.core.json.IJsonManager;
import org.spincast.core.xml.IXmlManager;
import org.spincast.plugins.httpclient.IHttpResponseFactory;
import org.spincast.plugins.httpclient.ISpincastHttpClientConfig;
import org.spincast.plugins.httpclient.utils.ISpincastHttpClientUtils;
import org.spincast.shaded.org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.spincast.shaded.org.apache.http.client.methods.HttpPost;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

/**
 * Implementation for the Http Client builder for a POST request.
 */
public class PostRequestBuilder extends EntitySenderRequestBuilderBase<IPostRequestBuilder>
                                implements IPostRequestBuilder {

    @AssistedInject
    public PostRequestBuilder(@Assisted String url,
                              ICookieFactory cookieFactory,
                              IHttpResponseFactory spincastHttpResponseFactory,
                              IJsonManager jsonManager,
                              IXmlManager xmlManager,
                              ISpincastHttpClientUtils spincastHttpClientUtils,
                              ISpincastHttpClientConfig spincastHttpClientConfig) {
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
        return new HttpPost(url);
    }

}
