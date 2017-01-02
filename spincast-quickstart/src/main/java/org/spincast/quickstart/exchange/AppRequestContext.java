package org.spincast.quickstart.exchange;

import org.spincast.core.exchange.RequestContext;
import org.spincast.plugins.httpclient.HttpClient;

/**
 * Interface of our custom Request Context type.
 */
public interface AppRequestContext extends RequestContext<AppRequestContext> {

    /**
     * Add-on to access the HttpClient factory
     */
    public HttpClient httpClient();
}
