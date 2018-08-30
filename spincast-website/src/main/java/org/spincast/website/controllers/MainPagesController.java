package org.spincast.website.controllers;

import java.net.URL;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.exceptions.NotFoundException;
import org.spincast.core.guice.MainArgs;
import org.spincast.core.json.JsonManager;
import org.spincast.core.utils.SpincastUtils;
import org.spincast.shaded.org.apache.commons.lang3.StringUtils;
import org.spincast.website.AppConfig;
import org.spincast.website.AppConstants;
import org.spincast.website.exchange.AppRequestContext;
import org.spincast.website.models.NewsEntriesAndTotalNbr;
import org.spincast.website.models.NewsEntry;
import org.spincast.website.services.NewsService;

import com.google.common.collect.Lists;
import com.google.inject.Inject;

public class MainPagesController {

    protected final Logger logger = LoggerFactory.getLogger(MainPagesController.class);

    private final String[] mainArgs;
    private final JsonManager jsonManager;
    private final SpincastUtils spincastUtils;
    private final NewsService newsService;
    private final AppConfig appConfig;

    @Inject
    public MainPagesController(@MainArgs String[] mainArgs, JsonManager jsonManager, SpincastUtils spincastUtils,
                               NewsService newsService, AppConfig appConfig) {
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
        context.response().sendTemplateHtml("/templates/index.html");
    }

    public void presentation(AppRequestContext context) {
        context.response().sendTemplateHtml("/templates/presentation.html");
    }

    public void documentation(AppRequestContext context) {

        if (context.request().getQueryStringParamFirst("alert") != null) {
            context.response().getModel().set("alertDemoMsg",
                                              "This is an example Success Alert message, using no javascript!");
        }

        context.response().sendTemplateHtml("/templates/documentation.html");
    }

    public void download(AppRequestContext context) {
        context.response().sendTemplateHtml("/templates/download.html");
    }

    public void plugins(AppRequestContext context) {
        context.response().sendTemplateHtml("/templates/plugins.html");
    }

    public void community(AppRequestContext context) {
        context.response().sendTemplateHtml("/templates/community.html");
    }

    public void about(AppRequestContext context) {
        context.response().sendTemplateHtml("/templates/about.html");
    }

    public void more(AppRequestContext context) {
        context.response().sendTemplateHtml("/templates/more.html");
    }

    /**
     * A Plugin documentation
     */
    public void plugin(AppRequestContext context) {

        String pluginName = context.request().getPathParam("pluginName");

        String pluginDocTemplatePath = getPluginDocTemplatePath(pluginName);
        if (pluginDocTemplatePath == null) {

            // ==========================================
            // We specify the classes of the current section so the
            // menu can keep track of it, even if the Not Found
            // route is used.
            // ==========================================
            context.variables().set(AppConstants.RC_VARIABLE_HTML_SECTION_CLASSES, Lists.newArrayList("plugins"));

            throw new NotFoundException("Plugin not found");
        }

        context.response().sendTemplateHtml(pluginDocTemplatePath);
    }

    /**
     * Return the classpath path to the plugin documentation template or NULL if not
     * found.
     */
    protected String getPluginDocTemplatePath(String pluginName) {

        if (StringUtils.isBlank(pluginName)) {
            return null;
        }

        // ==========================================
        // Sanitization
        // ==========================================
        if (!Pattern.matches("[-_0-9a-z]+", pluginName)) {
            this.logger.info("Invalid plugin name tried : " + pluginName);
            return null;
        }

        String pluginDocTemplatePath = "/templates/plugins/" + pluginName + "/" + pluginName + ".html";

        URL url = MainPagesController.class.getResource(pluginDocTemplatePath);
        if (url == null) {
            return null;
        }
        return pluginDocTemplatePath;
    }

    public void news(AppRequestContext context) {

        int page = 1;
        String pageStr = context.request().getQueryStringParamFirst("page");

        if (pageStr != null) {
            try {
                page = Integer.parseInt(pageStr);
                if (page < 0) {
                    context.response().redirect("/news", false);
                    return;
                }
            } catch (Exception ex) {
                // ok
            }
        }

        int nbrNewsEntriesPerPage = getAppConfig().getNbrNewsEntriesOnNewsPage();

        int startPos = ((page - 1) * nbrNewsEntriesPerPage) + 1;
        int endPos = (startPos - 1) + nbrNewsEntriesPerPage;

        NewsEntriesAndTotalNbr newsEntriesAndTotalNbr = getNewsService().getNewsEntries(startPos, endPos, false);

        if (page > 1 && newsEntriesAndTotalNbr.getNewsEntries().size() == 0) {
            context.response().redirect("/news", false);
            return;
        }

        int nextPage = -1;
        if (newsEntriesAndTotalNbr.getNbrNewsEntriesTotal() > endPos) {
            nextPage = page + 1;
        }

        int nbrPageTotal = (int)Math
                                    .floor((newsEntriesAndTotalNbr.getNbrNewsEntriesTotal() - 1) / nbrNewsEntriesPerPage) +
                           1;

        context.response().getModel().set("newsEntries", newsEntriesAndTotalNbr.getNewsEntries());
        context.response().getModel().set("currentPage", page);
        context.response().getModel().set("nextPage", nextPage);
        context.response().getModel().set("nbrPageTotal", nbrPageTotal);

        context.response().sendTemplateHtml("/templates/news.html");

    }

    public void newsEntryTest(AppRequestContext context) {

    }

    public void newsEntry(AppRequestContext context) {

        long newsId;
        try {
            newsId = Long.parseLong(context.request().getPathParam("newsId"));
        } catch (Exception ex) {
            throw new NotFoundException("The news entry was not found.");
        }

        NewsEntry newsEntry = getNewsService().getNewsEntry(newsId);
        if (newsEntry == null) {
            throw new NotFoundException("The news entry '" + newsId + "' was not found.");
        }

        context.response().getModel().set("newsEntry", newsEntry);
        context.response().sendTemplateHtml("/templates/newsEntry.html");
    }

}
