package org.spincast.plugins.hotswap.fileswatcher;

import java.io.File;
import java.util.Objects;
import java.util.regex.Pattern;

import org.spincast.core.utils.SpincastStatics;

public class FileToWatch {

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
     * ofClasspath
     */
    public static FileToWatch ofClasspath(String classpathFilePath) {

        Objects.requireNonNull(classpathFilePath, "The classpathFilePath can't be NULL");

        File file = SpincastStatics.getClasspathFile(classpathFilePath);
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
            dir = SpincastStatics.getClasspathFile(dirPath);
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

    @Override
    public String toString() {
        return getDir().getAbsolutePath() + " / " + getFileName();
    }

}
