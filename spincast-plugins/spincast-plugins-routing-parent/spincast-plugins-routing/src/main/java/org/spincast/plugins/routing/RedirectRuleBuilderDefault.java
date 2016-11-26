package org.spincast.plugins.routing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.exceptions.RedirectException;
import org.spincast.core.exchange.RequestContext;
import org.spincast.core.routing.Handler;
import org.spincast.core.routing.RedirectRuleBuilder;
import org.spincast.core.routing.Router;
import org.spincast.core.websocket.WebsocketContext;
import org.spincast.plugins.routing.utils.ReplaceDynamicParamsResult;
import org.spincast.plugins.routing.utils.SpincastRoutingUtils;
import org.spincast.shaded.org.apache.commons.lang3.StringUtils;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

/**
 * Default implementation for the RedirectRuleBuilder interface.
 */
public class RedirectRuleBuilderDefault<R extends RequestContext<?>, W extends WebsocketContext<?>>
                                       implements RedirectRuleBuilder {

    protected final Logger logger = LoggerFactory.getLogger(RedirectRuleBuilderDefault.class);

    private final Router<R, W> router;
    private final String oldPath;
    private final SpincastRouterConfig spincastRouterConfig;
    private final SpincastRoutingUtils spincastRoutingUtils;

    private boolean permanently = true;

    @AssistedInject
    public RedirectRuleBuilderDefault(@Assisted Router<R, W> router,
                                      @Assisted String oldPath,
                                      SpincastRouterConfig spincastRouterConfig,
                                      SpincastRoutingUtils spincastRoutingUtils) {
        this.router = router;
        this.oldPath = oldPath;
        this.spincastRouterConfig = spincastRouterConfig;
        this.spincastRoutingUtils = spincastRoutingUtils;
    }

    protected Router<R, W> getRouter() {
        return this.router;
    }

    protected String getOldPath() {
        return this.oldPath;
    }

    protected SpincastRouterConfig getSpincastRouterConfig() {
        return this.spincastRouterConfig;
    }

    protected SpincastRoutingUtils getSpincastRoutingUtils() {
        return this.spincastRoutingUtils;
    }

    protected boolean isPermanently() {
        return this.permanently;
    }

    @Override
    public RedirectRuleBuilder permanently() {
        this.permanently = true;
        return this;
    }

    @Override
    public RedirectRuleBuilder temporarily() {
        this.permanently = false;
        return this;
    }

    @Override
    public void to(String newPathOrFullUrl) {

        if(StringUtils.isBlank(newPathOrFullUrl)) {
            newPathOrFullUrl = "/";
        }

        final String newPathOrFullUrlFinal = newPathOrFullUrl;
        getRouter().ALL(getOldPath())
                   .pos(getSpincastRouterConfig().getRedirectFilterPosition())
                   .found().notFound()
                   .save(new Handler<R>() {

                       @Override
                       public void handle(R context) {

                           //==========================================
                           // If the current route contains dynamic parameters,
                           // we may have to use them in the new URL!
                           //==========================================
                           ReplaceDynamicParamsResult result =
                                   getSpincastRoutingUtils().replaceDynamicParamsInPath(newPathOrFullUrlFinal,
                                                                                        context.request().getPathParams());

                           //==========================================
                           // Throwing a "RedirectException" make sure nothing
                           // more is executed after this handler.
                           //==========================================
                           throw new RedirectException(result.getPath(), isPermanently());
                       }
                   });
    }

}
