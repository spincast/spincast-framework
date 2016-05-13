package org.spincast.website.controllers;

import java.net.URL;
import java.util.List;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.exceptions.NotFoundException;
import org.spincast.core.guice.MainArgs;
import org.spincast.core.json.IJsonManager;
import org.spincast.core.utils.ISpincastUtils;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.shaded.org.apache.commons.lang3.StringUtils;
import org.spincast.website.AppConstants;
import org.spincast.website.exchange.IAppRequestContext;
import org.spincast.website.models.INewsEntry;

import com.google.common.collect.Lists;
import com.google.inject.Inject;

public class AppController {

    protected final Logger logger = LoggerFactory.getLogger(AppController.class);

    private final String[] mainArgs;
    private final IJsonManager jsonManager;
    private final ISpincastUtils spincastUtils;
    private final List<INewsEntry> newsEntries;

    @Inject
    public AppController(@MainArgs String[] mainArgs,
                         IJsonManager jsonManager,
                         ISpincastUtils spincastUtils,
                         List<INewsEntry> newsEntries) {
        this.mainArgs = mainArgs;
        this.jsonManager = jsonManager;
        this.spincastUtils = spincastUtils;
        this.newsEntries = newsEntries;
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

    protected List<INewsEntry> getNewsEntries() {
        return this.newsEntries;
    }

    /**
     * This will add some global variables that will be 
     * available to any templating engine evaluation.
     */
    public void addGlobalTemplatingVariables(IAppRequestContext context) {

        //==========================================
        // The Language abreviation
        //==========================================
        context.templating().addTemplatingGlobalVariable("langAbrv", context.getLocaleToUse().getLanguage());

        //==========================================
        // The current Spincast version
        //==========================================
        String currentVersion = getSpincastUtils().getSpincastCurrentVersion();
        context.templating().addTemplatingGlobalVariable("spincastCurrrentVersion", currentVersion);
        context.templating().addTemplatingGlobalVariable("spincastCurrrentVersionIsSnapshot",
                                                         currentVersion.contains("-SNAPSHOT"));
    }

    public void index(IAppRequestContext context) {
        context.response().sendHtmlTemplate("/templates/index.html", null);
    }

    public void documentation(IAppRequestContext context) {
        context.response().sendHtmlTemplate("/templates/documentation.html", null);
    }

    public void download(IAppRequestContext context) {
        context.response().sendHtmlTemplate("/templates/download.html", null);
    }

    public void plugins(IAppRequestContext context) {
        context.response().sendHtmlTemplate("/templates/plugins.html", null);
    }

    public void community(IAppRequestContext context) {
        context.response().sendHtmlTemplate("/templates/community.html", null);
    }

    public void about(IAppRequestContext context) {
        context.response().sendHtmlTemplate("/templates/about.html", null);
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

        context.response().sendHtmlTemplate(pluginDocTemplatePath, null);
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

        URL url = AppController.class.getResource(pluginDocTemplatePath);
        if(url == null) {
            return null;
        }
        return pluginDocTemplatePath;
    }

    public void news(IAppRequestContext context) {

        context.response().sendHtmlTemplate("/templates/news.html",
                                            SpincastStatics.params("newsEntries", getNewsEntries()));
    }
}
