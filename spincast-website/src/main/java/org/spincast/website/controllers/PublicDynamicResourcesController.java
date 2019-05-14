package org.spincast.website.controllers;

import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.config.SpincastConfig;
import org.spincast.core.json.JsonObject;
import org.spincast.core.templating.TemplatingEngine;
import org.spincast.core.utils.SpincastUtils;
import org.spincast.plugins.cssyuicompressor.SpincastCssYuiCompressorManager;
import org.spincast.plugins.jsclosurecompiler.SpincastJsClosureCompilerManager;
import org.spincast.shaded.org.apache.commons.lang3.StringUtils;
import org.spincast.website.exchange.AppRequestContext;

import com.google.inject.Inject;

public class PublicDynamicResourcesController /* No extends BaseController! */ {

    protected static final Logger logger = LoggerFactory.getLogger(PublicDynamicResourcesController.class);

    private final TemplatingEngine templatingEngine;
    private final SpincastConfig spincastConfig;
    private final SpincastUtils spincastUtils;
    private final SpincastCssYuiCompressorManager spincastCssYuiCompressorManager;
    private final SpincastJsClosureCompilerManager spincastJsClosureCompilerManager;

    @Inject
    public PublicDynamicResourcesController(TemplatingEngine templatingEngine,
                                            SpincastConfig spincastConfig,
                                            SpincastUtils spincastUtils,
                                            SpincastCssYuiCompressorManager spincastCssYuiCompressorManager,
                                            SpincastJsClosureCompilerManager spincastJsClosureCompilerManager) {
        this.templatingEngine = templatingEngine;
        this.spincastConfig = spincastConfig;
        this.spincastUtils = spincastUtils;
        this.spincastCssYuiCompressorManager = spincastCssYuiCompressorManager;
        this.spincastJsClosureCompilerManager = spincastJsClosureCompilerManager;
    }

    protected TemplatingEngine getTemplatingEngine() {
        return this.templatingEngine;
    }

    protected SpincastConfig getSpincastConfig() {
        return this.spincastConfig;
    }

    protected SpincastUtils getSpincastUtils() {
        return this.spincastUtils;
    }

    protected SpincastCssYuiCompressorManager getSpincastCssYuiCompressorManager() {
        return this.spincastCssYuiCompressorManager;
    }

    protected SpincastJsClosureCompilerManager getSpincastJsClosureCompilerManager() {
        return this.spincastJsClosureCompilerManager;
    }

    /**
     * Dynamic JS
     */
    public void dynJs(AppRequestContext context) {

        String fileRelativePath = context.request().getPathParam("fileRelativePath");
        if (StringUtils.isBlank(fileRelativePath) || !Pattern.matches("[0-9a-zA-Z_\\-\\./]+\\.js", fileRelativePath) ||
            fileRelativePath.contains("..")) {
            context.response().sendCharacters("", "application/javascript");
            return;
        }

        String templatePath = "/templates/publicdyn/js/" + fileRelativePath;
        if (this.getClass().getResource(templatePath) == null) {
            context.response().sendCharacters("", "application/javascript");
            return;
        }

        try {

            JsonObject params = context.json().create();
            params.set("lang", context.getLocaleToUse().getLanguage());

            String jsContent = getTemplatingEngine().fromTemplate(templatePath, params);

            //==========================================
            // Compress the JS!
            //==========================================
            if (!getSpincastConfig().isDevelopmentMode()) {
                jsContent = getSpincastJsClosureCompilerManager().compile(jsContent);
            }

            context.response().sendCharacters(jsContent, "application/javascript");

        } catch (

        Exception ex) {
            logger.error("Error minifying " + templatePath, ex);
            context.response().sendCharacters("", "application/javascript");
        }
    }

    /**
     * Dynamic CSS
     */
    public void dynCss(AppRequestContext context) {

        String fileRelativePath = context.request().getPathParam("fileRelativePath");
        if (StringUtils.isBlank(fileRelativePath) || !Pattern.matches("[0-9a-zA-Z_\\-\\./]+\\.css", fileRelativePath) ||
            fileRelativePath.contains("..")) {
            context.response().sendCharacters("", "text/css");
            return;
        }

        String templatePath = "/templates/publicdyn/css/" + fileRelativePath;
        if (this.getClass().getResource(templatePath) == null) {
            context.response().sendCharacters("", "text/css");
            return;
        }

        try {
            JsonObject params = context.json().create();
            params.set("lang", context.getLocaleToUse().getLanguage());
            String cssContent = getTemplatingEngine().fromTemplate(templatePath, params);

            //==========================================
            // Minify CSS
            //==========================================
            if (!getSpincastConfig().isDevelopmentMode()) {
                cssContent = getSpincastCssYuiCompressorManager().minify(cssContent);
            }

            context.response().sendCharacters(cssContent, "text/css");

        } catch (Exception ex) {
            logger.error("Error minifying " + templatePath, ex);
            context.response().sendCharacters("", "text/css");
        }
    }

}
