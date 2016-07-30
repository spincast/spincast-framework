package org.spincast.plugins.routing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.exceptions.RedirectException;
import org.spincast.core.exchange.IRequestContext;
import org.spincast.core.routing.IHandler;
import org.spincast.core.routing.IRedirectRuleBuilder;
import org.spincast.core.routing.IRouter;
import org.spincast.core.websocket.IWebsocketContext;
import org.spincast.plugins.routing.utils.IReplaceDynamicParamsResult;
import org.spincast.plugins.routing.utils.ISpincastRoutingUtils;
import org.spincast.shaded.org.apache.commons.lang3.StringUtils;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

/**
 * Default implementation for the IRedirectRuleBuilder interface.
 */
public class RedirectRuleBuilder<R extends IRequestContext<?>, W extends IWebsocketContext<?>> implements IRedirectRuleBuilder {

    protected final Logger logger = LoggerFactory.getLogger(RedirectRuleBuilder.class);

    private final IRouter<R, W> router;
    private final String oldPath;
    private final ISpincastRouterConfig spincastRouterConfig;
    private final ISpincastRoutingUtils spincastRoutingUtils;

    private boolean permanently = true;

    @AssistedInject
    public RedirectRuleBuilder(@Assisted IRouter<R, W> router,
                               @Assisted String oldPath,
                               ISpincastRouterConfig spincastRouterConfig,
                               ISpincastRoutingUtils spincastRoutingUtils) {
        this.router = router;
        this.oldPath = oldPath;
        this.spincastRouterConfig = spincastRouterConfig;
        this.spincastRoutingUtils = spincastRoutingUtils;
    }

    protected IRouter<R, W> getRouter() {
        return this.router;
    }

    protected String getOldPath() {
        return this.oldPath;
    }

    protected ISpincastRouterConfig getSpincastRouterConfig() {
        return this.spincastRouterConfig;
    }

    protected ISpincastRoutingUtils getSpincastRoutingUtils() {
        return this.spincastRoutingUtils;
    }

    protected boolean isPermanently() {
        return this.permanently;
    }

    @Override
    public IRedirectRuleBuilder permanently() {
        this.permanently = true;
        return this;
    }

    @Override
    public IRedirectRuleBuilder temporarily() {
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
                   .save(new IHandler<R>() {

                       @Override
                       public void handle(R context) {

                           //==========================================
                           // If the current route contains dynamic parameters,
                           // we may have to use them in the new URL!
                           //==========================================
                           IReplaceDynamicParamsResult result =
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
