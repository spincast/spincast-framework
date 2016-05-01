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

    /**
     * If the static resource to check for gzipping is a file (not a directory),
     * this will be set to the pth of the file. This can be used to check the
     * Content-Type of the file to server.
     * 
     * In the case of a *directory*, the path of the URL will contain the target
     * file name.
     */
    private final String specificTargetFilePath;

    public GzipCheckerHandler(HttpHandler nextHandler,
                              ISpincastUtils spincastUtils,
                              String specificTargetFilePath) {
        this.nextHandler = nextHandler;
        this.spincastUtils = spincastUtils;
        this.specificTargetFilePath = specificTargetFilePath;
    }

    protected HttpHandler getNextHandler() {
        return this.nextHandler;
    }

    protected ISpincastUtils getSpincastUtils() {
        return this.spincastUtils;
    }

    protected String getSpecificTargetFilePath() {
        return this.specificTargetFilePath;
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

                //==========================================
                // Check the target file extension.
                //==========================================
                String specificTargetFilePath = getSpecificTargetFilePath();
                if(specificTargetFilePath != null) {
                    String mimeType = getSpincastUtils().getMimeTypeFromPath(specificTargetFilePath);
                    if(mimeType != null) {
                        return mimeType;
                    }
                }

                //==========================================
                // Check the URL file extension, if any (not required,
                // "/image" for example can point to an ".png".
                //==========================================
                String path = exchange.getRequestPath();
                return getSpincastUtils().getMimeTypeFromPath(path);
            } else {
                return contentType.getHeaderName().toString();
            }
        }
        return null;
    }
}
