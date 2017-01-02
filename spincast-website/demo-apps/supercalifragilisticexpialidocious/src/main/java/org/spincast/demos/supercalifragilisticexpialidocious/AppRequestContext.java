package org.spincast.demos.supercalifragilisticexpialidocious;

import org.spincast.core.exchange.RequestContext;
import org.spincast.plugins.httpclient.HttpClient;

public interface AppRequestContext extends RequestContext<AppRequestContext> {

    /**
     * Add-on to access the HttpClient factory
     */
    public HttpClient httpClient();

    //... other add-ons
}
