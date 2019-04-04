package org.spincast.website.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.guice.MainArgs;
import org.spincast.core.json.JsonManager;
import org.spincast.core.utils.SpincastUtils;
import org.spincast.website.AppConfig;
import org.spincast.website.exchange.AppRequestContext;
import org.spincast.website.services.NewsService;

import com.google.inject.Inject;

/**
 * Admin controller
 */
public class AdminController {

    protected static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    private final String[] mainArgs;
    private final JsonManager jsonManager;
    private final SpincastUtils spincastUtils;
    private final NewsService newsService;
    private final AppConfig appConfig;

    @Inject
    public AdminController(@MainArgs String[] mainArgs,
                           JsonManager jsonManager,
                           SpincastUtils spincastUtils,
                           NewsService newsService,
                           AppConfig appConfig) {
        this.mainArgs = mainArgs;
        this.jsonManager = jsonManager;
        this.spincastUtils = spincastUtils;
        this.newsService = newsService;
        this.appConfig = appConfig;
    }

    protected String[] getMainArgs() {
        return this.mainArgs;
    }

    protected JsonManager getJsonManager() {
        return this.jsonManager;
    }

    protected SpincastUtils getSpincastUtils() {
        return this.spincastUtils;
    }

    protected NewsService getNewsService() {
        return this.newsService;
    }

    protected AppConfig getAppConfig() {
        return this.appConfig;
    }

    public void index(AppRequestContext context) {
        context.response().sendTemplateHtml("/templates/admin/adminIndex.html");
    }

    public void news(AppRequestContext context) {
        context.response().sendTemplateHtml("/templates/admin/adminNews.html");
    }
}
