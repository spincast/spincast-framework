package org.spincast.plugins.undertow;

import org.spincast.core.utils.SpincastUtils;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

/**
 * Handler to remove cache buster codes from the request URL.
 */
public class CacheBusterRemovalHandlerDefault implements CacheBusterRemovalHandler {

    public static final String EXCHANGE_VARIABLE_ORIGINAL_REQUEST_URL =
            CacheBusterRemovalHandlerDefault.class.getName() + "_originalRequestUrl";

    private final HttpHandler next;
    private final SpincastUtils spincastUtils;
    private final SpincastUndertowUtils spincastUndertowUtils;

    @AssistedInject
    public CacheBusterRemovalHandlerDefault(@Assisted HttpHandler next,
                                            SpincastUtils spincastUtils,
                                            SpincastUndertowUtils spincastUndertowUtils) {
        this.next = next;
        this.spincastUtils = spincastUtils;
        this.spincastUndertowUtils = spincastUndertowUtils;
    }

    protected HttpHandler getNext() {
        return this.next;
    }

    protected SpincastUtils getSpincastUtils() {
        return this.spincastUtils;
    }

    protected SpincastUndertowUtils getSpincastUndertowUtils() {
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
