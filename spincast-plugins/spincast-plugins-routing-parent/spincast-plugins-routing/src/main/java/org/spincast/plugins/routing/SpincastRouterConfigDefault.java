package org.spincast.plugins.routing;

import java.util.Set;

import org.spincast.core.config.SpincastConfig;
import org.spincast.core.routing.RoutingType;

import com.google.common.collect.Sets;
import com.google.inject.Inject;

/**
 * Default configuration for the Spincast Router.
 */
public class SpincastRouterConfigDefault implements SpincastRouterConfig {

    private final SpincastConfig spincastConfig;

    @Inject
    public SpincastRouterConfigDefault(SpincastConfig spincastConfig) {
        this.spincastConfig = spincastConfig;
    }

    protected SpincastConfig getSpincastConfig() {
        return this.spincastConfig;
    }

    @Override
    public Set<RoutingType> getFilterDefaultRoutingTypes() {

        //==========================================
        // By default we apply the filters on all
        // route types.
        //==========================================
        return Sets.newHashSet(RoutingType.values());
    }

    @Override
    public int getCorsFilterPosition() {
        return -100;
    }

    @Override
    public int getRedirectFilterPosition() {
        return -1000;
    }

}
