package org.spincast.plugins.jsclosurecompiler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.mitchellbosecke.pebble.extension.escaper.SafeString;
import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

/**
 * Pebble extension
 */
public class SpincastJsClosureCompilerPebbleExtensionDefault extends AbstractExtension
                                                             implements SpincastJsClosureCompilerPebbleExtension {

    protected static final Logger logger = LoggerFactory.getLogger(SpincastJsClosureCompilerPebbleExtensionDefault.class);

    public static final String JS_BUNDLE_FUNCTION_ARG_DISABLE_CACHE_BUSTING = "--spincast-no-cache-busting";

    protected static final String HASH_LINE_START = "/*hash:";
    protected static final String HASH_LINE_END = "*/";

    private final SpincastJsClosureCompilerConfig spincastJsClosureCompilerConfig;
    private final SpincastConfig spincastConfig;
    private final SpincastUtils spincastUtils;
    private final Router<?, ?> router;
    private final Server server;
    private final HttpClient httpClient;
    private final SpincastJsClosureCompilerManager spincastJsClosureCompilerManager;
    private final ReentrantLock jsBundleLock = new ReentrantLock();

    @Inject
    public SpincastJsClosureCompilerPebbleExtensionDefault(SpincastJsClosureCompilerConfig spincastJsClosureCompilerConfig,
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
                          public Object execute(Map<String, Object> args,
                                                PebbleTemplate self,
                                                EvaluationContext evaluationContext,
                                                int lineNumber) {

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

                              if (jsFilesUrlRelativePaths.size() == 0) {
                                  return "";
                              }

                              //==========================================
                              // Bundling disabled?
                              //==========================================
                              if (getSpincastJsClosureCompilerConfig().isJsBundlesDisabled()) {
                                  logger.info("JS bundling disabled, outputing regular files!");
                                  return bundlingDisabledOutput(jsFilesUrlRelativePaths);
                              }

                              String bundleName = generateJsBundleName(jsFilesUrlRelativePaths);
                              File bundleFile = getJsBundleFile(bundleName);

                              String urlPath = generateJsBundleUrlPath(bundleName, false);
                              boolean bundleAvailable = bundleJs(bundleFile, jsFilesUrlRelativePaths, urlPath, cmdArgs);

                              //==========================================
                              // If the bundle is not done yet, we output
                              // the files as is.
                              //==========================================
                              if (!bundleAvailable) {
                                  logger.info("JS bundle not available yet (being created by another thread), outputing regular files!");
                                  return bundlingDisabledOutput(jsFilesUrlRelativePaths);
                              }

                              String path = generateJsBundleUrlPath(bundleName, !disableCacheBusting);
                              return bundlingOutput(path);
                          }
                      });


        return functions;
    }

    protected Object bundlingDisabledOutput(List<String> jsFilesUrlRelativePaths) {
        StringBuilder builder = new StringBuilder();
        for (String path : jsFilesUrlRelativePaths) {
            builder.append("<script src=\"").append(path).append("\"></script>\n");
        }

        return new SafeString(builder.toString());
    }

    protected Object bundlingOutput(String path) {
        return new SafeString("<script src=\"" + path + "\"></script>\n");
    }

    protected boolean bundleJs(File bundleFile, List<String> jsFilesUrlRelativePaths, String urlPath, List<String> cmdArgs) {

        if (bundleFile.isFile() &&
            getServer().getStaticResourceServed(urlPath) != null) {
            return true;
        }

        if (this.jsBundleLock.tryLock()) {
            try {

                if (bundleFile.isFile() &&
                    getServer().getStaticResourceServed(urlPath) != null) {
                    return true;
                }

                StringBuilder content = new StringBuilder();

                String publicUrlBase = getSpincastConfig().getPublicUrlBase();
                if (!publicUrlBase.endsWith("/")) {
                    publicUrlBase += "/";
                }

                logger.info("Getting JS files via HTTP to bundle them together...");
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

                String all = content.toString();
                String newContentHash = DigestUtils.md5Hex(all.toString());
                logger.info("JS files all gotten. Content hash: " + newContentHash);

                String existingContentHash = getExistingBundleFileHash(bundleFile);

                //==========================================
                // Bundle content has changed?
                //==========================================
                if (existingContentHash == null || !existingContentHash.equals(newContentHash)) {
                    logger.info("Optimizing the JS content...");
                    String optimizedContent = getSpincastJsClosureCompilerManager().compile(all, cmdArgs);
                    logger.info("JS content optimized!");

                    //==========================================
                    // Add the hash
                    //==========================================
                    optimizedContent = HASH_LINE_START + newContentHash + HASH_LINE_END + "\n" + optimizedContent;
                    FileUtils.writeStringToFile(bundleFile, optimizedContent, "UTF-8");
                } else {
                    logger.info("JS content not changed, no need to optimize.");
                }

                //==========================================
                // Add file route to the router, if not
                // already there.
                //==========================================
                if (getServer().getStaticResourceServed(urlPath) == null) {
                    getRouter().file(urlPath).cache(60 * 60 * 24 * 365).pathAbsolute(bundleFile.getAbsolutePath())
                               .handle();
                }

                return true;

            } catch (Exception ex) {
                throw SpincastStatics.runtimize(ex);
            } finally {
                this.jsBundleLock.unlock();
            }
        } else {
            //==========================================
            // The bundle is being created by another
            // thread.
            //==========================================
            return false;
        }
    }

    /**
     * Return the hash used to create the existing
     * bundle oir <code>null</code> if the bundle doesn't
     * exist.
     */
    protected String getExistingBundleFileHash(File bundleFile) {
        if (!bundleFile.isFile()) {
            return null;
        }

        try (BufferedReader buff = new BufferedReader(new FileReader(bundleFile))) {

            String line = buff.readLine();

            if (StringUtils.isBlank(line)) {
                throw new RuntimeException("Unable to read the first line of: " + bundleFile.getAbsolutePath());
            }

            if (!line.startsWith(HASH_LINE_START) || !line.endsWith("*/")) {
                throw new RuntimeException("First line must be '/*hash:xxxxxx*/': " + bundleFile.getAbsolutePath());
            }
            String hash = line.substring(HASH_LINE_START.length(), line.length() - (HASH_LINE_END.length()));
            return hash;

        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    protected String generateJsBundleName(List<String> jsFilesUrlRelativePaths) {
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
