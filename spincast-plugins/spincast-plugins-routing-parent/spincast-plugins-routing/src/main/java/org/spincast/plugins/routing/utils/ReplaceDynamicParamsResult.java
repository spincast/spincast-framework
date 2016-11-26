package org.spincast.plugins.routing.utils;

/**
 * The result of a call to 
 * SpincastRoutingUtils#replaceDynamicParamsInPath(...)
 */
public interface ReplaceDynamicParamsResult {

    /**
     * The resulting path
     */
    public String getPath();

    /**
     * Are there some placeholders remaining in the resulting
     * path?
     */
    public boolean isPlaceholdersRemaining();
}
