package org.spincast.core.utils;

import java.io.File;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.config.ISpincastConfig;
import org.spincast.shaded.org.apache.commons.io.FileUtils;
import org.spincast.shaded.org.apache.commons.lang3.StringUtils;

import com.google.inject.Inject;

/**
 * Implementations of the Spincast utilities.
 */
public class SpincastUtils implements ISpincastUtils {

    protected final Logger logger = LoggerFactory.getLogger(SpincastUtils.class);

    private final ISpincastConfig spincastConfig;

    private Map<String, String> extensionToMimeTypeMap;
    private File appJarDir = null;
    private boolean appJarDirComputed = false;

    @Inject
    public SpincastUtils(ISpincastConfig spincastConfig) {
        this.spincastConfig = spincastConfig;
    }

    protected ISpincastConfig getSpincastConfig() {
        return this.spincastConfig;
    }

    @Override
    public boolean isContentTypeToSkipGziping(String contentType) {

        if(StringUtils.isBlank(contentType)) {
            return false;
        }
        contentType = contentType.toLowerCase();

        List<String> contentTypesToSkip = getSpincastConfig().getContentTypesToSkipGziping();
        if(contentTypesToSkip != null && contentTypesToSkip.size() > 0) {
            for(String contentTypeToSkip : contentTypesToSkip) {
                if(contentTypeToSkip.endsWith("*")) {
                    contentTypeToSkip = contentTypeToSkip.substring(0, contentTypeToSkip.length() - 1);
                    if(contentType.startsWith(contentTypeToSkip)) {
                        return true;
                    }
                } else if(contentType.equals(contentTypeToSkip) ||
                          contentType.startsWith(contentTypeToSkip + " ") ||
                          contentType.startsWith(contentTypeToSkip + ";")) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String getMimeTypeFromPath(String path) {

        if(StringUtils.isBlank(path)) {
            return null;
        }
        int index = path.lastIndexOf('.');
        if(index != -1 && index != path.length() - 1) {

            String extension = path.substring(index + 1);

            String mimeType = getMimeTypeFromExtension(extension);
            return mimeType;
        }
        return null;
    }

    @Override
    public String getMimeTypeFromExtension(String extension) {

        if(StringUtils.isBlank(extension)) {
            return null;
        }
        extension = extension.trim();
        if(extension.startsWith(".")) {
            extension = extension.substring(1);
        }
        extension = extension.toLowerCase();

        if(this.extensionToMimeTypeMap == null) {

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

        if(StringUtils.isBlank(header)) {
            return null;
        }

        try {
            Locale bestLocale = null;
            Double bestQ = Double.MIN_VALUE;

            // Based on http://stackoverflow.com/a/12141151/843699
            for(String str : header.split(",")) {
                String[] arr = str.trim().replace("-", "_").split(";");

                //Parse the locale
                Locale locale = null;
                String[] l = arr[0].split("_");
                switch(l.length) {
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
                for(String s : arr) {
                    s = s.trim();
                    if(s.startsWith("q=")) {
                        q = Double.parseDouble(s.substring(2).trim());
                        break;
                    }
                }

                if(q > bestQ) {
                    bestQ = q;
                    bestLocale = locale;
                }
            }

            return bestLocale;
        } catch(Exception ex) {
            this.logger.warn("Unable to parse the \"Accept-Language\" HTTP header : " + header);
            return null;
        }
    }

    @Override
    public File getAppJarDirectory() {

        if(!this.appJarDirComputed) {
            this.appJarDirComputed = true;

            try {

                String jarPath = SpincastUtils.class.getProtectionDomain().getCodeSource().getLocation().getPath();
                if(jarPath == null) {
                    throw new RuntimeException("Unable to get the .jar path!");
                }

                jarPath = URLDecoder.decode(jarPath, "UTF-8");
                if(jarPath.toLowerCase().endsWith(".jar")) {
                    File jarFile = new File(jarPath);
                    if(!jarFile.isFile()) {
                        throw new RuntimeException("This is supposed to be a file : " + jarFile.getAbsolutePath());
                    }
                    this.appJarDir = jarFile.getParentFile();
                    if(!this.appJarDir.isDirectory()) {
                        throw new RuntimeException("This is supposed to be a directory : " +
                                                   this.appJarDir.getAbsolutePath());
                    }
                }
            } catch(Exception ex) {
                throw SpincastStatics.runtimize(ex);
            }
        }
        return this.appJarDir;
    }

    @Override
    public String getSpincastCurrentVersion() {

        String currentVersion = getClass().getPackage().getImplementationVersion();

        //==========================================
        // We're in an IDE...
        //==========================================
        if(currentVersion == null) {
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
            if(pomFile.isFile()) {
                String content = FileUtils.readFileToString(pomFile);
                int pos = content.indexOf("<version>");
                if(pos > 0) {
                    int pos2 = content.indexOf("</version>", pos);
                    if(pos2 > 0) {
                        artifactVersion = content.substring(pos + "<version>".length(), pos2);
                    }
                }
            }
            if(artifactVersion == null) {
                throw new RuntimeException("Version in pom.xml not found");
            }
        } catch(Exception ex) {
            throw new RuntimeException("Unable to get the pom.xml : " + SpincastStatics.getStackTrace(ex));
        }

        return artifactVersion;
    }

}