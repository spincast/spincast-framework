package org.spincast.website.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.website.exchange.IAppRequestContext;

/**
 * Showcase controller
 */
public class ShowcaseController {

    protected final Logger logger = LoggerFactory.getLogger(ShowcaseController.class);

    public void websockets(IAppRequestContext context) {
        context.response().sendHtmlTemplate("/templates/showcase/websockets.html", null);
    }

}
