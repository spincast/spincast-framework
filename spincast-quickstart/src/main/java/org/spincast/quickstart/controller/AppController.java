package org.spincast.quickstart.controller;

import java.util.HashMap;
import java.util.Map;

import org.spincast.quickstart.config.IAppConfig;
import org.spincast.quickstart.exchange.IAppRequestContext;

import com.google.inject.Inject;

/**
 * Implementation of an application controller.
 */
public class AppController implements IAppController {

    private final IAppConfig appConfig;

    @Inject
    public AppController(IAppConfig appConfig) {
        this.appConfig = appConfig;
    }

    protected IAppConfig getAppConfig() {
        return this.appConfig;
    }

    @Override
    public void indexPage(IAppRequestContext context) {

        //==========================================
        // Render an HTML template with some parameters.
        //==========================================
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("appName", getAppConfig().getAppName());
        variables.put("serverPort", getAppConfig().getHttpServerPort());

        context.response().sendHtmlTemplate("/templates/index.html", variables);
    }

    @Override
    public void notFound(IAppRequestContext context) {
        context.response().sendHtmlTemplate("/templates/notFound.html", null);
    }

    @Override
    public void exception(IAppRequestContext context) {
        context.response().sendHtmlTemplate("/templates/exception.html", null);
    }
}
