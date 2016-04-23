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
     * Are cookies allowed?
     */
    public boolean isAllowCookies();

}
