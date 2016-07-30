package org.spincast.plugins.routing.utils;

import java.util.Map;
import java.util.Map.Entry;

/**
 * Spincast router utils implementation.
 */
public class SpincastRoutingUtils implements ISpincastRoutingUtils {

    @Override
    public IReplaceDynamicParamsResult replaceDynamicParamsInPath(String path, Map<String, String> dynamicParams) {

        if(path == null) {
            return createReplaceDynamicParamsResult(null, false);
        }
        if(dynamicParams != null && dynamicParams.size() > 0) {
            for(Entry<String, String> entry : dynamicParams.entrySet()) {

                String paramName = entry.getKey();
                String paramValue = entry.getValue();

                path = path.replaceAll("(\\$|\\*)\\{" + paramName + "(|:[^\\}]+)\\}", paramValue);
            }
        }

        return createReplaceDynamicParamsResult(path, isPathContainDynamicParams(path));
    }

    protected IReplaceDynamicParamsResult createReplaceDynamicParamsResult(String path, boolean placeholdersRemaining) {
        return new ReplaceDynamicParamsResult(path, placeholdersRemaining);
    }

    @Override
    public boolean isPathContainDynamicParams(String path) {
        return (path.indexOf("${") > -1) || (path.indexOf("*{") > -1);
    }

}
