package org.spincast.core.routing.hotlinking;

import java.net.URI;

import org.spincast.core.routing.StaticResource;

public interface HotlinkingManager {

    /**
     * This must return <code>true</code> if the current
     * resource has to be hotlinking protected.
     */
    public boolean mustHotlinkingProtect(Object serverExchange,
                                         URI resourceUri,
                                         String requestOriginHeader,
                                         String requestRefererHeader,
                                         StaticResource<?> resource);

    /**
     * The strategy to use to protect the hotlinked resource.
     * 
     */
    public HotlinkingStategy getHotlinkingStategy(Object serverExchange,
                                                  URI resourceURI,
                                                  StaticResource<?> resource);

    /**
     * The URL to redirect the protected resource when the
     * {@link #getHotlinkingStategy(Object, URI, StaticResource)} returns
     * {@link HotlinkingStategy#REDIRECT}. Won't be used otherwise.
     */
    public String getRedirectUrl(Object serverExchange,
                                 URI resourceURI,
                                 StaticResource<?> resource);
}
