package org.spincast.core.routing.hotlinking;

/**
 * The strategy to apply to hotlinking protect
 * a resource.
 */
public enum HotlinkingStategy {

    /**
     * A forbidden empty response is returned.
     */
    FORBIDDEN,

    /**
     * The request must be redirect to a new URL, for
     * example to a watermarked version of the resource.
     */
    REDIRECT
}
