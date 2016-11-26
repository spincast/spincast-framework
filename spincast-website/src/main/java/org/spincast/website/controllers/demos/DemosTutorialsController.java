package org.spincast.website.controllers.demos;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.website.exchange.AppRequestContext;

/**
 * Demos/Tutorials controller
 */
public class DemosTutorialsController {

    protected final Logger logger = LoggerFactory.getLogger(DemosTutorialsController.class);

    public void helloWorld(AppRequestContext context) {
        context.response().sendTemplateHtml("/templates/demos/helloWorld.html");
    }

    public void webSockets(AppRequestContext context) {
        context.response().getModel().put("isHttps", context.request().isHttps());
        context.response().sendTemplateHtml("/templates/demos/websockets.html");
    }

    public void httpAuthentication(AppRequestContext context) {
        context.response().sendTemplateHtml("/templates/demos/httpAuth.html");
    }

    public void httpAuthenticationProtectedPage(AppRequestContext context) {
        context.response().sendTemplateHtml("/templates/demos/httpAuthProtectedPage.html");
    }

    public void fullWebsite(AppRequestContext context) {
        context.response().sendTemplateHtml("/templates/demos/fullWebsite.html");
    }

    public void todoList(AppRequestContext context) {
        context.response().sendTemplateHtml("/templates/demos/todoList.html");
    }
}