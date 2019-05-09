package org.spincast.plugins.cssautoprefixer;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.config.SpincastConfig;
import org.spincast.core.json.JsonManager;
import org.spincast.core.json.JsonObject;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.core.utils.SpincastUtils;
import org.spincast.plugins.cssautoprefixer.config.SpincastCssAutoprefixerConfig;
import org.spincast.plugins.processutils.ExecutionOutputStrategy;
import org.spincast.plugins.processutils.SpincastProcessUtils;
import org.spincast.plugins.processutils.SyncExecutionResult;
import org.spincast.shaded.org.apache.commons.codec.digest.DigestUtils;
import org.spincast.shaded.org.apache.commons.io.FileUtils;
import org.spincast.shaded.org.apache.commons.lang3.StringUtils;
import org.spincast.shaded.org.apache.commons.lang3.SystemUtils;

import com.google.common.collect.Lists;
import com.google.inject.Inject;

/**
 * CSS Autoprefixer manager.
 */
public class SpincastCssAutoprefixerManagerDefault implements SpincastCssAutoprefixerManager {

    protected static final Logger logger = LoggerFactory.getLogger(SpincastCssAutoprefixerManagerDefault.class);

    private final SpincastUtils spincastUtils;
    private final SpincastProcessUtils spincastProcessUtils;
    private final SpincastConfig spincastConfig;
    private final SpincastCssAutoprefixerConfig spincastCssAutoprefixerConfig;
    private final JsonManager jsonManager;

    private File postCssConfigFileDirsParentDir = null;
    private File nodeGlobalDir = null;

    private boolean isValidAutoprefixerEnvironment = false;
    private boolean isValidAutoprefixerEnvironmentEvaluated = false;
    private Object isValidAutoprefixerEnvironmentLock = new Object();


    @Inject
    public SpincastCssAutoprefixerManagerDefault(SpincastUtils spincastUtils,
                                                 SpincastProcessUtils spincastProcessUtils,
                                                 SpincastConfig spincastConfig,
                                                 SpincastCssAutoprefixerConfig spincastCssAutoprefixerConfig,
                                                 JsonManager jsonManager) {
        this.spincastUtils = spincastUtils;
        this.spincastProcessUtils = spincastProcessUtils;
        this.spincastConfig = spincastConfig;
        this.spincastCssAutoprefixerConfig = spincastCssAutoprefixerConfig;
        this.jsonManager = jsonManager;
    }

    protected SpincastUtils getSpincastUtils() {
        return this.spincastUtils;
    }

    protected SpincastProcessUtils getSpincastProcessUtils() {
        return this.spincastProcessUtils;
    }

    protected SpincastConfig getSpincastConfig() {
        return this.spincastConfig;
    }

    protected SpincastCssAutoprefixerConfig getSpincastCssAutoprefixerConfig() {
        return this.spincastCssAutoprefixerConfig;
    }

    protected JsonManager getJsonManager() {
        return this.jsonManager;
    }

    @Override
    public boolean isValidAutoprefixerEnvironment() {

        if (this.isValidAutoprefixerEnvironmentEvaluated) {
            return this.isValidAutoprefixerEnvironment;
        }

        synchronized (this.isValidAutoprefixerEnvironmentLock) {

            if (this.isValidAutoprefixerEnvironmentEvaluated) {
                return this.isValidAutoprefixerEnvironment;
            }
            this.isValidAutoprefixerEnvironmentEvaluated = true;

            try {
                String css = "::placeholder {" +
                             "    color: gray;" +
                             "}";
                autoPrefix(css);
                return true;
            } catch (Exception ex) {
                logger.info("Autoprefixer doesn't seem to be installed properly:\n" + SpincastStatics.getStackTrace(ex));
                logger.info("Read https://www.spincast.org/plugins/spincast-css-autoprefixer in order " +
                            "to learn how to install it!");
                return false;
            }
        }
    }

    protected String getNomExecutableName() {
        String postcssExecutable = "npm";
        if (SystemUtils.IS_OS_WINDOWS) {
            postcssExecutable += ".cmd";
        }
        return postcssExecutable;
    }

    protected File getNodeGlobalDir() {

        if (this.nodeGlobalDir == null) {
            try {
                SyncExecutionResult result = getSpincastProcessUtils().executeSync(30,
                                                                                   TimeUnit.SECONDS,
                                                                                   ExecutionOutputStrategy.BUFFER,
                                                                                   getNomExecutableName(),
                                                                                   "root",
                                                                                   "-g");
                int exitCode = result.getExitCode();
                if (exitCode != 0) {
                    throw new RuntimeException("Exited with " + exitCode);
                }

                if (result.getSystemOutLines().size() != 1) {
                    throw new RuntimeException("Was expecting one line, the version: " + result.getSystemOutLines());
                }

                String line = result.getSystemOutLines().get(0);
                File temp = new File(line);
                if (!temp.isDirectory()) {
                    throw new RuntimeException("Command 'npm root -g' returned an invalid directory!: " +
                                               this.nodeGlobalDir.getAbsolutePath());
                }
                this.nodeGlobalDir = temp;
            } catch (Exception ex) {
                throw SpincastStatics.runtimize(ex);
            }
        }

        return this.nodeGlobalDir;
    }

    @Override
    public void autoPrefix(File cssFile) {
        autoPrefix(cssFile, StandardCharsets.UTF_8, null);
    }

    @Override
    public void autoPrefix(File cssFile, JsonObject options) {
        autoPrefix(cssFile, StandardCharsets.UTF_8, options);
    }

    @Override
    public void autoPrefix(File cssFile, Charset encoding) {
        autoPrefix(cssFile, encoding, null);
    }

    @Override
    public void autoPrefix(File cssFile, Charset encoding, JsonObject options) {
        if (!cssFile.isFile()) {
            throw new RuntimeException("The specified file doesn't exit: " + cssFile.getAbsolutePath());
        }

        try {
            String content = FileUtils.readFileToString(cssFile, encoding);
            content = autoPrefix(content, options);
            FileUtils.writeStringToFile(cssFile, content, encoding);
        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    @Override
    public String autoPrefix(String cssContent) {
        return autoPrefix(cssContent, null);
    }

    @Override
    public String autoPrefix(String cssContent, JsonObject options) {

        if (StringUtils.isBlank(cssContent)) {
            return "";
        }

        File tempCssFile = null;
        try {

            if (options == null) {
                options = getJsonManager().create();
            }

            tempCssFile = new File(getSpincastUtils().createTempFilePath() + ".css");
            FileUtils.write(tempCssFile, cssContent, "UTF-8");

            String postcssExecutableName = getSpincastCssAutoprefixerConfig().getPostcssExecutableName();

            File postCssConfigFileDir = getPostCssConfigFileDir(options);
            createPostCssConfigFile(postCssConfigFileDir, options);

            List<String> args = getCommandArgs(postcssExecutableName, tempCssFile, postCssConfigFileDir);

            //==========================================
            // Execute "postcss"!
            //==========================================
            logger.info("Executing: " + StringUtils.join(args, " "));
            SyncExecutionResult result = getSpincastProcessUtils().executeSync(getAutoprefixCommandMaxNbrMinutes(),
                                                                               TimeUnit.MINUTES,
                                                                               args);
            if (result.getExitCode() != 0) {
                throw new RuntimeException("Program did not exit with code '0': " + result.getExitCode());
            }

            String resultContent = FileUtils.readFileToString(tempCssFile, "UTF-8");
            return resultContent;

        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        } finally {
            if (tempCssFile != null && tempCssFile.isFile()) {
                FileUtils.deleteQuietly(tempCssFile);
            }
        }
    }

    protected int getAutoprefixCommandMaxNbrMinutes() {
        return 1;
    }

    protected File getPostCssConfigFileDirsParentDir() {

        if (this.postCssConfigFileDirsParentDir == null) {

            try {
                this.postCssConfigFileDirsParentDir =
                        new File(getSpincastConfig().getTempDir(),
                                 getClass().getSimpleName() + "/postCssConfigFileDirsParentDir");
                FileUtils.forceMkdir(this.postCssConfigFileDirsParentDir);
            } catch (Exception ex) {
                throw SpincastStatics.runtimize(ex);
            }
        }
        return this.postCssConfigFileDirsParentDir;
    }

    /**
     * Return a directory where to find a
     * "postcss.config.js" serving as the
     * configurations for "postcss".
     * <p>
     * Return <code>null</code> if no configuration
     * file is required.
     */
    protected File getPostCssConfigFileDir(JsonObject options) {
        File postCssConfigFileDirsParentDir = getPostCssConfigFileDirsParentDir();

        String hash = DigestUtils.md5Hex(options.toJsonString());
        File postCssConfigFileDir = new File(postCssConfigFileDirsParentDir, hash);
        if (!postCssConfigFileDir.isDirectory()) {
            try {
                FileUtils.forceMkdir(postCssConfigFileDir);
            } catch (Exception ex) {
                throw SpincastStatics.runtimize(ex);
            }
        }
        return postCssConfigFileDir;
    }

    protected void createPostCssConfigFile(File postCssConfigFileDir, JsonObject options) {

        File configFile = new File(postCssConfigFileDir, "postcss.config.js");
        if (configFile.isFile()) {
            return;
        }

        File nodeGlobalDir = getNodeGlobalDir();
        String nodeGlobalDirJsPath = nodeGlobalDir.getAbsolutePath().replace("\\", "\\\\"); // for Windows

        //==========================================
        // The "module.paths.push" is a hack to make
        // sure the global "autoprefixer" library
        // is found. It seems "postcss" doesn't find
        // it otherwise (except if it is also installed in
        // the *current working directory*).
        //==========================================
        // @formatter:off
        String content = "module.exports = ctx => {" +
                         "    try {" +
                         "        module.paths.push('" + nodeGlobalDirJsPath + "');" +
                         "        return ({" +
                         "            plugins: [" +
                         "                require('autoprefixer')(" + options.toJsonString(false) + ")" +
                         "            ]" +
                         "        })" +
                         "    } finally {" +
                         "        module.paths.pop();" +
                         "    }" +
                         "}";
        // @formatter:on

        try {
            FileUtils.writeStringToFile(configFile, content, "UTF-8");
        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    /**
     * Create arguments for the "postcss" program.
     */
    protected List<String> getCommandArgs(String postcssExecutableName,
                                          File tempCssFile,
                                          File postCssConfigFileDir) {

        ArrayList<String> args = Lists.newArrayList(postcssExecutableName,
                                                    tempCssFile.getAbsolutePath(),
                                                    "--no-map",
                                                    "--config",
                                                    postCssConfigFileDir.getAbsolutePath(),
                                                    "-o",
                                                    tempCssFile.getAbsolutePath());

        return args;
    }


}

