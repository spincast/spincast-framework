package org.spincast.website.controllers;

import java.net.URL;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.exceptions.NotFoundException;
import org.spincast.core.guice.MainArgs;
import org.spincast.core.json.IJsonManager;
import org.spincast.core.json.IJsonObject;
import org.spincast.core.utils.ISpincastUtils;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.shaded.org.apache.commons.lang3.StringUtils;
import org.spincast.website.AppConstants;
import org.spincast.website.IAppConfig;
import org.spincast.website.exchange.IAppRequestContext;
import org.spincast.website.models.INewsEntriesAndTotalNbr;
import org.spincast.website.models.INewsEntry;
import org.spincast.website.services.INewsService;

import com.google.common.collect.Lists;
import com.google.inject.Inject;

public class MainPagesController {

    protected final Logger logger = LoggerFactory.getLogger(MainPagesController.class);

    private final String[] mainArgs;
    private final IJsonManager jsonManager;
    private final ISpincastUtils spincastUtils;
    private final INewsService newsService;
    private final IAppConfig appConfig;

    @Inject
    public MainPagesController(@MainArgs String[] mainArgs,
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
        context.response().sendTemplateHtml("/templates/index.html", (IJsonObject)null);
    }

    public void presentation(IAppRequestContext context) {
        context.response().sendTemplateHtml("/templates/presentation.html", (IJsonObject)null);
    }

    public void documentation(IAppRequestContext context) {
        context.response().sendTemplateHtml("/templates/documentation.html", (IJsonObject)null);
    }

    public void download(IAppRequestContext context) {
        context.response().sendTemplateHtml("/templates/download.html", (IJsonObject)null);
    }

    public void plugins(IAppRequestContext context) {
        context.response().sendTemplateHtml("/templates/plugins.html", (IJsonObject)null);
    }

    public void community(IAppRequestContext context) {
        context.response().sendTemplateHtml("/templates/community.html", (IJsonObject)null);
    }

    public void about(IAppRequestContext context) {
        context.response().sendTemplateHtml("/templates/about.html", (IJsonObject)null);
    }

    public void more(IAppRequestContext context) {
        context.response().sendTemplateHtml("/templates/more.html", (IJsonObject)null);
    }

    /**
     * A Plugin documentation
     */
    public void plugin(IAppRequestContext context) {

        String pluginName = context.request().getPathParam("pluginName");

        String pluginDocTemplatePath = getPluginDocTemplatePath(pluginName);
        if(pluginDocTemplatePath == null) {

            //==========================================
            // We specify the classes of the current section so the 
            // menu can keep track of it, even if the Not Found
            // route is used.
            //==========================================
            context.variables().add(AppConstants.RC_VARIABLE_HTML_SECTION_CLASSES, Lists.newArrayList("plugins"));

            throw new NotFoundException("Plugin not found");
        }

        context.response().sendTemplateHtml(pluginDocTemplatePath, (IJsonObject)null);
    }

    /**
     * Return the classpath path to the plugin documentation template
     * or NULL if not found.
     */
    protected String getPluginDocTemplatePath(String pluginName) {

        if(StringUtils.isBlank(pluginName)) {
            return null;
        }

        //==========================================
        // Sanitization
        //==========================================
        if(!Pattern.matches("[-_0-9a-z]+", pluginName)) {
            this.logger.info("Invalid plugin name tried : " + pluginName);
            return null;
        }

        String pluginDocTemplatePath = "/templates/plugins/" + pluginName + "/" + pluginName + ".html";

        URL url = MainPagesController.class.getResource(pluginDocTemplatePath);
        if(url == null) {
            return null;
        }
        return pluginDocTemplatePath;
    }

    public void news(IAppRequestContext context) {

        int page = 1;
        String pageStr = context.request().getQueryStringParamFirst("page");

        if(pageStr != null) {
            try {
                page = Integer.parseInt(pageStr);
                if(page < 0) {
                    context.response().redirect("/news", false);
                    return;
                }
            } catch(Exception ex) {
                // ok
            }
        }

        int nbrNewsEntriesPerPage = getAppConfig().getNbrNewsEntriesOnNewsPage();

        int startPos = ((page - 1) * nbrNewsEntriesPerPage) + 1;
        int endPos = (startPos - 1) + nbrNewsEntriesPerPage;

        INewsEntriesAndTotalNbr newsEntriesAndTotalNbr = getNewsService().getNewsEntries(startPos, endPos, false);

        if(page > 1 && newsEntriesAndTotalNbr.getNewsEntries().size() == 0) {
            context.response().redirect("/news", false);
            return;
        }

        int nextPage = -1;
        if(newsEntriesAndTotalNbr.getNbrNewsEntriesTotal() > endPos) {
            nextPage = page + 1;
        }

        int nbrPageTotal = (int)Math.floor((newsEntriesAndTotalNbr.getNbrNewsEntriesTotal() - 1) / nbrNewsEntriesPerPage) + 1;

        // @formatter:off
        context.response().sendTemplateHtml("/templates/news.html",
                                            SpincastStatics.params("newsEntries", newsEntriesAndTotalNbr.getNewsEntries(),
                                                                   "currentPage", page,
                                                                   "nextPage", nextPage,
                                                                   "nbrPageTotal", nbrPageTotal));
         // @formatter:on
    }

    public void newsEntry(IAppRequestContext context) {

        long newsId;
        try {
            newsId = Long.parseLong(context.request().getPathParam("newsId"));
        } catch(Exception ex) {
            throw new NotFoundException("The news entry was not found.");
        }

        INewsEntry newsEntry = getNewsService().getNewsEntry(newsId);
        if(newsEntry == null) {
            throw new NotFoundException("The news entry '" + newsId + "' was not found.");
        }

        context.response().sendTemplateHtml("/templates/newsEntry.html",
                                            SpincastStatics.params("newsEntry", newsEntry));
    }

}
