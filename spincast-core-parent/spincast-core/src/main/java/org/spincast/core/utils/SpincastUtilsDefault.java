package org.spincast.core.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.config.SpincastConfig;
import org.spincast.shaded.org.apache.commons.io.FileUtils;
import org.spincast.shaded.org.apache.commons.io.FilenameUtils;
import org.spincast.shaded.org.apache.commons.io.IOUtils;
import org.spincast.shaded.org.apache.commons.lang3.StringUtils;
import org.spincast.shaded.org.apache.http.NameValuePair;
import org.spincast.shaded.org.apache.http.client.utils.URLEncodedUtils;
import org.spincast.shaded.org.commonjava.mimeparse.MIMEParse;
import org.spincast.shaded.org.jsoup.Jsoup;
import org.spincast.shaded.org.jsoup.safety.Safelist;

import com.google.inject.Inject;

/**
 * Implementations of the Spincast utilities.
 */
public class SpincastUtilsDefault implements SpincastUtils {

    protected static final Logger logger = LoggerFactory.getLogger(SpincastUtilsDefault.class);

    private String cacheBusterCode;
    private final Object cacheBusterCodeLock = new Object();

    private final SpincastConfig spincastConfig;

    private Map<String, String> extensionToMimeTypeMap;

    private File appJarDirectory;
    private boolean appJarDirectoryChecked;
    private final Object appJarDirectoryLock = new Object();

    private File appRootDirectoryNoJar;
    private boolean appRootDirectoryNoJarChecked;
    private final Object appRootDirectoryNoJarLock = new Object();
    private File generatedTempFilesDir;
    private Map<String, Object> fileSystemsLocks;

    @Inject
    public SpincastUtilsDefault(SpincastConfig spincastConfig) {
        this.spincastConfig = spincastConfig;
    }

    protected SpincastConfig getSpincastConfig() {
        return this.spincastConfig;
    }

    protected Map<String, Object> getFileSystemsLocks() {
        if (this.fileSystemsLocks == null) {
            this.fileSystemsLocks = new HashMap<String, Object>();
        }
        return this.fileSystemsLocks;
    }

    protected Object getFileSystemLock(URI jarDirUri) {
        Objects.requireNonNull(jarDirUri, "The jarDirUri can't be NULL");
        String key = jarDirUri.toString();

        Object fileSystemLock = getFileSystemsLocks().get(key);
        if (fileSystemLock == null) {
            synchronized (this) {
                fileSystemLock = getFileSystemsLocks().get(key);
                if (fileSystemLock == null) {
                    fileSystemLock = new Object();
                    getFileSystemsLocks().put(key, fileSystemLock);
                }
            }
        }
        return fileSystemLock;
    }

    protected File getGeneratedTempFilesDir() {
        if (this.generatedTempFilesDir == null || !this.generatedTempFilesDir.isDirectory()) {
            this.generatedTempFilesDir = new File(getSpincastConfig().getTempDir().getAbsolutePath() + "/random_temp_files/");
            if (!this.generatedTempFilesDir.isDirectory()) {
                boolean result = this.generatedTempFilesDir.mkdirs();
                if (!result) {
                    throw new RuntimeException("Unable to create the directory to create temp files: " +
                                               this.generatedTempFilesDir.getAbsolutePath());
                }
            }
        }
        return this.generatedTempFilesDir;
    }

    @Override
    public boolean isContentTypeToSkipGziping(String contentType) {

        if (StringUtils.isBlank(contentType)) {
            return false;
        }
        contentType = contentType.toLowerCase();

        List<String> contentTypesToSkip = getSpincastConfig().getContentTypesToSkipGziping();
        if (contentTypesToSkip != null && contentTypesToSkip.size() > 0) {
            for (String contentTypeToSkip : contentTypesToSkip) {
                if (contentTypeToSkip.endsWith("*")) {
                    contentTypeToSkip = contentTypeToSkip.substring(0, contentTypeToSkip.length() - 1);
                    if (contentType.startsWith(contentTypeToSkip)) {
                        return true;
                    }
                } else if (contentType.equals(contentTypeToSkip) ||
                           contentType.startsWith(contentTypeToSkip + " ") ||
                           contentType.startsWith(contentTypeToSkip + ";")) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String getMimeTypeFromMultipleSources(String responseContentTypeHeader,
                                                 String resourcePath,
                                                 String requestPath) {

        //==========================================
        // Content-Type header defined, we use this.
        //==========================================
        if (responseContentTypeHeader != null) {
            return responseContentTypeHeader;
        }

        //==========================================
        // Check the target file extension.
        //==========================================
        if (resourcePath != null) {
            String mimeType = getMimeTypeFromPath(resourcePath);
            if (mimeType != null) {
                return mimeType;
            }
        }

        //==========================================
        // Check the URL file extension, if any (not required,
        // "/image" for example can point to an ".png".
        //==========================================
        String contentTypeFound = getMimeTypeFromPath(requestPath);
        if (contentTypeFound != null) {
            return contentTypeFound;
        }

        return null;
    }

    @Override
    public String getMimeTypeFromPath(String path) {

        if (StringUtils.isBlank(path)) {
            return null;
        }
        int index = path.lastIndexOf('.');
        if (index != -1 && index != path.length() - 1) {

            String extension = path.substring(index + 1);

            String mimeType = getMimeTypeFromExtension(extension);
            return mimeType;
        }
        return null;
    }

    @Override
    public String getMimeTypeFromExtension(String extension) {

        if (StringUtils.isBlank(extension)) {
            return null;
        }
        extension = extension.trim();
        if (extension.startsWith(".")) {
            extension = extension.substring(1);
        }
        extension = extension.toLowerCase();

        if (this.extensionToMimeTypeMap == null) {

            this.extensionToMimeTypeMap = new HashMap<String, String>();

            this.extensionToMimeTypeMap.put("htm", "text/html");
            this.extensionToMimeTypeMap.put("html", "text/html");
            this.extensionToMimeTypeMap.put("xhtml", "text/html");
            this.extensionToMimeTypeMap.put("css", "text/css");
            this.extensionToMimeTypeMap.put("js", "application/javascript");
            this.extensionToMimeTypeMap.put("json", "application/json");
            this.extensionToMimeTypeMap.put("xml", "application/xml");
            this.extensionToMimeTypeMap.put("dtd", "application/xml-dtd");
            this.extensionToMimeTypeMap.put("xslt", "application/xslt+xml");
            this.extensionToMimeTypeMap.put("txt", "text/plain");
            this.extensionToMimeTypeMap.put("text", "text/plain");
            this.extensionToMimeTypeMap.put("ics", "text/calendar");
            this.extensionToMimeTypeMap.put("csv", "text/csv");
            this.extensionToMimeTypeMap.put("rss", "application/rss+xml");
            this.extensionToMimeTypeMap.put("atom", "application/atom+xml");
            this.extensionToMimeTypeMap.put("yaml", "application/x-yaml");
            this.extensionToMimeTypeMap.put("png", "image/png");
            this.extensionToMimeTypeMap.put("jpg", "image/jpeg");
            this.extensionToMimeTypeMap.put("jpeg", "image/jpeg");
            this.extensionToMimeTypeMap.put("jpe", "image/jpeg");
            this.extensionToMimeTypeMap.put("gif", "image/gif");
            this.extensionToMimeTypeMap.put("bmp", "image/bmp");
            this.extensionToMimeTypeMap.put("ico", "image/ico");
            this.extensionToMimeTypeMap.put("tiff", "image/tiff");
            this.extensionToMimeTypeMap.put("tif", "image/tiff");
            this.extensionToMimeTypeMap.put("svg", "image/svg+xml");
            this.extensionToMimeTypeMap.put("svgz", "image/svg+xml");
            this.extensionToMimeTypeMap.put("swf", "application/x-shockwave-flash");
            this.extensionToMimeTypeMap.put("flv", "video/x-flv");
            this.extensionToMimeTypeMap.put("zip", "application/zip");
            this.extensionToMimeTypeMap.put("rar", "application/x-rar-compressed");
            this.extensionToMimeTypeMap.put("tar", "application/x-tar");
            this.extensionToMimeTypeMap.put("exe", "application/x-msdownload");
            this.extensionToMimeTypeMap.put("msi", "application/x-msdownload");
            this.extensionToMimeTypeMap.put("bin", "application/octet-stream");
            this.extensionToMimeTypeMap.put("cab", "application/vnd.ms-cab-compressed");
            this.extensionToMimeTypeMap.put("mp3", "audio/mpeg");
            this.extensionToMimeTypeMap.put("mp2", "audio/mpeg");
            this.extensionToMimeTypeMap.put("wav", "audio/wav");
            this.extensionToMimeTypeMap.put("midi", "audio/midi");
            this.extensionToMimeTypeMap.put("mid", "audio/midi");
            this.extensionToMimeTypeMap.put("aiff", "audio/aiff");
            this.extensionToMimeTypeMap.put("aif", "audio/aiff");
            this.extensionToMimeTypeMap.put("mkv", "video/x-matroska");
            this.extensionToMimeTypeMap.put("qt", "video/quicktime");
            this.extensionToMimeTypeMap.put("mov", "video/quicktime");
            this.extensionToMimeTypeMap.put("mpeg", "video/mpeg");
            this.extensionToMimeTypeMap.put("mpg", "video/mpeg");
            this.extensionToMimeTypeMap.put("mpe", "video/mpeg");
            this.extensionToMimeTypeMap.put("avi", "video/msvideo");
            this.extensionToMimeTypeMap.put("wmv", "video/x-ms-wmv");
            this.extensionToMimeTypeMap.put("mp4", "video/mp4");
            this.extensionToMimeTypeMap.put("ogg", "application/ogg");
            this.extensionToMimeTypeMap.put("ra", "audio/x-pn-realaudio");
            this.extensionToMimeTypeMap.put("ram", "audio/x-pn-realaudio");
            this.extensionToMimeTypeMap.put("rm", "application/vnd.rn-realmedia");
            this.extensionToMimeTypeMap.put("pdf", "application/pdf");
            this.extensionToMimeTypeMap.put("doc", "application/msword");
            this.extensionToMimeTypeMap.put("docx", "application/msword");
            this.extensionToMimeTypeMap.put("rtf", "application/rtf");
            this.extensionToMimeTypeMap.put("xls", "application/vnd.ms-excel");
            this.extensionToMimeTypeMap.put("xlt", "application/vnd.ms-excel");
            this.extensionToMimeTypeMap.put("xlm", "application/vnd.ms-excel");
            this.extensionToMimeTypeMap.put("xld", "application/vnd.ms-excel");
            this.extensionToMimeTypeMap.put("xla", "application/vnd.ms-excel");
            this.extensionToMimeTypeMap.put("xlc", "application/vnd.ms-excel");
            this.extensionToMimeTypeMap.put("xlw", "application/vnd.ms-excel");
            this.extensionToMimeTypeMap.put("xll", "application/vnd.ms-excel");
            this.extensionToMimeTypeMap.put("ppt", "application/vnd.ms-powerpoint");
            this.extensionToMimeTypeMap.put("pps", "application/vnd.ms-powerpoint");
            this.extensionToMimeTypeMap.put("odt", "application/vnd.oasis.opendocument.text");
            this.extensionToMimeTypeMap.put("ods", "application/vnd.oasis.opendocument.spreadsheet");
            this.extensionToMimeTypeMap.put("eps", "application/postscript");
            this.extensionToMimeTypeMap.put("xul", "application/vnd.mozilla.xul+xml");
            this.extensionToMimeTypeMap.put("java", "text/plain");
            this.extensionToMimeTypeMap.put("rtx", "text/richtext");
            this.extensionToMimeTypeMap.put("tsv", "text/tab-separated-values");
            this.extensionToMimeTypeMap.put("etx", "text/x-setext");
            this.extensionToMimeTypeMap.put("ps", "application/x-postscript");
            this.extensionToMimeTypeMap.put("class", "application/java");
            this.extensionToMimeTypeMap.put("csh", "application/x-csh");
            this.extensionToMimeTypeMap.put("sh", "application/x-sh");
            this.extensionToMimeTypeMap.put("tcl", "application/x-tcl");
            this.extensionToMimeTypeMap.put("tex", "application/x-tex");
            this.extensionToMimeTypeMap.put("texinfo", "application/x-texinfo");
            this.extensionToMimeTypeMap.put("texi", "application/x-texinfo");
            this.extensionToMimeTypeMap.put("t", "application/x-troff");
            this.extensionToMimeTypeMap.put("tr", "application/x-troff");
            this.extensionToMimeTypeMap.put("roff", "application/x-troff");
            this.extensionToMimeTypeMap.put("man", "application/x-troff-man");
            this.extensionToMimeTypeMap.put("me", "application/x-troff-me");
            this.extensionToMimeTypeMap.put("ms", "application/x-wais-source");
            this.extensionToMimeTypeMap.put("src", "application/x-wais-source");
            this.extensionToMimeTypeMap.put("bcpio", "application/x-bcpio");
            this.extensionToMimeTypeMap.put("cpio", "application/x-cpio");
            this.extensionToMimeTypeMap.put("gtar", "application/x-gtar");
            this.extensionToMimeTypeMap.put("shar", "application/x-shar");
            this.extensionToMimeTypeMap.put("sv4cpio", "application/x-sv4cpio");
            this.extensionToMimeTypeMap.put("sv4crc", "application/x-sv4crc");
            this.extensionToMimeTypeMap.put("ustar", "application/x-ustar");
            this.extensionToMimeTypeMap.put("dvi", "application/x-dvi");
            this.extensionToMimeTypeMap.put("hdf", "application/x-hdf");
            this.extensionToMimeTypeMap.put("latex", "application/x-latex");
            this.extensionToMimeTypeMap.put("oda", "application/oda");
            this.extensionToMimeTypeMap.put("ps", "application/postscript");
            this.extensionToMimeTypeMap.put("eps", "application/postscript");
            this.extensionToMimeTypeMap.put("ai", "application/postscript");
            this.extensionToMimeTypeMap.put("nc", "application/x-netcdf");
            this.extensionToMimeTypeMap.put("cdf", "application/x-netcdf");
            this.extensionToMimeTypeMap.put("cer", "application/x-x509-ca-cert");
            this.extensionToMimeTypeMap.put("gz", "application/x-gzip");
            this.extensionToMimeTypeMap.put("Z", "application/x-compress");
            this.extensionToMimeTypeMap.put("z", "application/x-compress");
            this.extensionToMimeTypeMap.put("hqx", "application/mac-binhex40");
            this.extensionToMimeTypeMap.put("mif", "application/x-mif");
            this.extensionToMimeTypeMap.put("ief", "image/ief");
            this.extensionToMimeTypeMap.put("ras", "image/x-cmu-raster");
            this.extensionToMimeTypeMap.put("pnm", "image/x-portable-anymap");
            this.extensionToMimeTypeMap.put("pbm", "image/x-portable-bitmap");
            this.extensionToMimeTypeMap.put("pgm", "image/x-portable-graymap");
            this.extensionToMimeTypeMap.put("ppm", "image/x-portable-pixmap");
            this.extensionToMimeTypeMap.put("rgb", "image/x-rgb");
            this.extensionToMimeTypeMap.put("xbm", "image/x-xbitmap");
            this.extensionToMimeTypeMap.put("xpm", "image/x-xpixmap");
            this.extensionToMimeTypeMap.put("xwd", "image/x-xwindowdump");
            this.extensionToMimeTypeMap.put("au", "audio/basic");
            this.extensionToMimeTypeMap.put("snd", "audio/basic");
            this.extensionToMimeTypeMap.put("aif", "audio/x-aiff");
            this.extensionToMimeTypeMap.put("aiff", "audio/x-aiff");
            this.extensionToMimeTypeMap.put("aifc", "audio/x-aiff");
            this.extensionToMimeTypeMap.put("movie", "video/x-sgi-movie");
            this.extensionToMimeTypeMap.put("avx", "video/x-rad-screenplay");
            this.extensionToMimeTypeMap.put("wrl", "x-world/x-vrml");
            this.extensionToMimeTypeMap.put("mpv2", "video/mpeg2");
            this.extensionToMimeTypeMap.put("xsl", "text/xml");
            this.extensionToMimeTypeMap.put("wbmp", "image/vnd.wap.wbmp");
            this.extensionToMimeTypeMap.put("wml", "text/vnd.wap.wml");
            this.extensionToMimeTypeMap.put("wmlc", "application/vnd.wap.wmlc");
            this.extensionToMimeTypeMap.put("wmls", "text/vnd.wap.wmlscript");
            this.extensionToMimeTypeMap.put("wmlscriptc", "application/vnd.wap.wmlscriptc");
        }

        return this.extensionToMimeTypeMap.get(extension);
    }

    @Override
    public Locale getLocaleBestMatchFromAcceptLanguageHeader(String header) {

        if (StringUtils.isBlank(header)) {
            return null;
        }

        try {
            Locale bestLocale = null;
            Double bestQ = Double.MIN_VALUE;

            // Based on http://stackoverflow.com/a/12141151/843699
            for (String str : header.split(",")) {
                String[] arr = str.trim().replace("-", "_").split(";");

                //Parse the locale
                Locale locale = null;
                String[] l = arr[0].split("_");
                switch (l.length) {
                    case 2 :
                        locale = new Locale(l[0], l[1]);
                        break;
                    case 3 :
                        locale = new Locale(l[0], l[1], l[2]);
                        break;
                    default :
                        locale = new Locale(l[0]);
                        break;
                }

                //Parse the q-value
                Double q = 1.0D;
                for (String s : arr) {
                    s = s.trim();
                    if (s.startsWith("q=")) {
                        q = Double.parseDouble(s.substring(2).trim());
                        break;
                    }
                }

                if (q > bestQ) {
                    bestQ = q;
                    bestLocale = locale;
                }
            }

            return bestLocale;
        } catch (Exception ex) {
            logger.warn("Unable to parse the \"Accept-Language\" HTTP header : " + header);
            return null;
        }
    }

    @Override
    public boolean isClasspathResourceLoadedFromJar(String resourcePath) {
        Objects.requireNonNull(resourcePath, "The resourcePath can't be NULL");

        try {
            URL resource = getClass().getResource(resourcePath);
            if (resource == null) {
                throw new RuntimeException("Resource not found on the classpath: " + resourcePath);
            }

            URI uri = resource.toURI();
            return uri.toString().toLowerCase().startsWith("jar:");

        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    @Override
    public File getAppJarDirectory() {

        if (!this.appJarDirectoryChecked) {
            synchronized (this.appJarDirectoryLock) {
                if (!this.appJarDirectoryChecked) {
                    this.appJarDirectoryChecked = true;

                    try {

                        String jarPath = SpincastUtilsDefault.class.getProtectionDomain().getCodeSource().getLocation().getPath();
                        if (jarPath == null) {
                            throw new RuntimeException("Unable to get the path of " + SpincastUtilsDefault.class.getName() + "!");
                        }

                        jarPath = URLDecoder.decode(jarPath, "UTF-8");
                        if (jarPath.toLowerCase().endsWith(".jar")) {

                            File jarFile = new File(jarPath);
                            if (!jarFile.isFile()) {
                                throw new RuntimeException("This is supposed to be a file : " + jarFile.getAbsolutePath());
                            }

                            //==========================================
                            // We also use a class which is not from the
                            // same "core" artifact to make sure everything
                            // is running from a single executable jar!
                            // "MIMEParse" is from "spincast-shaded-dependencies".
                            //==========================================
                            String jar2Path = MIMEParse.class.getProtectionDomain().getCodeSource().getLocation().getPath();

                            if (jar2Path == null) {
                                throw new RuntimeException("Unable to get the path of " + MIMEParse.class.getName() + "!");
                            }
                            jar2Path = URLDecoder.decode(jar2Path, "UTF-8");

                            if (!jarPath.equals(jar2Path)) {
                                logger.warn("Not inside a single executable jar.");
                            } else {

                                String manifestPath = "jar:file:" + jarPath + "!/META-INF/MANIFEST.MF";
                                Manifest manifest = new Manifest(new URL(manifestPath).openStream());
                                Attributes attr = manifest.getMainAttributes();
                                String mainClass = attr.getValue("Main-Class");
                                if (mainClass == null) {
                                    logger.warn("Not inside an executable jar : " + jarFile.getAbsolutePath());
                                } else {
                                    File appJarDir = jarFile.getParentFile();
                                    if (!appJarDir.isDirectory()) {
                                        throw new RuntimeException("This is supposed to be a directory : " +
                                                                   appJarDir.getAbsolutePath());
                                    }
                                    this.appJarDirectory = appJarDir;
                                }
                            }
                        }
                    } catch (Exception ex) {
                        throw SpincastStatics.runtimize(ex);
                    }
                }
            }
        }

        return this.appJarDirectory;
    }

    @Override
    public boolean isClassLoadedFromJar(Class<?> clazz) {
        Objects.requireNonNull(clazz, "The clazz parameter can't be NULL");

        File file = getClassLocationDirOrJarFile(clazz);
        try {
            String path = URLDecoder.decode(file.getAbsolutePath(), "UTF-8");
            boolean inJar = path.toLowerCase().endsWith(".jar");
            return inJar;
        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    @Override
    public File getClassLocationDirOrJarFile(Class<?> clazz) {
        String path = clazz.getProtectionDomain().getCodeSource().getLocation().getPath();
        if (path == null) {
            throw new RuntimeException("Unable to get the path of " + SpincastUtilsDefault.class.getName() + "!");
        }
        return new File(path);
    }

    @Override
    public boolean isRunningFromExecutableJar() {
        return getAppJarDirectory() != null;
    }

    @Override
    public File getAppRootDirectoryNoJar() {

        if (!this.appRootDirectoryNoJarChecked) {
            synchronized (this.appRootDirectoryNoJarLock) {
                if (!this.appRootDirectoryNoJarChecked) {
                    this.appRootDirectoryNoJarChecked = true;

                    try {
                        //==========================================
                        // Running in a .jar
                        //==========================================
                        if (getAppJarDirectory() != null) {
                            this.appRootDirectoryNoJar = null;
                        } else {
                            String path = new File(".").getAbsolutePath();
                            path = path.substring(0, path.length() - 1);
                            this.appRootDirectoryNoJar = new File(path);
                        }

                    } catch (Exception ex) {
                        throw SpincastStatics.runtimize(ex);
                    }
                }
            }
        }

        return this.appRootDirectoryNoJar;
    }

    @Override
    public String getSpincastCurrentVersion() {

        String currentVersion = getClass().getPackage().getImplementationVersion();

        if (currentVersion == null) {

            if (isRunningFromExecutableJar()) {
                logger.error("Unable to get the Spincast version! Make sure you have this plugin " +
                             "in your pom.xml (with an up to date version!):\n\n" +
                             "<plugin>\n" +
                             "    <groupId>org.apache.maven.plugins</groupId>\n" +
                             "    <artifactId>maven-jar-plugin</artifactId>\n" +
                             "    <version>3.0.2</version>\n" +
                             "    <configuration>\n" +
                             "        <archive>  \n" +
                             "            <manifest>\n" +
                             "                <addDefaultImplementationEntries>true</addDefaultImplementationEntries>\n" +
                             "                <addDefaultSpecificationEntries>true</addDefaultSpecificationEntries>\n" +
                             "            </manifest>\n" +
                             "        </archive>\n" +
                             "    </configuration>\n" +
                             "</plugin>\n\n");

                return null;
            }

            //==========================================
            // We're in an IDE...
            //==========================================
            currentVersion = getCurrentVersionFromPom();
        }

        return currentVersion;
    }

    protected String getCurrentVersionFromPom() {

        String artifactVersion = null;
        try {
            File file = new File(".");
            String filePath = file.getAbsolutePath();
            file = new File(filePath);
            File parent = file.getParentFile();
            File pomFile = new File(parent.getAbsolutePath() + "/pom.xml");
            if (pomFile.isFile()) {
                String content = FileUtils.readFileToString(pomFile, "UTF-8");
                int pos = content.indexOf("<version>");
                if (pos > 0) {
                    int pos2 = content.indexOf("</version>", pos);
                    if (pos2 > 0) {
                        artifactVersion = content.substring(pos + "<version>".length(), pos2);
                    }
                }
            }
            if (artifactVersion == null) {
                throw new RuntimeException("Version in pom.xml not found");
            }
        } catch (Exception ex) {
            throw new RuntimeException("Unable to get the pom.xml : " + SpincastStatics.getStackTrace(ex));
        }

        return artifactVersion;
    }

    @Override
    public void zipDirectory(File directoryToZip, File targetZipFile, boolean includeDirItself) {

        File targetParentDir = targetZipFile.getParentFile();
        if (!targetParentDir.isDirectory()) {
            boolean result = targetParentDir.mkdirs();
            if (!result) {
                throw new RuntimeException("Unable to create the target parent directory: " + targetParentDir.getAbsolutePath());
            }
        }

        try (FileOutputStream fos = new FileOutputStream(targetZipFile);
             ZipOutputStream zos = new ZipOutputStream(fos)) {

            final Path directoryToZipPath = directoryToZip.toPath();

            String prefix = "";
            if (includeDirItself) {
                prefix = directoryToZip.getName() + "/";
                zos.putNextEntry(new ZipEntry(prefix));
            }
            final String prefixFinal = prefix;

            Files.walkFileTree(directoryToZipPath, new SimpleFileVisitor<Path>() {

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    zos.putNextEntry(new ZipEntry(prefixFinal + directoryToZipPath.relativize(file).toString()));
                    Files.copy(file, zos);
                    zos.closeEntry();
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {

                    if (!"".equals(directoryToZipPath.relativize(dir).toString())) {
                        zos.putNextEntry(new ZipEntry(prefixFinal + directoryToZipPath.relativize(dir).toString() + "/"));
                        zos.closeEntry();
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    @Override
    public void zipExtract(File zipFile, File targetDir) {

        Objects.requireNonNull(zipFile, "The zip file can't be NULL");
        Objects.requireNonNull(targetDir, "The target directory can't be NULL");

        try {

            if (!zipFile.isFile()) {
                throw new RuntimeException("The file to extract doesn't exist: " + zipFile.getCanonicalPath());
            }

            if (!targetDir.isDirectory()) {
                boolean result = targetDir.mkdirs();
                if (!result) {
                    throw new RuntimeException("Unable to create the target directory: " + targetDir.getCanonicalPath());
                }
            }

            byte[] buffer = new byte[1024];

            ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
            try {
                ZipEntry ze = zis.getNextEntry();
                while (ze != null) {

                    String fileName = ze.getName();
                    File newFile = new File(targetDir.getAbsolutePath() + "/" + fileName);

                    File newFileParent = newFile.getParentFile();
                    if (!newFileParent.isDirectory()) {
                        boolean result = targetDir.mkdirs();
                        if (!result) {
                            throw new RuntimeException("Unable to create an unzipped directory: " +
                                                       newFileParent.getCanonicalPath());
                        }
                    }

                    if (ze.isDirectory()) {
                        boolean result = newFile.mkdirs();
                        if (!result) {
                            throw new RuntimeException("Unable to create an unzipped directory: " +
                                                       newFile.getCanonicalPath());
                        }
                    } else {
                        FileOutputStream fos = null;
                        try {

                            fos = new FileOutputStream(newFile);

                            int len;
                            while ((len = zis.read(buffer)) > 0) {
                                fos.write(buffer, 0, len);
                            }
                        } finally {
                            SpincastStatics.closeQuietly(fos);
                        }
                    }

                    ze = zis.getNextEntry();
                }

                zis.closeEntry();
            } finally {
                SpincastStatics.closeQuietly(zis);
            }
        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    @Override
    public String getCacheBusterCode() {

        if (getSpincastConfig().isDevelopmentMode() && isAlwaysChangeCacheBusterCodeInDevMode()) {
            return generateCacheBusterCode();
        }

        if (this.cacheBusterCode == null) {
            synchronized (this.cacheBusterCodeLock) {
                if (this.cacheBusterCode == null) {
                    //==========================================
                    // By default, the cache buster code change
                    // everytime the application is restarted.
                    //==========================================
                    this.cacheBusterCode = generateCacheBusterCode();
                }
            }
        }
        return this.cacheBusterCode;
    }

    protected String generateCacheBusterCode() {
        return "spincastcb_" + new Date().getTime() + "_";
    }

    protected boolean isAlwaysChangeCacheBusterCodeInDevMode() {
        return true;
    }

    @Override
    public String removeCacheBusterCodes(String text) {

        if (StringUtils.isBlank(text)) {
            return text;
        }

        String result = text.replaceAll("spincastcb_[\\d]{13}_", "");
        return result;
    }

    @Override
    public String readClasspathFile(String path) {
        return readClasspathFile(path, "UTF-8");
    }

    @Override
    public String readClasspathFile(String path, String encoding) {

        InputStream in = getClasspathInputStream(path);
        if (in == null) {
            return null;
        }

        if (encoding == null) {
            encoding = "UTF-8";
        }

        try {
            return IOUtils.toString(in, encoding);
        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        } finally {
            SpincastStatics.closeQuietly(in);
        }
    }

    @Override
    public InputStream getClasspathInputStream(String classpathPath) {
        if (classpathPath == null) {
            return null;
        }
        if (!classpathPath.startsWith("/")) {
            classpathPath = "/" + classpathPath;
        }

        InputStream in = getClass().getResourceAsStream(classpathPath);
        return in;
    }

    @Override
    public void copyClasspathFileToFileSystem(String classpathFilePath, File fileSystemFile) {
        try {

            if (classpathFilePath == null) {
                throw new RuntimeException("The classpathFilePath is null.");
            }
            if (fileSystemFile == null) {
                throw new RuntimeException("The fileSystemFile is null.");
            }

            File targetParentDir = fileSystemFile.getParentFile();
            if (!targetParentDir.isDirectory()) {
                boolean result = targetParentDir.mkdirs();
                if (!result) {
                    throw new RuntimeException("Unable to create the parent target directory " +
                                               targetParentDir.getAbsolutePath());
                }
            }

            boolean isInJar = isClasspathResourceLoadedFromJar(classpathFilePath);
            if (isInJar) {
                //==========================================
                // In a jar
                //==========================================
                copyJarFileToFileSystem(classpathFilePath, fileSystemFile);
            } else {
                //==========================================
                // Not in a jar
                //==========================================
                InputStream is = getClasspathInputStream(classpathFilePath);
                try {
                    if (is == null) {
                        throw new RuntimeException("Resource not found on the classpath: " + classpathFilePath);
                    }
                    FileUtils.copyInputStreamToFile(is, fileSystemFile);
                } finally {
                    SpincastStatics.closeQuietly(is);
                }
            }
        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    @Override
    public void copyClasspathDirToFileSystem(String classpathDirPath, File targetDir) {
        try {

            if (classpathDirPath == null) {
                throw new RuntimeException("The classpathDirPath is null.");
            }
            if (targetDir == null) {
                throw new RuntimeException("The fileSystemDir is null.");
            }

            if (!targetDir.isDirectory()) {
                boolean result = targetDir.mkdirs();
                if (!result) {
                    throw new RuntimeException("Unable to create the parent directory " + targetDir.getAbsolutePath());
                }
            } else {
                FileUtils.cleanDirectory(targetDir);
            }

            boolean isInJar = isClasspathResourceLoadedFromJar(classpathDirPath);
            if (isInJar) {
                //==========================================
                // In a jar
                //==========================================
                copyJarDirToFileSystem(classpathDirPath, targetDir);
            } else {

                URL resource = getClass().getResource(classpathDirPath);
                if (resource == null) {
                    throw new RuntimeException("Resource not found on the classpath: " + classpathDirPath);
                }

                File sourceDir = Paths.get(resource.toURI()).toFile();
                if (!sourceDir.isDirectory()) {
                    throw new RuntimeException("The classpathDirPath is not a directory: " + sourceDir.getAbsolutePath());
                } else {
                    logger.info("Classpath directory to copy: " + sourceDir.getAbsolutePath());
                }

                FileUtils.copyDirectory(sourceDir, targetDir);
            }
        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    protected void copyJarDirToFileSystem(String jarDirPath, File targetDir) {
        try {

            if (!jarDirPath.startsWith("/")) {
                jarDirPath = "/" + jarDirPath;
            }
            URL resource = getClass().getResource(jarDirPath);
            if (resource == null) {
                throw new RuntimeException("Resource not found on the classpath: " + jarDirPath);
            }

            URI jarDirUri = getClass().getResource(jarDirPath).toURI();

            //==========================================
            // Get a lock so the same FileSystem is only
            // open once at the time.
            //==========================================
            Object fileSystemLock = getFileSystemLock(jarDirUri);

            //==========================================
            // Copy the directory
            //==========================================
            synchronized (fileSystemLock) {
                try (FileSystem jarFs = FileSystems.newFileSystem(jarDirUri, new HashMap<>())) {
                    Path path = jarFs.getPath(jarDirPath);

                    if (path == null || !Files.exists(path)) {
                        throw new RuntimeException("Resource not found on the classpath: " + jarDirPath);
                    }
                    try (Stream<Path> pathsStream = Files.walk(path)) {
                        Path[] paths = pathsStream.toArray(Path[]::new);
                        for (Path elPath : paths) {
                            String relativePath = elPath.toString().substring(jarDirPath.length());
                            Path targetPath = Paths.get(targetDir.getAbsolutePath(), relativePath);
                            Files.copy(elPath, targetPath, StandardCopyOption.REPLACE_EXISTING);
                        }
                    }
                }
            }

        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    protected void copyJarFileToFileSystem(String jarDirPath, File targetFile) {
        try {

            if (!jarDirPath.startsWith("/")) {
                jarDirPath = "/" + jarDirPath;
            }

            InputStream inputStream = getClass().getResourceAsStream(jarDirPath);
            if (inputStream == null) {
                throw new RuntimeException("Resource not found on the classpath: " + jarDirPath);
            }
            FileUtils.copyInputStreamToFile(inputStream, targetFile);

        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    @Override
    public boolean isContainsSpecialCharacters(String str) {

        if (str == null) {
            return false;
        }

        for (char c : str.toCharArray()) {
            if (c < 32 || c == 127) {
                return true;
            }
        }

        return false;
    }

    @Override
    public String inQuotesStringFormat(String str, boolean singleQuotes) {

        str = str.replace("\r", "");
        str = str.replace("\n", "\\n");

        if (singleQuotes) {
            str = str.replace("'", "\\\'");
        } else {
            str = str.replace("\"", "\\\"");
        }

        return str;
    }

    @Override
    public <T extends Enum<?>> T enumValueOfInsensitive(Class<T> enumClass, String str) {
        Objects.requireNonNull(enumClass, "The enumClass can't be NULL");
        if (str == null) {
            return null;
        }

        for (T enumElement : enumClass.getEnumConstants()) {
            if (enumElement.name().equalsIgnoreCase(str)) {
                return enumElement;
            }
        }
        return null;
    }

    @Override
    public long[] convertLongSetToLongPrimitiveArray(Set<Long> longSet) {
        if (longSet == null) {
            return null;
        }

        long[] arr = new long[longSet.size()];
        int pos = 0;
        for (Long oneLong : longSet) {
            arr[pos++] = oneLong.longValue();
        }
        return arr;
    }

    @Override
    public String createTempFilePath() {
        String path = getGeneratedTempFilesDir() + "/" + UUID.randomUUID().toString();
        return path;
    }

    @Override
    public String basicHtml(boolean newlineToBrFirst, String html) {
        return basicHtml(newlineToBrFirst, html, false);
    }

    @Override
    public String basicHtml(boolean newlineToBrFirst, String html, boolean allowImages) {

        if (StringUtils.isBlank(html)) {
            return "";
        }

        if (newlineToBrFirst) {
            html = html.replaceAll("\\r?\\n", "\n<br>");
        }

        Safelist safelist = null;
        if (allowImages) {
            safelist = Safelist.basicWithImages();
        } else {
            safelist = Safelist.basic();
        }

        //==========================================
        // Passing the url base + "preserveRelativeLinks"
        // will allow relative links.
        //
        // Also allows "center" tags.
        //==========================================
        html = Jsoup.clean(html,
                           getSpincastConfig().getPublicUrlBase(),
                           safelist.preserveRelativeLinks(true).addTags("center"));

        //==========================================
        // JSoup addds some unwanted whitespaces
        //==========================================
        html = html.replace(" \n<br>", "\n<br>");

        return html;
    }

    @Override
    public <T> T getRandomElement(Set<T> set) {
        if (set != null && set.size() > 0) {
            int pos = new Random().nextInt(set.size());
            int i = 0;
            for (T obj : set) {
                if (i++ == pos) {
                    return obj;
                }
            }
        }

        return null;
    }

    @Override
    public String getQuerystringFromUrl(String url) {
        try {
            if (url == null) {
                return "";
            }

            URL urlObj = new URL(url);
            String qs = urlObj.getQuery();
            if (qs == null) {
                qs = "";
            }
            return qs;
        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    @Override
    public Map<String, List<String>> getQuerystringParametersFromUrl(String url) {
        String qs = getQuerystringFromUrl(url);
        return getParametersFromQuerystring(qs, true);
    }

    @Override
    public Map<String, List<String>> getParametersFromQuerystring(String qs, boolean decodeQueryStringFirst) {

        try {
            Map<String, List<String>> qsParams = new HashMap<String, List<String>>();
            if (qs != null) {
                qs = qs.trim();
                if (qs.startsWith("?")) {
                    qs = qs.substring(1);
                }
            }
            if (StringUtils.isBlank(qs)) {
                return qsParams;
            }

            if (decodeQueryStringFirst) {
                qs = URLDecoder.decode(qs, "UTF-8");
            }

            List<NameValuePair> params = URLEncodedUtils.parse(qs, Charset.forName("UTF-8"));
            if (params != null) {
                for (NameValuePair nameValuePair : params) {
                    String name = nameValuePair.getName();
                    List<String> values = qsParams.get(name);
                    if (values == null) {
                        values = new ArrayList<String>();
                        qsParams.put(name, values);
                    }
                    values.add(nameValuePair.getValue());
                }
            }

            return qsParams;
        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }

    }

    @Override
    public boolean isRequestedResourceNameEndsWithBeforeExtension(URI currentURI, String suffix) {

        if (currentURI == null) {
            return false;
        }

        if (StringUtils.isBlank(suffix)) {
            return true;
        }

        String baseName = FilenameUtils.getBaseName(currentURI.toString());
        return isRequestedResourceNameEndsWithBeforeExtension(baseName, suffix);
    }

    @Override
    public boolean isRequestedResourceNameEndsWithBeforeExtension(String resourceBaseName, String suffix) {
        if (StringUtils.isBlank(resourceBaseName)) {
            return false;
        }

        if (StringUtils.isBlank(suffix)) {
            return true;
        }

        boolean endsWith =
                getSpincastConfig().isRoutesCaseSensitive() ? StringUtils.endsWith(resourceBaseName, suffix)
                                                            : StringUtils.endsWithIgnoreCase(resourceBaseName,
                                                                                             suffix);
        return endsWith;
    }

    @Override
    public String convertToFriendlyToken(String str) {
        return convertToFriendlyToken(str, null);
    }

    @Override
    public String convertToFriendlyToken(String str, String resultIfEmpty) {
        if (!StringUtils.isBlank(str)) {
            str = StringUtils.stripAccents(str);
            str = str.replaceAll("( |_)", "-");
            str = str.replaceAll("(?i)[^a-z0-9\\-]", "");
            str = str.replaceAll("(-)\\1+", "$1");
            str = StringUtils.strip(str, "-");
            str = str.toLowerCase();
        }
        if (StringUtils.isBlank(str)) {
            if (resultIfEmpty != null) {
                return resultIfEmpty;
            }
            return UUID.randomUUID().toString();
        }
        return str;
    }

    @Override
    public boolean isPortOpen(String host, int port) {

        Socket socket = null;
        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress(host, port));
            return true;
        } catch (IOException e) {
            return false;
        } finally {
            try {
                socket.close();
            } catch (Exception ex) {
                //...
            }
        }
    }

    @Override
    public void clearDirectory(File dir) {

        if (dir == null || !dir.isDirectory()) {
            return;
        }

        try {
            FileUtils.cleanDirectory(dir);
        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    @Override
    public <K, V> LinkedHashMap<K, V> sortMapByValues(Map<K, V> map) {

        if (map == null) {
            return null;
        }

        List<Entry<K, V>> list = new LinkedList<>(map.entrySet());
        Collections.sort(list, new Comparator<Object>() {

            @Override
            @SuppressWarnings("unchecked")
            public int compare(Object o1, Object o2) {
                return ((Comparable<V>)((Map.Entry<K, V>)(o1)).getValue()).compareTo(((Map.Entry<K, V>)(o2)).getValue());
            }
        });

        LinkedHashMap<K, V> result = new LinkedHashMap<>();
        for (Iterator<Entry<K, V>> it = list.iterator(); it.hasNext();) {
            Map.Entry<K, V> entry = (Map.Entry<K, V>)it.next();
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

}
