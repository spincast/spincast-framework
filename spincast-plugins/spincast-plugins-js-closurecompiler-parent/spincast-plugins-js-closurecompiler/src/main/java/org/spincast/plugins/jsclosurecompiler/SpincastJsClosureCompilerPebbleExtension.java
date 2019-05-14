package org.spincast.plugins.jsclosurecompiler;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.spincast.core.config.SpincastConfig;
import org.spincast.core.routing.Router;
import org.spincast.core.server.Server;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.core.utils.SpincastUtils;
import org.spincast.plugins.httpclient.HttpClient;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.plugins.httpclient.builders.GetRequestBuilder;
import org.spincast.plugins.jsclosurecompiler.config.SpincastJsClosureCompilerConfig;
import org.spincast.shaded.org.apache.commons.codec.digest.DigestUtils;
import org.spincast.shaded.org.apache.commons.io.FileUtils;
import org.spincast.shaded.org.apache.commons.lang3.StringUtils;
import org.spincast.shaded.org.apache.http.HttpStatus;

import com.google.inject.Inject;
import com.mitchellbosecke.pebble.extension.AbstractExtension;
import com.mitchellbosecke.pebble.extension.Function;

/**
 * Pebble extension
 */
public class SpincastJsClosureCompilerPebbleExtension extends AbstractExtension {

    public static final String JS_BUNDLE_FUNCTION_ARG_DISABLE_CACHE_BUSTING = "--spincast-no-cache-busting";

    private final SpincastJsClosureCompilerConfig spincastJsClosureCompilerConfig;
    private final SpincastConfig spincastConfig;
    private final SpincastUtils spincastUtils;
    private final Router<?, ?> router;
    private final Server server;
    private final HttpClient httpClient;
    private final SpincastJsClosureCompilerManager spincastJsClosureCompilerManager;
    private final Object jsBundleLock = new Object();

    @Inject
    public SpincastJsClosureCompilerPebbleExtension(SpincastJsClosureCompilerConfig spincastJsClosureCompilerConfig,
                                                    SpincastConfig spincastConfig,
                                                    SpincastUtils spincastUtils,
                                                    Router<?, ?> router,
                                                    Server server,
                                                    HttpClient httpClient,
                                                    SpincastJsClosureCompilerManager spincastJsClosureCompilerManager) {
        this.spincastJsClosureCompilerConfig = spincastJsClosureCompilerConfig;
        this.spincastConfig = spincastConfig;
        this.spincastUtils = spincastUtils;
        this.router = router;
        this.server = server;
        this.httpClient = httpClient;
        this.spincastJsClosureCompilerManager = spincastJsClosureCompilerManager;
    }

    protected SpincastJsClosureCompilerConfig getSpincastJsClosureCompilerConfig() {
        return this.spincastJsClosureCompilerConfig;
    }

    protected SpincastConfig getSpincastConfig() {
        return this.spincastConfig;
    }

    protected SpincastUtils getSpincastUtils() {
        return this.spincastUtils;
    }

    protected Router<?, ?> getRouter() {
        return this.router;
    }

    protected Server getServer() {
        return this.server;
    }

    protected HttpClient getHttpClient() {
        return this.httpClient;
    }

    protected SpincastJsClosureCompilerManager getSpincastJsClosureCompilerManager() {
        return this.spincastJsClosureCompilerManager;
    }

    @Override
    public Map<String, Function> getFunctions() {

        Map<String, Function> functions = new HashMap<String, Function>();

        functions.put(getSpincastJsClosureCompilerConfig().getJsBundlePebbleFunctionName(),
                      new Function() {

                          @Override
                          public List<String> getArgumentNames() {
                              return null;
                          }

                          @Override
                          public Object execute(Map<String, Object> args) {

                              List<String> numericalKeys = args.keySet().stream().filter((key) -> StringUtils.isNumeric(key))
                                                               .sorted()
                                                               .collect(Collectors.toList());

                              List<String> jsFilesUrlRelativePaths = new ArrayList<String>();
                              List<String> cmdArgs = new ArrayList<String>();
                              boolean disableCacheBusting = false;
                              boolean inArgs = false;
                              for (String numericalKey : numericalKeys) {
                                  String val = args.get(numericalKey).toString();

                                  if (!inArgs) {
                                      if (val.startsWith("-")) {
                                          inArgs = true;
                                      } else {
                                          jsFilesUrlRelativePaths.add(val);
                                          continue;
                                      }
                                  }

                                  //==========================================
                                  // Spincast arg?
                                  //==========================================
                                  if (val.startsWith("-spincast") || val.startsWith("--spincast")) {
                                      if (JS_BUNDLE_FUNCTION_ARG_DISABLE_CACHE_BUSTING.equals(val)) {
                                          disableCacheBusting = true;
                                      }
                                      continue;
                                  }

                                  cmdArgs.add(val);
                              }

                              String hash = generateJsBundleHash(jsFilesUrlRelativePaths);
                              File bundleFile = getJsBundleFile(hash);

                              String urlPath = generateJsBundleUrlPath(hash, false);
                              bundleJs(bundleFile, jsFilesUrlRelativePaths, urlPath, cmdArgs);

                              return generateJsBundleUrlPath(hash, !disableCacheBusting);
                          }
                      });


        return functions;
    }

    protected void bundleJs(File bundleFile, List<String> jsFilesUrlRelativePaths, String urlPath, List<String> cmdArgs) {

        if (!getSpincastConfig().isTestingMode() && bundleFile.isFile() && getServer().getStaticResourceServed(urlPath) != null) {
            return;
        }

        synchronized (this.jsBundleLock) {

            if (!getSpincastConfig().isTestingMode() && bundleFile.isFile() &&
                getServer().getStaticResourceServed(urlPath) != null) {
                return;
            }

            try {

                if (!bundleFile.isFile()) {
                    StringBuilder content = new StringBuilder();

                    String publicUrlBase = getSpincastConfig().getPublicUrlBase();
                    if (!publicUrlBase.endsWith("/")) {
                        publicUrlBase += "/";
                    }

                    for (String jsFileUrlRelativePath : jsFilesUrlRelativePaths) {
                        jsFileUrlRelativePath = StringUtils.stripStart(jsFileUrlRelativePath, "/");
                        String url = publicUrlBase + jsFileUrlRelativePath;

                        GetRequestBuilder requestBuilder = getHttpClient().GET(url);
                        if (getSpincastJsClosureCompilerConfig().isJsBundlesIgnoreSslCertificateErrors()) {
                            requestBuilder = requestBuilder.disableSslCertificateErrors();
                        }
                        HttpResponse response = requestBuilder.send();

                        if (response.getStatus() != HttpStatus.SC_OK) {
                            throw new RuntimeException("Invalid response for file '" + url + "' : " + response.getStatus());
                        }
                        content.append(response.getContentAsString() + "\n");
                    }

                    String optimizedContent = getSpincastJsClosureCompilerManager().compile(content.toString(), cmdArgs);

                    FileUtils.writeStringToFile(bundleFile, optimizedContent, "UTF-8");
                }

                //==========================================
                // Add file route to the router, if not
                // already there.
                //==========================================
                if (getServer().getStaticResourceServed(urlPath) == null) {
                    getRouter().file(urlPath).cache(60 * 60 * 24 * 365).pathAbsolute(bundleFile.getAbsolutePath())
                               .handle();
                }

            } catch (Exception ex) {
                throw SpincastStatics.runtimize(ex);
            }
        }
    }

    protected String generateJsBundleHash(List<String> jsFilesUrlRelativePaths) {

        StringBuilder builder = new StringBuilder();
        for (String path : jsFilesUrlRelativePaths) {
            builder.append(path).append("|");
        }

        String hash = DigestUtils.md5Hex(builder.toString());
        return hash;
    }

    protected File getJsBundleFile(String hash) {

        File dir = getSpincastJsClosureCompilerConfig().getJsBundlesDir();
        if (!dir.isDirectory()) {
            try {
                FileUtils.forceMkdir(dir);
            } catch (Exception ex) {
                throw SpincastStatics.runtimize(ex);
            }
        }
        return new File(dir, hash + ".js");
    }

    protected String generateJsBundleUrlPath(String hash, boolean withCacheBuster) {

        String path = getSpincastJsClosureCompilerConfig().getJsBundlesUrlPath();
        if (StringUtils.isBlank(path)) {
            path = "/";
        } else if (!path.endsWith("/")) {
            path += "/";
        }
        if (!path.startsWith("/")) {
            path = "/" + path;
        }

        if (withCacheBuster) {
            path += getSpincastUtils().getCacheBusterCode();
        }

        path += hash + ".js";
        return path;
    }

}
