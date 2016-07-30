package org.spincast.plugins.routing;

import java.util.Set;

import org.spincast.core.config.ISpincastConfig;
import org.spincast.core.routing.RoutingType;

import com.google.common.collect.Sets;
import com.google.inject.Inject;

/**
 * Default configuration for the Spincast Router.
 */
public class SpincastRouterConfigDefault implements ISpincastRouterConfig {

    private final ISpincastConfig spincastConfig;

    @Inject
    public SpincastRouterConfigDefault(ISpincastConfig spincastConfig) {
        this.spincastConfig = spincastConfig;
    }

    protected ISpincastConfig getSpincastConfig() {
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
