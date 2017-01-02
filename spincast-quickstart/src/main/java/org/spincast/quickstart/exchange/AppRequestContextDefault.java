package org.spincast.quickstart.exchange;

import org.spincast.core.exchange.RequestContextBase;
import org.spincast.core.exchange.RequestContextBaseDeps;
import org.spincast.plugins.httpclient.HttpClient;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

/**
 * Implementation of our custom Request Context type.
 */
public class AppRequestContextDefault extends RequestContextBase<AppRequestContext>
                                      implements AppRequestContext {

    private final HttpClient httpClient;

    @AssistedInject
    public AppRequestContextDefault(@Assisted Object exchange,
                                    RequestContextBaseDeps<AppRequestContext> requestContextBaseDeps,
                                    HttpClient httpClient) {
        super(exchange, requestContextBaseDeps);
        this.httpClient = httpClient;
    }

    @Override
    public HttpClient httpClient() {
        return this.httpClient;
    }

}
