package org.spincast.plugins.undertow;

import org.spincast.core.utils.ISpincastUtils;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

/**
 * Handler to remove cache buster codes from the request URL.
 */
public class CacheBusterRemovalHandler implements ICacheBusterRemovalHandler {

    public static final String EXCHANGE_VARIABLE_ORIGINAL_REQUEST_URL =
            CacheBusterRemovalHandler.class.getName() + "_originalRequestUrl";

    private final HttpHandler next;
    private final ISpincastUtils spincastUtils;
    private final ISpincastUndertowUtils spincastUndertowUtils;

    @AssistedInject
    public CacheBusterRemovalHandler(@Assisted HttpHandler next,
                                     ISpincastUtils spincastUtils,
                                     ISpincastUndertowUtils spincastUndertowUtils) {
        this.next = next;
        this.spincastUtils = spincastUtils;
        this.spincastUndertowUtils = spincastUndertowUtils;
    }

    protected HttpHandler getNext() {
        return this.next;
    }

    protected ISpincastUtils getSpincastUtils() {
        return this.spincastUtils;
    }

    protected ISpincastUndertowUtils getSpincastUndertowUtils() {
        return this.spincastUndertowUtils;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {

        keepOriginalRequestUrlInformation(exchange);
        removeCacheBusterCode(exchange);
        getNext().handleRequest(exchange);
    }

    protected void keepOriginalRequestUrlInformation(HttpServerExchange exchange) {
        getSpincastUndertowUtils().getRequestCustomVariables(exchange).put(EXCHANGE_VARIABLE_ORIGINAL_REQUEST_URL,
                                                                           exchange.getRequestURL());
    }

    @Override
    public String getOrigninalRequestUrlWithPotentialCacheBusters(HttpServerExchange exchange) {
        return getSpincastUndertowUtils().getRequestCustomVariables(exchange).get(EXCHANGE_VARIABLE_ORIGINAL_REQUEST_URL);
    }

    protected void removeCacheBusterCode(HttpServerExchange exchange) {

        String requestPath = exchange.getRequestPath();

        String requestPathNoCacheBusterCodes = getSpincastUtils().removeCacheBusterCodes(requestPath);

        exchange.setRequestPath(requestPathNoCacheBusterCodes);
        exchange.setRelativePath(requestPathNoCacheBusterCodes);
        exchange.setRequestURI(requestPathNoCacheBusterCodes, false);

    }

}
