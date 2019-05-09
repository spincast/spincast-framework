package org.spincast.plugins.jsclosurecompiler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.config.SpincastConfig;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.core.utils.SpincastUtils;
import org.spincast.plugins.jsclosurecompiler.config.SpincastJsClosureCompilerConfig;
import org.spincast.plugins.processutils.SpincastProcessUtils;
import org.spincast.plugins.processutils.SyncExecutionResult;
import org.spincast.shaded.org.apache.commons.io.FileUtils;
import org.spincast.shaded.org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.javascript.jscomp.Compiler;

public class SpincastJsClosureCompilerManagerDefault implements SpincastJsClosureCompilerManager {

    protected static final Logger logger = LoggerFactory.getLogger(SpincastJsClosureCompilerManagerDefault.class);

    private final SpincastConfig spincastConfig;
    private final SpincastProcessUtils spincastProcessUtils;
    private final SpincastUtils spincastUtils;
    private final SpincastJsClosureCompilerConfig spincastJsClosureCompilerConfig;

    private String closureCompilerJarFilePath;

    @Inject
    public SpincastJsClosureCompilerManagerDefault(SpincastConfig spincastConfig,
                                                   SpincastProcessUtils spincastProcessUtils,
                                                   SpincastUtils spincastUtils,
                                                   SpincastJsClosureCompilerConfig spincastJsClosureCompilerConfig) {
        this.spincastConfig = spincastConfig;
        this.spincastProcessUtils = spincastProcessUtils;
        this.spincastUtils = spincastUtils;
        this.spincastJsClosureCompilerConfig = spincastJsClosureCompilerConfig;
    }

    @Inject
    protected void init() {
        //==========================================
        // Validate the closure compile jar
        //==========================================
        getClosureCompilerJarFilePath();
    }

    protected SpincastConfig getSpincastConfig() {
        return this.spincastConfig;
    }

    protected SpincastProcessUtils getSpincastProcessUtils() {
        return this.spincastProcessUtils;
    }

    protected SpincastUtils getSpincastUtils() {
        return this.spincastUtils;
    }

    protected SpincastJsClosureCompilerConfig getSpincastJsClosureCompilerConfig() {
        return this.spincastJsClosureCompilerConfig;
    }

    protected String getClosureCompilerJarFilePath() {
        if (this.closureCompilerJarFilePath == null) {

            File classDirOrJarFile = getSpincastUtils().getClassLocationDirOrJarFile(Compiler.class);
            if (!getSpincastUtils().isClassLoadedFromJar(Compiler.class)) {
                throw new RuntimeException("Expecting the " + Compiler.class.getName() +
                                           " class to be loaded from a .jar here! But was " +
                                           "loaded from: " + classDirOrJarFile);
            }
            this.closureCompilerJarFilePath = classDirOrJarFile.getAbsolutePath();
        }
        return this.closureCompilerJarFilePath;
    }

    protected int getCompileCommandMaxNbrMinutes() {
        return 1;
    }

    protected File createJsFile(String jsContent) {
        try {
            File jsFile = new File(getSpincastConfig().getTempDir(),
                                   getClass().getSimpleName() + "/" + UUID.randomUUID().toString() + ".js");
            FileUtils.writeStringToFile(jsFile, jsContent, "UTF-8");

            return jsFile;
        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    @Override
    public String compile(String jsContent, String... args) {

        if (StringUtils.isBlank(jsContent)) {
            return "";
        }

        List<String> argsList = SpincastStatics.toList(args, true);
        return compile(jsContent, argsList);
    }

    @Override
    public String compile(String jsContent, List<String> args) {

        if (StringUtils.isBlank(jsContent)) {
            return "";
        }

        File tempJsFile = null;
        try {
            tempJsFile = createJsFile(jsContent);
            return compile(tempJsFile, args);
        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        } finally {
            FileUtils.deleteQuietly(tempJsFile);
        }
    }

    @Override
    public String compile(File jsFile, String... args) {
        Objects.requireNonNull(jsFile, "The jsFile can't be NULL");
        List<String> argsList = SpincastStatics.toList(args, true);
        return compile(jsFile, argsList);
    }

    @Override
    public String compile(File jsFile, List<String> args) {
        Objects.requireNonNull(jsFile, "The jsFile can't be NULL");
        return compile(Lists.newArrayList(jsFile), args);
    }

    @Override
    public String compile(List<File> jsFiles, String... args) {
        if (jsFiles == null || jsFiles.size() == 0) {
            throw new RuntimeException("At least one js file is required");
        }

        List<String> argsList = SpincastStatics.toList(args, true);
        return compile(jsFiles, argsList);
    }

    @Override
    public String compile(List<File> jsFiles, List<String> args) {
        if (jsFiles == null || jsFiles.size() == 0) {
            throw new RuntimeException("At least one js file is required");
        }

        List<String> argsList = args != null ? args : new ArrayList<String>();

        for (File jsFile : jsFiles) {
            if (jsFile == null) {
                throw new RuntimeException("Specified file is null");
            }
            argsList.add(jsFile.getAbsolutePath());
        }

        return compileCustom(argsList);
    }

    @Override
    public String compileCustom(String... args) {
        if (args == null || args.length == 0) {
            throw new RuntimeException("You at least have to specify " +
                                       "the js files to target.");
        }

        return compileCustom(SpincastStatics.toList(args, true));
    }

    @Override
    public String compileCustom(List<String> args) {
        if (args == null || args.size() == 0) {
            throw new RuntimeException("You at least have to specify " +
                                       "the js files to target.");
        }

        File tempJsFile = null;
        try {

            tempJsFile = createJsFile("");

            List<String> argsList = new ArrayList<String>();
            argsList.add(getSpincastJsClosureCompilerConfig().getJavaBinPath());
            argsList.add("-jar");
            argsList.add(getClosureCompilerJarFilePath());
            argsList.add("--js_output_file=" + tempJsFile.getAbsolutePath());

            for (String arg : args) {
                if (arg == null) {
                    throw new RuntimeException("One of the argument is null: " + args);
                }
                if (arg.toLowerCase().startsWith("--js_output_file=")) {
                    logger.warn("You cannot specify the '--js_output_file=' option! This method takes care of it by itself. Ignoring this argument...");
                    continue;

                }

                argsList.add(arg);
            }

            SyncExecutionResult result = getSpincastProcessUtils().executeSync(getCompileCommandMaxNbrMinutes(),
                                                                               TimeUnit.MINUTES,
                                                                               argsList);
            if (result.getExitCode() != 0) {
                throw new RuntimeException("Program did not exit with code '0': " + result.getExitCode());
            }

            String resultContent = FileUtils.readFileToString(tempJsFile, "UTF-8");
            return resultContent;

        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        } finally {
            FileUtils.deleteQuietly(tempJsFile);
        }
    }

}

