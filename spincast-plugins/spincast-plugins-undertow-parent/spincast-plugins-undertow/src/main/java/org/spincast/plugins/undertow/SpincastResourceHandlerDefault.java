package org.spincast.plugins.undertow;

import java.net.URI;
import java.util.Date;

import org.spincast.core.config.SpincastConfig;
import org.spincast.core.routing.StaticResource;
import org.spincast.core.routing.StaticResourceType;
import org.spincast.core.routing.hotlinking.HotlinkingManager;
import org.spincast.core.routing.hotlinking.HotlinkingStategy;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.core.utils.SpincastUtils;
import org.spincast.plugins.routing.utils.SpincastRoutingUtils;
import org.spincast.shaded.org.apache.http.client.utils.DateUtils;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.ResponseCodeHandler;
import io.undertow.server.handlers.resource.Resource;
import io.undertow.server.handlers.resource.ResourceHandler;
import io.undertow.server.handlers.resource.ResourceManager;
import io.undertow.util.Headers;
import io.undertow.util.Methods;
import io.undertow.util.StatusCodes;

/**
 * Spincast's custom ResourceHandler for Undertow.
 */
public class SpincastResourceHandlerDefault extends ResourceHandler implements SpincastResourceHandler {

    private final StaticResource<?> staticResource;
    private final SpincastUtils spincastUtils;
    private final SpincastConfig spincastConfig;
    private final SpincastRoutingUtils spincastRoutingUtils;
    private HttpHandler next;

    @AssistedInject
    public SpincastResourceHandlerDefault(@Assisted ResourceManager resourceManager,
                                          @Assisted StaticResource<?> staticResource,
                                          SpincastUtils spincastUtils,
                                          SpincastConfig spincastConfig,
                                          SpincastRoutingUtils spincastRoutingUtils) {
        this(resourceManager,
             staticResource,
             ResponseCodeHandler.HANDLE_404,
             spincastUtils,
             spincastConfig,
             spincastRoutingUtils);
    }

    @AssistedInject
    public SpincastResourceHandlerDefault(@Assisted ResourceManager resourceManager,
                                          @Assisted StaticResource<?> staticResource,
                                          @Assisted HttpHandler next,
                                          SpincastUtils spincastUtils,
                                          SpincastConfig spincastConfig,
                                          SpincastRoutingUtils spincastRoutingUtils) {
        super(resourceManager, next);
        this.next = next;
        this.staticResource = staticResource;
        this.spincastUtils = spincastUtils;
        this.spincastConfig = spincastConfig;
        this.spincastRoutingUtils = spincastRoutingUtils;
    }

    protected HttpHandler getNext() {
        return this.next;
    }

    protected SpincastUtils getSpincastUtils() {
        return this.spincastUtils;
    }

    protected SpincastConfig getSpincastConfig() {
        return this.spincastConfig;
    }

    protected StaticResource<?> getStaticResource() {
        return this.staticResource;
    }

    protected SpincastRoutingUtils getSpincastRoutingUtils() {
        return this.spincastRoutingUtils;
    }

    @Override
    public ResourceHandler setCacheTime(final Integer cacheTime) {
        throw new RuntimeException("Use a constructor to specify the cache time.");
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {

        //==========================================
        // Is it a hotlinking protected resource?
        //==========================================
        if (exchange.getRequestMethod().equals(Methods.GET) && getStaticResource().isHotlinkingProtected()) {
            HotlinkingManager hotlinkingManager = getStaticResource().getHotlinkingManager();
            URI resourceUri = new URI(exchange.getRequestURI());
            if (hotlinkingManager.mustHotlinkingProtect(exchange,
                                                        resourceUri,
                                                        exchange.getRequestHeaders().getFirst(Headers.ORIGIN_STRING),
                                                        exchange.getRequestHeaders().getFirst(Headers.REFERER_STRING),
                                                        getStaticResource())) {
                HotlinkingStategy hotlinkingStategy =
                        hotlinkingManager.getHotlinkingStategy(exchange, resourceUri, getStaticResource());

                if (hotlinkingStategy == HotlinkingStategy.FORBIDDEN) {
                    exchange.setStatusCode(StatusCodes.FORBIDDEN);
                    exchange.endExchange();
                    return;

                } else if (hotlinkingStategy == HotlinkingStategy.REDIRECT) {
                    String redirectUrl = hotlinkingManager.getRedirectUrl(exchange, resourceUri, getStaticResource());
                    exchange.setStatusCode(StatusCodes.FOUND);
                    exchange.getResponseHeaders().put(Headers.LOCATION, redirectUrl);
                    exchange.endExchange();
                    return;

                } else {
                    throw new RuntimeException("The Hotlinking strategy \"" + hotlinkingStategy + "\" is not implemented in " +
                                               this.getClass().getName() + "!");
                }
            }
        }

        if (exchange.getRequestMethod().equals(Methods.GET) ||
            exchange.getRequestMethod().equals(Methods.POST) ||
            exchange.getRequestMethod().equals(Methods.HEAD)) {

            //==========================================
            // Add Content-Type headers.
            // We add it by ourself instead
            // of letting Undertow determine it.
            //==========================================
            addContentTypeHeader(exchange);

            //==========================================
            // Do we have to send caching headers?
            //==========================================
            addCacheHeaders(exchange);
        }

        super.handleRequest(exchange);
    }

    protected void addContentTypeHeader(HttpServerExchange exchange) {

        //==========================================
        // No Content-Type for directories...
        //==========================================
        try {
            Resource resource = getResourceManager().getResource(exchange.getRelativePath());
            if (resource != null && resource.isDirectory()) {
                return;
            }
        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }

        //==========================================
        // If the target resource is a file, then we
        // use the file extension to determine the
        // Content-Type. Otherwise, the 
        // getSpincastUndertowUtils().getContentTypeToUse() method will use
        // the request's path.
        //==========================================
        String resourcePath = null;
        if (getStaticResource().getStaticResourceType() == StaticResourceType.FILE ||
            getStaticResource().getStaticResourceType() == StaticResourceType.FILE_FROM_CLASSPATH) {
            resourcePath = getStaticResource().getResourcePath();
        }

        String responseContentTypeHeader = exchange.getResponseHeaders().getFirst(Headers.CONTENT_TYPE);
        String contentType = getSpincastUtils().getMimeTypeFromMultipleSources(responseContentTypeHeader,
                                                                               resourcePath,
                                                                               exchange.getRequestPath());
        if (contentType == null) {
            contentType = ContentTypeDefaults.BINARY.getMainVariation();
        }

        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, contentType);
    }

    /**
     * Adds cache headers if required.
     */
    protected void addCacheHeaders(HttpServerExchange exchange) {

        if (getStaticResource().getCacheConfig() != null) {
            int cacheSeconds = getStaticResource().getCacheConfig().getCacheSeconds();
            if (cacheSeconds > 0) {

                String cacheControl = "";
                if (getStaticResource().getCacheConfig().isCachePrivate()) {
                    cacheControl = "private";
                } else {
                    cacheControl = "public";
                }
                cacheControl += ", max-age=" + cacheSeconds;

                Integer cacheSecondsCdn = getStaticResource().getCacheConfig().getCacheSecondsCdn();
                if (cacheSecondsCdn != null) {
                    if (cacheSecondsCdn < 0) {
                        cacheSecondsCdn = 0;
                    }
                    cacheControl += ", s-maxage=" + cacheSecondsCdn;
                }

                exchange.getResponseHeaders().put(Headers.CACHE_CONTROL, cacheControl);
            }

            Date date = org.spincast.shaded.org.apache.commons.lang3.time.DateUtils.addSeconds(new Date(), cacheSeconds);
            String dateStr = DateUtils.formatDate(date);
            exchange.getResponseHeaders().put(Headers.EXPIRES, dateStr);
        }
    }

}
