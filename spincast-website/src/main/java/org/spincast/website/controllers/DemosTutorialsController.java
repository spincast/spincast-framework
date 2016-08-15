package org.spincast.website.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.json.IJsonObject;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.website.exchange.IAppRequestContext;

/**
 * Demos/Tutorials controller
 */
public class DemosTutorialsController {

    protected final Logger logger = LoggerFactory.getLogger(DemosTutorialsController.class);

    public void helloWorld(IAppRequestContext context) {
        context.response().sendTemplateHtml("/templates/demos/helloWorld.html", (IJsonObject)null);
    }

    public void webSockets(IAppRequestContext context) {
        context.response().sendTemplateHtml("/templates/demos/websockets.html",
                                            SpincastStatics.params("isHttps", context.request().isHttps()));
    }

    public void httpAuthentication(IAppRequestContext context) {
        context.response().sendTemplateHtml("/templates/demos/httpAuth.html", (IJsonObject)null);
    }

    public void httpAuthenticationProtectedPage(IAppRequestContext context) {
        context.response().sendTemplateHtml("/templates/demos/httpAuthProtectedPage.html", (IJsonObject)null);
    }

    public void fullWebsite(IAppRequestContext context) {
        context.response().sendTemplateHtml("/templates/demos/fullWebsite.html", (IJsonObject)null);
    }

    public void todoList(IAppRequestContext context) {
        context.response().sendTemplateHtml("/templates/demos/todoList.html", (IJsonObject)null);
    }
}
