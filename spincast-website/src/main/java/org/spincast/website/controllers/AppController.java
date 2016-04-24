package org.spincast.website.controllers;

import java.net.URL;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.exceptions.NotFoundException;
import org.spincast.core.guice.MainArgs;
import org.spincast.core.json.IJsonManager;
import org.spincast.core.utils.ISpincastUtils;
import org.spincast.website.AppConstants;
import org.spincast.website.exchange.IAppRequestContext;

import com.google.common.collect.Lists;
import com.google.inject.Inject;

import net.logstash.logback.encoder.org.apache.commons.lang.StringUtils;

public class AppController {

    protected final Logger logger = LoggerFactory.getLogger(AppController.class);

    private final String[] mainArgs;
    private final IJsonManager jsonManager;
    private final ISpincastUtils spincastUtils;

    @Inject
    public AppController(@MainArgs String[] mainArgs,
                         IJsonManager jsonManager,
                         ISpincastUtils spincastUtils) {
        this.mainArgs = mainArgs;
        this.jsonManager = jsonManager;
        this.spincastUtils = spincastUtils;
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
        // Latest stable Spincast version
        //==========================================

        // TODO
        //String latestStableVersion = getSpincastUtils().getSpincastLatestStableVersion();
        String latestStableVersion = getSpincastUtils().getSpincastCurrentVersion();
        context.templating().addTemplatingGlobalVariable("spincastLatestStableVersion", latestStableVersion);
        context.templating().addTemplatingGlobalVariable("spincastLatestStableVersionIsSnapshot",
                                                         latestStableVersion.contains("-SNAPSHOT"));
        //==========================================
        // The current Spincast version
        //==========================================
        String currentVersion = getSpincastUtils().getSpincastCurrentVersion();
        context.templating().addTemplatingGlobalVariable("spincastCurrrentVersion", currentVersion);
        context.templating().addTemplatingGlobalVariable("spincastCurrrentVersionIsSnapshot",
                                                         currentVersion.contains("-SNAPSHOT"));
    }

    public void index(IAppRequestContext context) {

        //        if(1==1) {
        //            throw new RuntimeException("test");
        //        }

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
            // We specified the classes of the current section so the 
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
}
