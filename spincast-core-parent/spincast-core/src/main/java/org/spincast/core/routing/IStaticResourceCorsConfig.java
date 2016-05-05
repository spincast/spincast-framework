package org.spincast.core.routing;

import java.util.Set;

/**
 * Cors configurations available for a static resource.
 */
public interface IStaticResourceCorsConfig {

    /**
     * The allowed origins.
     */
    public Set<String> getAllowedOrigins();

    /**
     * The extra headers a browser will be allowed to read.
     */
    public Set<String> getExtraHeadersAllowedToBeRead();

    /**
     * The extra headers a browser will be allowed to send.
     */
    public Set<String> getExtraHeadersAllowedToBeSent();

    /**
     * Are cookies allowed?
     */
    public boolean isAllowCookies();

    /**
     * The max Age a preflight request can be cached.
     */
    public int getMaxAgeInSeconds();

}
