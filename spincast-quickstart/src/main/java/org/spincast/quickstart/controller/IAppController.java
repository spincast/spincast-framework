package org.spincast.quickstart.controller;

import org.spincast.quickstart.exchange.IAppRequestContext;

/**
 * An application controller.
 */
public interface IAppController {

    /**
     * Route handler for the "/" index page.
     */
    public void indexPage(IAppRequestContext context);

    /**
     * Route handler for a "Not Found" request.
     */
    public void notFound(IAppRequestContext context);

    /**
     * Route handler for exceptions handling.
     */
    public void exception(IAppRequestContext context);

}
