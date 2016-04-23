package org.spincast.plugins.undertow;

import org.spincast.core.utils.ISpincastUtils;

import com.google.common.net.HttpHeaders;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.encoding.ContentEncodingRepository;
import io.undertow.server.handlers.encoding.EncodingHandler;
import io.undertow.server.handlers.encoding.GzipEncodingProvider;
import io.undertow.util.HeaderMap;
import io.undertow.util.HeaderValues;

/**
 * Handler that will check if gzip compress is required for
 * the resource and, if so, will call the gzip handler before
 * calling the next handler.
 */
public class GzipCheckerHandler implements HttpHandler {

    private final HttpHandler nextHandler;
    private final ISpincastUtils spincastUtils;
    private EncodingHandler gzipHandler;

    public GzipCheckerHandler(HttpHandler nextHandler,
                              ISpincastUtils spincastUtils) {
        this.nextHandler = nextHandler;
        this.spincastUtils = spincastUtils;
    }

    protected HttpHandler getNextHandler() {
        return this.nextHandler;
    }

    protected ISpincastUtils getSpincastUtils() {
        return this.spincastUtils;
    }

    protected EncodingHandler getGzipNoNextHandler() {
        if(this.gzipHandler == null) {

            // @formatter:off
            this.gzipHandler =
                    new EncodingHandler(
                            new ContentEncodingRepository().addEncodingHandler("gzip",
                                    new GzipEncodingProvider(), 50)).setNext(
                                        new HttpHandler() {
                                               @Override
                                               public void handleRequest(HttpServerExchange exchange) throws Exception {
                                                   // empty next handler!
                                               }
                                        });
            // @formatter:on

        }
        return this.gzipHandler;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {

        if(!isContentTypeToSkipGziping(exchange)) {
            getGzipNoNextHandler().handleRequest(exchange);
        }
        getNextHandler().handleRequest(exchange);
    }

    protected boolean isContentTypeToSkipGziping(HttpServerExchange exchange) {
        String contentType = getResponseContentType(exchange);
        if(contentType != null) {
            return getSpincastUtils().isContentTypeToSkipGziping(contentType);
        }
        return false;
    }

    protected String getResponseContentType(HttpServerExchange exchange) {

        HeaderMap responseHeaderMap = exchange.getResponseHeaders();
        if(responseHeaderMap != null) {
            HeaderValues contentType = responseHeaderMap.get(HttpHeaders.CONTENT_TYPE);
            if(contentType == null) {
                String path = exchange.getRequestPath();
                return getSpincastUtils().getMimeTypeFromPath(path);
            } else {
                return contentType.getHeaderName().toString();
            }
        }
        return null;
    }
}
