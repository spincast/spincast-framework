package org.spincast.plugins.hotswap.fileswatcher;

import java.io.File;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.shaded.org.apache.commons.lang3.StringUtils;

public class FileToWatch {

    protected final static Logger logger = LoggerFactory.getLogger(FileToWatch.class);

    private static boolean isInJar;
    private static boolean isInJarChecked = false;
    private static final Object isInJarCheckedLock = new Object();

    private final File dir;
    private final String fileName;
    private final boolean isRegEx;
    private Pattern regExPattern;

    private FileToWatch(File dir, String fileName, boolean isRegEx) {
        this.dir = dir;
        this.fileName = fileName;
        this.isRegEx = isRegEx;
    }

    /**
     * ofFileSystem
     */
    public static FileToWatch ofFileSystem(String fileAbsolutePath) {

        Objects.requireNonNull(fileAbsolutePath, "The fileAbsolutePath can't be NULL");

        File file = new File(fileAbsolutePath);
        if (!file.exists()) {
            throw new RuntimeException("The file system's '" + fileAbsolutePath + "' file doesn't exist. It can't be watched.");
        }

        return new FileToWatch(file.getParentFile(), file.getName(), false);
    }

    /**
     * Note that a file from the classpath can only
     * be watched when the application is ran locally
     * in development mode, not when it runs from a .jar!
     */
    public static FileToWatch ofClasspath(String classpathFilePath) {

        Objects.requireNonNull(classpathFilePath, "The classpathFilePath can't be NULL");

        if (isInExecutableJar()) {
            throw new RuntimeException("The classpath file \"" + classpathFilePath + "\" cannot be watched when running from " +
                                       "a jar!");
        }

        File file = getFileFromNotInJarClasspath(classpathFilePath);
        if (!file.exists()) {
            throw new RuntimeException("The classpath '" + classpathFilePath + "' file doesn't exist. It can't be watched.");
        }

        return new FileToWatch(file.getParentFile(), file.getName(), false);
    }

    /**
     * ofRegEx
     */
    public static FileToWatch ofRegEx(String dirPath, String fileNameRegEx, boolean isClassPath) {

        Objects.requireNonNull(dirPath, "The dirPath can't be NULL");
        Objects.requireNonNull(fileNameRegEx, "The fileNameRegEx can't be NULL");

        File dir;
        if (isClassPath) {

            if (isInExecutableJar()) {
                throw new RuntimeException("The classpath file \"" + dirPath + "\" cannot be watched when running from " +
                                           "a jar!");
            }

            dir = getFileFromNotInJarClasspath(dirPath);
        } else {
            dir = new File(dirPath);
        }
        if (!dir.exists()) {
            throw new RuntimeException("The directory '" + dirPath + "' doesn't exist. It can't be watched.");
        }

        return new FileToWatch(dir, fileNameRegEx, true);
    }

    public File getDir() {
        return this.dir;
    }

    public String getFileName() {
        return this.fileName;
    }

    public boolean isRegEx() {
        return this.isRegEx;
    }

    public Pattern getRegExPattern() {
        if (this.regExPattern == null) {
            this.regExPattern = Pattern.compile(getFileName());
        }
        return this.regExPattern;
    }

    protected static File getFileFromNotInJarClasspath(String relativePath) {
        if (relativePath == null) {
            return null;
        }

        try {
            relativePath = StringUtils.stripStart(relativePath, "/");
            URL url = ClassLoader.getSystemResource(relativePath);
            if (url == null) {
                return null;
            }
            return Paths.get(url.toURI()).toFile();
        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    protected static boolean isInExecutableJar() {
        if (!isInJarChecked) {
            synchronized (isInJarCheckedLock) {
                if (!isInJarChecked) {
                    isInJarChecked = true;
                    try {
                        String jarPath = FileToWatch.class.getProtectionDomain().getCodeSource().getLocation().getPath();
                        if (jarPath == null) {
                            throw new RuntimeException("Unable to get the path of " + FileToWatch.class.getName() + "!");
                        }

                        jarPath = URLDecoder.decode(jarPath, "UTF-8");
                        if (!jarPath.toLowerCase().endsWith(".jar")) {
                            isInJar = false;
                        } else {
                            String manifestPath = "jar:file:" + jarPath + "!/META-INF/MANIFEST.MF";
                            Manifest manifest = new Manifest(new URL(manifestPath).openStream());
                            Attributes attr = manifest.getMainAttributes();
                            String mainClass = attr.getValue("Main-Class");
                            isInJar = mainClass != null;
                        }

                    } catch (Exception ex) {
                        throw SpincastStatics.runtimize(ex);
                    }
                }
            }
        }

        return isInJar;
    }

    @Override
    public String toString() {
        return getDir().getAbsolutePath() + " / " + getFileName();
    }

}
