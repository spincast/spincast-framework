package org.spincast.plugins.routing.utils;

/**
 * Implementation for result of a call to 
 * ISpincastRoutingUtils#replaceDynamicParamsInPath(...)
 */
public class ReplaceDynamicParamsResult implements IReplaceDynamicParamsResult {

    private final String path;
    private final boolean placeholdersRemaining;

    public ReplaceDynamicParamsResult(String path, boolean placeholdersRemaining) {
        this.path = path;
        this.placeholdersRemaining = placeholdersRemaining;
    }

    @Override
    public String getPath() {
        return this.path;
    }

    @Override
    public boolean isPlaceholdersRemaining() {
        return this.placeholdersRemaining;
    }
}
