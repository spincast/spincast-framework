package org.spincast.plugins.routing;

import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.exceptions.RedirectException;
import org.spincast.core.exchange.IRequestContext;
import org.spincast.core.routing.IHandler;
import org.spincast.core.routing.IRedirectRuleBuilder;
import org.spincast.core.routing.IRouter;
import org.spincast.core.websocket.IWebsocketContext;
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

    private boolean permanently = true;

    @AssistedInject
    public RedirectRuleBuilder(@Assisted IRouter<R, W> router,
                               @Assisted String oldPath,
                               ISpincastRouterConfig spincastRouterConfig) {
        this.router = router;
        this.oldPath = oldPath;
        this.spincastRouterConfig = spincastRouterConfig;
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

                           String newPathOrFullUrl = newPathOrFullUrlFinal;

                           //==========================================
                           // If the current route contains dynamic parameters,
                           // we may have to use them in the new URL!
                           //==========================================
                           Map<String, String> pathParams = context.request().getPathParams();
                           if(pathParams != null) {
                               for(Entry<String, String> entry : pathParams.entrySet()) {

                                   String paramName = entry.getKey();
                                   String paramValue = entry.getValue();

                                   //==========================================
                                   // In the new URL, the value of the dynamic parameters
                                   // can be used anywhere, and can be or format "${paramName}"
                                   // or "*{paramName}" without distinction.
                                   //==========================================
                                   newPathOrFullUrl = newPathOrFullUrl.replace("${" + paramName + "}", paramValue);
                                   newPathOrFullUrl = newPathOrFullUrl.replace("*{" + paramName + "}", paramValue);
                               }
                           }

                           //==========================================
                           // Throwing a "RedirectException" make sure nothing
                           // more is executed after this handler.
                           //==========================================
                           throw new RedirectException(newPathOrFullUrl, isPermanently());
                       }
                   });
    }

}
