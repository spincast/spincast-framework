package org.spincast.core.routing.hotlinking;

import java.net.URI;

import org.spincast.core.config.SpincastConfig;
import org.spincast.core.routing.StaticResource;
import org.spincast.core.utils.SpincastStatics;

import com.google.inject.Inject;

/**
 * Default {@link HotlinkingManager}. Will return
 * a <code>FORBIDDEN</code> status code.
 */
public class HotlinkingManagerDefault implements HotlinkingManager {

    private final SpincastConfig spincastConfig;

    @Inject
    public HotlinkingManagerDefault(SpincastConfig spincastConfig) {
        this.spincastConfig = spincastConfig;
    }

    protected SpincastConfig getSpincastConfig() {
        return this.spincastConfig;
    }

    @Override
    public HotlinkingStategy getHotlinkingStategy(Object serverExchange, URI resourceURI, StaticResource<?> resource) {
        return HotlinkingStategy.FORBIDDEN;
    }

    @Override
    public String getRedirectUrl(Object serverExchange, URI resourceURI, StaticResource<?> resource) {
        return null;
    }

    @Override
    public boolean mustHotlinkingProtect(Object serverExchange,
                                         URI resourceUri,
                                         String requestOriginHeader,
                                         String requestRefererHeader,
                                         StaticResource<?> resource) {
        return resource.isHotlinkingProtected() && !isHostAndRefererValid(serverExchange,
                                                                          resourceUri,
                                                                          requestOriginHeader,
                                                                          requestRefererHeader,
                                                                          resource);
    }

    protected boolean isHostAndRefererValid(Object serverExchange,
                                            URI resourceUri,
                                            String requestOriginHeader,
                                            String requestRefererHeader,
                                            StaticResource<?> resource) {
        try {

            String requiredHost = getSpincastConfig().getPublicServerHost();

            //==========================================
            // We want to allow a resource to be downloaded
            // without being protected...
            //
            // In short, by default we only protected a resource
            // if it is embedded in an HTML page from an
            // unknown domain.
            //==========================================
            if (requestOriginHeader == null && requestRefererHeader == null) {
                return true;
            }

            if (requestOriginHeader != null && requiredHost.equalsIgnoreCase(new URI(requestOriginHeader).getHost())) {
                return true;
            }

            if (requestRefererHeader != null && requiredHost.equalsIgnoreCase(new URI(requestRefererHeader).getHost())) {
                return true;
            }

            return false;
        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }
}

