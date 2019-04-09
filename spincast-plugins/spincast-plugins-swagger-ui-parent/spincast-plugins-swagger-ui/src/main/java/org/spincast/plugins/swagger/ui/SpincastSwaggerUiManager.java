package org.spincast.plugins.swagger.ui;

import java.io.File;
import java.nio.file.Files;

import org.spincast.core.config.SpincastConfig;
import org.spincast.core.routing.Router;
import org.spincast.core.templating.TemplatingEngine;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.core.utils.SpincastUtils;
import org.spincast.plugins.swagger.ui.config.SpincastSwaggerUiConfig;
import org.spincast.shaded.org.apache.commons.io.FileUtils;

import com.google.inject.Inject;

public class SpincastSwaggerUiManager {

    private final Router<?, ?> router;
    private final SpincastUtils spincastUtils;
    private final SpincastConfig spincastConfig;
    private final TemplatingEngine templatingEngine;
    private final SpincastSwaggerUiConfig spincastSwaggerUiConfig;

    @Inject
    public SpincastSwaggerUiManager(Router<?, ?> router,
                                    SpincastUtils spincastUtils,
                                    SpincastSwaggerUiConfig spincastSwaggerUiConfig,
                                    TemplatingEngine templatingEngine,
                                    SpincastConfig spincastConfig) {
        this.router = router;
        this.spincastUtils = spincastUtils;
        this.spincastSwaggerUiConfig = spincastSwaggerUiConfig;
        this.templatingEngine = templatingEngine;
        this.spincastConfig = spincastConfig;
    }

    @Inject
    public void init() {
        serverSwaggerUi();
    }

    protected Router<?, ?> getRouter() {
        return this.router;
    }

    protected SpincastUtils getSpincastUtils() {
        return this.spincastUtils;
    }

    protected TemplatingEngine getTemplatingEngine() {
        return this.templatingEngine;
    }

    protected SpincastConfig getSpincastConfig() {
        return this.spincastConfig;
    }

    protected SpincastSwaggerUiConfig getSpincastSwaggerUiConfig() {
        return this.spincastSwaggerUiConfig;
    }

    protected void serverSwaggerUi() {

        String swaggerUiFilesRoot = getSpincastConfig().getTempDir() + "/plugins/swagger-ui";
        getRouter().dir(getSpincastSwaggerUiConfig().getSwaggerUiPath())
                   .pathAbsolute(swaggerUiFilesRoot)
                   .handle((context) -> {

                       try {
                           String absolutePath = context.request().getRequestPath();
                           String path = absolutePath.substring(getSpincastSwaggerUiConfig().getSwaggerUiPath().length());

                           File index = new File(swaggerUiFilesRoot + "/index.html");
                           if (!index.exists()) {
                               synchronized (this) {
                                   if (!index.exists()) {
                                       getSpincastUtils().copyClasspathDirToFileSystem("/spincast/plugins/swagger-ui",
                                                                                       new File(swaggerUiFilesRoot));

                                       //==========================================
                                       // Tweak the index.html file
                                       //==========================================
                                       File indexFile = new File(swaggerUiFilesRoot, "index.html");

                                       //==========================================
                                       // Set the correct specs file location
                                       //==========================================
                                       String specsUrl = getSpincastSwaggerUiConfig().getOpenApiSpecificationsUrl();
                                       String content = FileUtils.readFileToString(indexFile, "UTF-8");
                                       content = content.replace("https://petstore.swagger.io/v2/swagger.json", specsUrl);

                                       //==========================================
                                       // Remove top bar?
                                       //==========================================
                                       if (!getSpincastSwaggerUiConfig().showTopBar()) {
                                           content =
                                                   content.replace("layout: \"StandaloneLayout\"", "layout: \"BaseLayout\"");
                                       }

                                       FileUtils.write(indexFile, content, "UTF-8");

                                   }
                               }
                           }

                           boolean isIndex = path.equals("") || path.equals("/") || path.equals("/index.html");

                           File toSend = new File(swaggerUiFilesRoot + (isIndex ? "/index.html" : path));

                           String mimeTypeFromPath = ContentTypeDefaults.HTML.getMainVariationWithUtf8Charset();
                           if (!isIndex) {
                               mimeTypeFromPath = getSpincastUtils().getMimeTypeFromPath(path);

                           }
                           byte[] bytes;
                           try {
                               bytes = Files.readAllBytes(toSend.toPath());
                           } catch (Exception ex) {
                               throw SpincastStatics.runtimize(ex);
                           }
                           context.response().sendBytes(bytes, mimeTypeFromPath);
                       } catch (Exception ex) {
                           throw SpincastStatics.runtimize(ex);
                       }
                   });

    }

}
