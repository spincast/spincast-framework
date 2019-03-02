package org.spincast.plugins.routing.utils;

import java.util.Map;

/**
 * Spincast router utils
 */
public interface SpincastRoutingUtils {

    /**
     * Replaces all the dynamic (and splat) parameters in the
     * specified 'path'.
     */
    public ReplaceDynamicParamsResult replaceDynamicParamsInPath(String path, Map<String, String> dynamicParams);

    /**
     * Does the specified path contain dynamic (or splat) parameters?
     */
    public boolean isPathContainDynamicParams(String resourcePath);

}
