package org.spincast.website.controllers;

import java.io.File;
import java.net.URL;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.exceptions.NotFoundException;
import org.spincast.core.guice.MainArgs;
import org.spincast.core.json.IJsonManager;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.shaded.org.apache.commons.io.FileUtils;
import org.spincast.website.AppConstants;
import org.spincast.website.exchange.IAppRequestContext;

import com.google.common.collect.Lists;
import com.google.inject.Inject;

import net.logstash.logback.encoder.org.apache.commons.lang.StringUtils;

public class AppController {

    protected final Logger logger = LoggerFactory.getLogger(AppController.class);

    private final String[] mainArgs;
    private final IJsonManager jsonManager;

    @Inject
    public AppController(@MainArgs String[] mainArgs,
                         IJsonManager jsonManager) {
        this.mainArgs = mainArgs;
        this.jsonManager = jsonManager;
    }

    protected String[] getMainArgs() {
        return this.mainArgs;
    }

    protected IJsonManager getJsonManager() {
        return this.jsonManager;
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
        String artifactVersion = getClass().getPackage().getImplementationVersion();

        //==========================================
        // We're in an IDE...
        //==========================================
        if(artifactVersion == null) {
            artifactVersion = getCurrentVersionFromPom();
        }
        context.templating().addTemplatingGlobalVariable("spincastCurrrentVersion", artifactVersion);
        context.templating().addTemplatingGlobalVariable("spincastCurrrentVersionIsSnapshot",
                                                         artifactVersion.contains("-SNAPSHOT"));
    }

    protected String getCurrentVersionFromPom() {

        String artifactVersion = null;
        try {
            File file = new File(".");
            String filePath = file.getAbsolutePath();
            file = new File(filePath);
            File parent = file.getParentFile();
            File pomFile = new File(parent.getAbsolutePath() + "/pom.xml");
            if(pomFile.isFile()) {
                String content = FileUtils.readFileToString(pomFile);
                int pos = content.indexOf("<version>");
                if(pos > 0) {
                    int pos2 = content.indexOf("</version>", pos);
                    if(pos2 > 0) {
                        artifactVersion = content.substring(pos + "<version>".length(), pos2);
                    }
                }
            }
            if(artifactVersion == null) {
                throw new RuntimeException("Version in pom.xml not found");
            }
        } catch(Exception ex) {
            throw new RuntimeException("Unable to get the pom.xml : " + SpincastStatics.getStackTrace(ex));
        }

        return artifactVersion;
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
