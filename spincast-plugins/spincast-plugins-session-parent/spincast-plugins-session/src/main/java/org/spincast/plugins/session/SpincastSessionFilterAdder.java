package org.spincast.plugins.session;

import org.spincast.core.exchange.DefaultRequestContext;
import org.spincast.core.routing.Router;
import org.spincast.core.websocket.DefaultWebsocketContext;
import org.spincast.plugins.session.config.SpincastSessionConfig;

import com.google.inject.Inject;

public class SpincastSessionFilterAdder {

    private final SpincastSessionConfig spincastSessionConfig;
    private final Router<DefaultRequestContext, DefaultWebsocketContext> router;
    private final SpincastSessionFilter spincastSessionFilter;

    @Inject
    public SpincastSessionFilterAdder(SpincastSessionConfig spincastSessionConfig,
                                      Router<DefaultRequestContext, DefaultWebsocketContext> router,
                                      SpincastSessionFilter spincastSessionFilter) {
        this.spincastSessionConfig = spincastSessionConfig;
        this.router = router;
        this.spincastSessionFilter = spincastSessionFilter;
    }

    protected SpincastSessionConfig getSpincastSessionConfig() {
        return this.spincastSessionConfig;
    }

    protected Router<DefaultRequestContext, DefaultWebsocketContext> getRouter() {
        return this.router;
    }

    protected SpincastSessionFilter getSpincastSessionFilter() {
        return this.spincastSessionFilter;
    }

    @Inject
    protected void init() {
        if (getSpincastSessionConfig().isAutoAddSessionFilters()) {
            addFilters();
        }
    }

    protected void addFilters() {

        int beforePos = getSpincastSessionConfig().getAutoAddedFilterBeforePosition();
        if (beforePos >= 0) {
            throw new RuntimeException("Invalid position for the before Session filter. Must be negative: " + beforePos);
        }

        int afterPos = getSpincastSessionConfig().getAutoAddedFilterAfterPosition();
        if (afterPos <= 0) {
            throw new RuntimeException("Invalid position for the after Session filter. Must be positive: " + afterPos);
        }

        getRouter().ALL().pos(beforePos)
                   .id(SpincastSessionFilter.ROUTE_ID_BEFORE_FILTER)
                   .spicastCoreRouteOrPluginRoute()
                   .skipResourcesRequests()
                   .handle((context) -> {
                       getSpincastSessionFilter().before(context);
                   });

        getRouter().ALL().pos(afterPos)
                   .id(SpincastSessionFilter.ROUTE_ID_AFTER_FILTER)
                   .spicastCoreRouteOrPluginRoute()
                   .skipResourcesRequests()
                   .handle((context) -> {
                       getSpincastSessionFilter().after(context);
                   });
    }

}
