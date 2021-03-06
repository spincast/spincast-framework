package org.spincast.website.controllers.demos;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.website.exchange.AppRequestContext;

/**
 * Demos/Tutorials controller
 */
public class DemosTutorialsController {

    protected static final Logger logger = LoggerFactory.getLogger(DemosTutorialsController.class);

    public void webSockets(AppRequestContext context) {
        context.response().getModel().set("isHttps", context.request().isHttps());
        context.response().sendTemplateHtml("/templates/demos/websockets.html");
    }

    public void httpAuthentication(AppRequestContext context) {
        context.response().sendTemplateHtml("/templates/demos/httpAuth.html");
    }

    public void httpAuthenticationProtectedPage(AppRequestContext context) {
        context.response().sendTemplateHtml("/templates/demos/httpAuthProtectedPage.html");
    }

    public void realApps(AppRequestContext context) {
        context.response().sendTemplateHtml("/templates/demos/realApps.html");
    }

    public void helloWorldQuick(AppRequestContext context) {
        context.response().sendTemplateHtml("/templates/demos/helloWorld/quick.html");
    }

    public void helloWorldBetter(AppRequestContext context) {
        context.response().sendTemplateHtml("/templates/demos/helloWorld/better.html");
    }

    public void helloWorldSuper(AppRequestContext context) {
        context.response().sendTemplateHtml("/templates/demos/helloWorld/super.html");
    }
}
