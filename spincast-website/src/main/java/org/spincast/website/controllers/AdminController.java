package org.spincast.website.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.guice.MainArgs;
import org.spincast.core.json.IJsonManager;
import org.spincast.core.utils.ISpincastUtils;
import org.spincast.website.IAppConfig;
import org.spincast.website.exchange.IAppRequestContext;
import org.spincast.website.services.INewsService;

import com.google.inject.Inject;

/**
 * Admin controller
 */
public class AdminController {

    protected final Logger logger = LoggerFactory.getLogger(AdminController.class);

    private final String[] mainArgs;
    private final IJsonManager jsonManager;
    private final ISpincastUtils spincastUtils;
    private final INewsService newsService;
    private final IAppConfig appConfig;

    @Inject
    public AdminController(@MainArgs String[] mainArgs,
                           IJsonManager jsonManager,
                           ISpincastUtils spincastUtils,
                           INewsService newsService,
                           IAppConfig appConfig) {
        this.mainArgs = mainArgs;
        this.jsonManager = jsonManager;
        this.spincastUtils = spincastUtils;
        this.newsService = newsService;
        this.appConfig = appConfig;
    }

    protected String[] getMainArgs() {
        return this.mainArgs;
    }

    protected IJsonManager getJsonManager() {
        return this.jsonManager;
    }

    protected ISpincastUtils getSpincastUtils() {
        return this.spincastUtils;
    }

    protected INewsService getNewsService() {
        return this.newsService;
    }

    protected IAppConfig getAppConfig() {
        return this.appConfig;
    }

    public void index(IAppRequestContext context) {
        context.response().sendHtmlTemplate("/templates/admin/adminIndex.html", null);
    }

    public void news(IAppRequestContext context) {
        context.response().sendHtmlTemplate("/templates/admin/adminNews.html", null);
    }
}
