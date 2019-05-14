package org.spincast.plugins.cssyuicompressor;

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
import org.spincast.plugins.cssyuicompressor.config.SpincastCssYuiCompressorConfig;
import org.spincast.plugins.httpclient.HttpClient;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.plugins.httpclient.builders.GetRequestBuilder;
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
public class SpincastCssYuiCompressorPebbleExtension extends AbstractExtension {

    public static final String CSS_BUNDLE_FUNCTION_ARG_DISABLE_CACHE_BUSTING = "--spincast-no-cache-busting";
    public static final String CSS_BUNDLE_FUNCTION_ARG_LINE_BREAK_POS = "--line-break-pos";

    private final SpincastCssYuiCompressorConfig spincastCssYuiCompressorConfig;
    private final SpincastConfig spincastConfig;
    private final SpincastUtils spincastUtils;
    private final Router<?, ?> router;
    private final Server server;
    private final HttpClient httpClient;
    private final SpincastCssYuiCompressorManager spincastCssYuiCompressorManager;
    private final Object cssBundleLock = new Object();

    @Inject
    public SpincastCssYuiCompressorPebbleExtension(SpincastCssYuiCompressorConfig spincastCssYuiCompressorConfig,
                                                   SpincastConfig spincastConfig,
                                                   SpincastUtils spincastUtils,
                                                   Router<?, ?> router,
                                                   Server server,
                                                   HttpClient httpClient,
                                                   SpincastCssYuiCompressorManager spincastCssYuiCompressorManager) {
        this.spincastCssYuiCompressorConfig = spincastCssYuiCompressorConfig;
        this.spincastConfig = spincastConfig;
        this.spincastUtils = spincastUtils;
        this.router = router;
        this.server = server;
        this.httpClient = httpClient;
        this.spincastCssYuiCompressorManager = spincastCssYuiCompressorManager;
    }

    protected SpincastCssYuiCompressorConfig getSpincastCssYuiCompressorConfig() {
        return this.spincastCssYuiCompressorConfig;
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

    protected SpincastCssYuiCompressorManager getSpincastCssYuiCompressorManager() {
        return this.spincastCssYuiCompressorManager;
    }

    @Override
    public Map<String, Function> getFunctions() {

        Map<String, Function> functions = new HashMap<String, Function>();

        functions.put(getSpincastCssYuiCompressorConfig().getCssBundlePebbleFunctionName(),
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

                              List<String> cssFilesUrlRelativePaths = new ArrayList<String>();
                              boolean lineBreakPosNext = false;
                              int lineBreakPos = -1;
                              boolean disableCacheBusting = false;
                              boolean inArgs = false;
                              for (String numericalKey : numericalKeys) {
                                  String val = args.get(numericalKey).toString();

                                  if (!inArgs) {
                                      if (val.startsWith("-")) {
                                          inArgs = true;
                                      } else {
                                          cssFilesUrlRelativePaths.add(val);
                                          continue;
                                      }
                                  }

                                  //==========================================
                                  // Spincast arg?
                                  //==========================================
                                  if (val.startsWith("-spincast") || val.startsWith("--spincast")) {
                                      if (CSS_BUNDLE_FUNCTION_ARG_DISABLE_CACHE_BUSTING.equals(val)) {
                                          disableCacheBusting = true;
                                      }
                                      continue;
                                  }

                                  //==========================================
                                  // Only one YUI arg and its value for now
                                  //==========================================
                                  if (CSS_BUNDLE_FUNCTION_ARG_LINE_BREAK_POS.equals(val)) {
                                      lineBreakPosNext = true;
                                  } else if (lineBreakPosNext) {
                                      lineBreakPosNext = false;
                                      try {
                                          lineBreakPos = Integer.parseInt(val);
                                      } catch (Exception ex) {
                                          throw SpincastStatics.runtimize(ex);
                                      }
                                  }
                              }

                              String hash = generateCssBundleHash(cssFilesUrlRelativePaths);
                              File bundleFile = getCssBundleFile(hash);

                              String urlPath = generateCssBundleUrlPath(hash, false);
                              bundleCss(bundleFile, cssFilesUrlRelativePaths, urlPath, lineBreakPos);

                              return generateCssBundleUrlPath(hash, !disableCacheBusting);
                          }
                      });


        return functions;
    }

    protected void bundleCss(File bundleFile, List<String> cssFilesUrlRelativePaths, String urlPath, int lineBreakPos) {

        if (!getSpincastConfig().isTestingMode() &&
            bundleFile.isFile() &&
            getServer().getStaticResourceServed(urlPath) != null) {
            return;
        }

        synchronized (this.cssBundleLock) {

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

                    for (String cssFileUrlRelativePath : cssFilesUrlRelativePaths) {
                        cssFileUrlRelativePath = StringUtils.stripStart(cssFileUrlRelativePath, "/");
                        String url = publicUrlBase + cssFileUrlRelativePath;

                        GetRequestBuilder requestBuilder = getHttpClient().GET(url);
                        if (getSpincastCssYuiCompressorConfig().isCssBundlesIgnoreSslCertificateErrors()) {
                            requestBuilder = requestBuilder.disableSslCertificateErrors();
                        }
                        HttpResponse response = requestBuilder.send();

                        if (response.getStatus() != HttpStatus.SC_OK) {
                            throw new RuntimeException("Invalid response for file '" + url + "' : " + response.getStatus());
                        }
                        content.append(response.getContentAsString() + "\n");
                    }

                    String optimizedContent = getSpincastCssYuiCompressorManager().minify(content.toString(), lineBreakPos);

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

    protected String generateCssBundleHash(List<String> cssFilesUrlRelativePaths) {

        StringBuilder builder = new StringBuilder();
        for (String path : cssFilesUrlRelativePaths) {
            builder.append(path).append("|");
        }

        String hash = DigestUtils.md5Hex(builder.toString());
        return hash;
    }

    protected File getCssBundleFile(String hash) {

        File dir = getSpincastCssYuiCompressorConfig().getCssBundlesDir();
        if (!dir.isDirectory()) {
            try {
                FileUtils.forceMkdir(dir);
            } catch (Exception ex) {
                throw SpincastStatics.runtimize(ex);
            }
        }
        return new File(dir, hash + ".css");
    }

    protected String generateCssBundleUrlPath(String hash, boolean withCacheBuster) {

        String path = getSpincastCssYuiCompressorConfig().getCssBundlesUrlPath();
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

        path += hash + ".css";
        return path;
    }

}
