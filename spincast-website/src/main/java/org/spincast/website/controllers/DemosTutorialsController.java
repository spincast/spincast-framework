package org.spincast.website.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.website.exchange.IAppRequestContext;

/**
 * Demos/Tutorials controller
 */
public class DemosTutorialsController {

    protected final Logger logger = LoggerFactory.getLogger(DemosTutorialsController.class);

    public void helloWorld(IAppRequestContext context) {
        context.response().sendHtmlTemplate("/templates/demos/helloWorld.html", null);
    }

    public void webSockets(IAppRequestContext context) {
        context.response().sendHtmlTemplate("/templates/demos/websockets.html",
                                            SpincastStatics.params("isHttps", context.request().isHttps()));
    }

    public void httpAuthentication(IAppRequestContext context) {
        context.response().sendHtmlTemplate("/templates/demos/httpAuth.html", null);
    }

    public void httpAuthenticationProtectedPage(IAppRequestContext context) {
        context.response().sendHtmlTemplate("/templates/demos/httpAuthProtectedPage.html", null);
    }

    public void fullWebsite(IAppRequestContext context) {
        context.response().sendHtmlTemplate("/templates/demos/fullWebsite.html", null);
    }
    
    public void todoList(IAppRequestContext context) {
        context.response().sendHtmlTemplate("/templates/demos/todoList.html", null);
    }

}
