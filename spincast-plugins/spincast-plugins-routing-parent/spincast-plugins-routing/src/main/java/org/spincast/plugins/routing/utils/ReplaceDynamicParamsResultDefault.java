package org.spincast.plugins.routing.utils;

/**
 * Implementation for result of a call to 
 * SpincastRoutingUtils#replaceDynamicParamsInPath(...)
 */
public class ReplaceDynamicParamsResultDefault implements ReplaceDynamicParamsResult {

    private final String path;
    private final boolean placeholdersRemaining;

    public ReplaceDynamicParamsResultDefault(String path, boolean placeholdersRemaining) {
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
