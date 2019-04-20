package org.spincast.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.List;
import java.util.UUID;

import org.junit.Test;
import org.spincast.core.guice.SpincastPlugin;
import org.spincast.core.templating.TemplatingEngine;
import org.spincast.core.utils.ResourceInfo;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.core.utils.SpincastUtils;
import org.spincast.plugins.httpclient.HttpClient;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.plugins.httpclient.SpincastHttpClientPlugin;
import org.spincast.plugins.processutils.JarExecutionHandlerDefault;
import org.spincast.plugins.processutils.MavenProjectGoal;
import org.spincast.plugins.processutils.SpincastProcessUtils;
import org.spincast.plugins.processutils.SpincastProcessUtilsPlugin;
import org.spincast.shaded.org.apache.commons.io.FileUtils;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.testing.core.utils.SpincastTestingUtils;
import org.spincast.testing.defaults.NoAppTestingBase;

import com.google.common.collect.Lists;
import com.google.inject.Inject;

public class SpincastUtilsTest extends NoAppTestingBase {

    @Override
    protected List<SpincastPlugin> getExtraPlugins() {
        List<SpincastPlugin> extraPlugins = super.getExtraPlugins();
        extraPlugins.add(new SpincastHttpClientPlugin());
        extraPlugins.add(new SpincastProcessUtilsPlugin());
        return extraPlugins;
    }

    @Override
    public void beforeClass() {
        super.beforeClass();
        packageTestmavenProject();
        assertTrue(getTestMavenJarFile().isFile());
    }

    private File testMavenProjectDir;

    protected String getSpincastVersionToUse() {
        return "1.4.0";
    }

    protected void packageTestmavenProject() {
        this.testMavenProjectDir =
                getSpincastProcessUtils().executeGoalOnExternalMavenProject(new ResourceInfo("/testMavenProject",
                                                                                             true),
                                                                            MavenProjectGoal.PACKAGE,
                                                                            SpincastStatics.map("spincastVersion",
                                                                                                getSpincastVersionToUse()));
    }

    protected File getTestMavenProjectDir() {
        return this.testMavenProjectDir;
    }

    protected File getTestMavenJarFile() {
        File jarFile = new File(getTestMavenProjectDir(),
                                "target/spincast-test-" + getSpincastVersionToUse() + ".jar");
        return jarFile;
    }

    @Inject
    private SpincastUtils spincastUtils;

    @Inject
    private TemplatingEngine templatingEngine;

    @Inject
    private HttpClient httpClient;

    @Inject
    private SpincastProcessUtils spincastProcessUtils;

    protected SpincastUtils getSpincastUtils() {
        return this.spincastUtils;
    }

    protected TemplatingEngine getTemplatingEngine() {
        return this.templatingEngine;
    }

    protected HttpClient getHttpClient() {
        return this.httpClient;
    }

    protected SpincastProcessUtils getSpincastProcessUtils() {
        return this.spincastProcessUtils;
    }

    @Test
    public void getMimeTypeFromExtension() throws Exception {

        String mimeType = getSpincastUtils().getMimeTypeFromExtension(".html");
        assertNotNull(mimeType);
        assertEquals("text/html", mimeType);
    }

    @Test
    public void getMimeTypeFromExtensionNoDot() throws Exception {
        String mimeType = getSpincastUtils().getMimeTypeFromExtension("html");
        assertNotNull(mimeType);
        assertEquals("text/html", mimeType);
    }

    @Test
    public void getMimeTypeFromExtensionCaseInsensitive() throws Exception {
        String mimeType = getSpincastUtils().getMimeTypeFromExtension("HtMl");
        assertNotNull(mimeType);
        assertEquals("text/html", mimeType);
    }

    @Test
    public void getMimeTypeFromExtensionNull() throws Exception {
        String mimeType = getSpincastUtils().getMimeTypeFromExtension(null);
        assertNull(mimeType);
    }

    @Test
    public void getMimeTypeFromExtensionEmpty() throws Exception {
        String mimeType = getSpincastUtils().getMimeTypeFromExtension("");
        assertNull(mimeType);
    }

    @Test
    public void getMimeTypeFromExtensionEmpty2() throws Exception {
        String mimeType = getSpincastUtils().getMimeTypeFromExtension("   ");
        assertNull(mimeType);
    }

    @Test
    public void getMimeTypeFromPath() throws Exception {
        String mimeType = getSpincastUtils().getMimeTypeFromPath("/toto/titi/tata.html");
        assertNotNull(mimeType);
        assertEquals("text/html", mimeType);
    }

    @Test
    public void getMimeTypeFromPathNoExtension() throws Exception {
        String mimeType = getSpincastUtils().getMimeTypeFromPath("/toto/titi/tata");
        assertNull(mimeType);
    }

    @Test
    public void getMimeTypeFromPathNull() throws Exception {
        String mimeType = getSpincastUtils().getMimeTypeFromPath(null);
        assertNull(mimeType);
    }

    @Test
    public void getMimeTypeFromPathEmpty() throws Exception {
        String mimeType = getSpincastUtils().getMimeTypeFromPath("");
        assertNull(mimeType);
    }

    @Test
    public void getMimeTypeFromPathEmpty2() throws Exception {
        String mimeType = getSpincastUtils().getMimeTypeFromPath("   ");
        assertNull(mimeType);
    }

    @Test
    public void zipUnzipDirectory() throws Exception {

        File testDir = new File(createTestingFilePath());
        assertFalse(testDir.isDirectory());

        boolean result = testDir.mkdirs();
        assertTrue(result);
        assertTrue(testDir.isDirectory());

        File testFile = new File(testDir.getAbsolutePath() + "/test.txt");
        FileUtils.writeStringToFile(testFile, SpincastTestingUtils.TEST_STRING, "UTF-8");

        testFile = new File(testDir.getAbsolutePath() + "/test2.txt");
        FileUtils.writeStringToFile(testFile, SpincastTestingUtils.TEST_STRING + "2", "UTF-8");

        File testSubDir = new File(testDir.getAbsolutePath() + "/someDir");
        result = testSubDir.mkdirs();
        assertTrue(result);
        assertTrue(testSubDir.isDirectory());

        testFile = new File(testSubDir.getAbsolutePath() + "/test3.txt");
        FileUtils.writeStringToFile(testFile, SpincastTestingUtils.TEST_STRING + "3", "UTF-8");

        testFile = new File(testSubDir.getAbsolutePath() + "/test4.txt");
        FileUtils.writeStringToFile(testFile, SpincastTestingUtils.TEST_STRING + "4", "UTF-8");

        File targetDir = new File(createTestingFilePath());
        assertFalse(targetDir.isDirectory());

        result = targetDir.mkdirs();
        assertTrue(result);
        assertTrue(targetDir.isDirectory());

        File targetZipFile = new File(targetDir.getAbsolutePath() + "/myZip.zip");
        assertFalse(targetZipFile.isFile());

        getSpincastUtils().zipDirectory(testDir, targetZipFile, false);
        assertTrue(targetZipFile.isFile());

        File targetDir2 = new File(createTestingFilePath());
        assertFalse(targetDir2.isDirectory());
        result = targetDir2.mkdirs();
        assertTrue(result);
        assertTrue(targetDir2.isDirectory());

        getSpincastUtils().zipExtract(targetZipFile, targetDir2);

        File unzippedFile = new File(targetDir2 + "/test.txt");
        assertTrue(unzippedFile.isFile());
        String content = FileUtils.readFileToString(unzippedFile, "UTF-8");
        assertEquals(SpincastTestingUtils.TEST_STRING, content);

        unzippedFile = new File(targetDir2.getAbsolutePath() + "/test2.txt");
        assertTrue(unzippedFile.isFile());
        content = FileUtils.readFileToString(unzippedFile, "UTF-8");
        assertEquals(SpincastTestingUtils.TEST_STRING + "2", content);

        File unzippedSubDir = new File(targetDir2.getAbsolutePath() + "/someDir");
        assertTrue(unzippedSubDir.isDirectory());

        unzippedFile = new File(unzippedSubDir.getAbsolutePath() + "/test3.txt");
        assertTrue(unzippedFile.isFile());
        content = FileUtils.readFileToString(unzippedFile, "UTF-8");
        assertEquals(SpincastTestingUtils.TEST_STRING + "3", content);

        unzippedFile = new File(unzippedSubDir.getAbsolutePath() + "/test4.txt");
        assertTrue(unzippedFile.isFile());
        content = FileUtils.readFileToString(unzippedFile, "UTF-8");
        assertEquals(SpincastTestingUtils.TEST_STRING + "4", content);
    }

    @Test
    public void zipUnzipDirectoryIncludeDir() throws Exception {

        File testDir = new File(createTestingFilePath());
        assertFalse(testDir.isDirectory());

        boolean result = testDir.mkdirs();
        assertTrue(result);
        assertTrue(testDir.isDirectory());

        File testFile = new File(testDir.getAbsolutePath() + "/test.txt");
        FileUtils.writeStringToFile(testFile, SpincastTestingUtils.TEST_STRING, "UTF-8");

        testFile = new File(testDir.getAbsolutePath() + "/test2.txt");
        FileUtils.writeStringToFile(testFile, SpincastTestingUtils.TEST_STRING + "2", "UTF-8");

        File testSubDir = new File(testDir.getAbsolutePath() + "/someDir");
        result = testSubDir.mkdirs();
        assertTrue(result);
        assertTrue(testSubDir.isDirectory());

        testFile = new File(testSubDir.getAbsolutePath() + "/test3.txt");
        FileUtils.writeStringToFile(testFile, SpincastTestingUtils.TEST_STRING + "3", "UTF-8");

        testFile = new File(testSubDir.getAbsolutePath() + "/test4.txt");
        FileUtils.writeStringToFile(testFile, SpincastTestingUtils.TEST_STRING + "4", "UTF-8");

        File targetDir = new File(createTestingFilePath());
        assertFalse(targetDir.isDirectory());

        result = targetDir.mkdirs();
        assertTrue(result);
        assertTrue(targetDir.isDirectory());

        File targetZipFile = new File(targetDir.getAbsolutePath() + "/myZip.zip");
        assertFalse(targetZipFile.isFile());

        // Include the directory itself!
        getSpincastUtils().zipDirectory(testDir, targetZipFile, true);
        assertTrue(targetZipFile.isFile());

        File targetDir2 = new File(createTestingFilePath());
        assertFalse(targetDir2.isDirectory());
        result = targetDir2.mkdirs();
        assertTrue(result);
        assertTrue(targetDir2.isDirectory());

        getSpincastUtils().zipExtract(targetZipFile, targetDir2);

        File unzippedDir = new File(targetDir2 + "/" + testDir.getName());
        assertTrue(unzippedDir.isDirectory());

        File unzippedFile = new File(unzippedDir.getAbsolutePath() + "/test.txt");
        assertTrue(unzippedFile.isFile());
        String content = FileUtils.readFileToString(unzippedFile, "UTF-8");
        assertEquals(SpincastTestingUtils.TEST_STRING, content);

        unzippedFile = new File(unzippedDir.getAbsolutePath() + "/test2.txt");
        assertTrue(unzippedFile.isFile());
        content = FileUtils.readFileToString(unzippedFile, "UTF-8");
        assertEquals(SpincastTestingUtils.TEST_STRING + "2", content);

        File unzippedSubDir = new File(unzippedDir.getAbsolutePath() + "/someDir");
        assertTrue(unzippedSubDir.isDirectory());

        unzippedFile = new File(unzippedSubDir.getAbsolutePath() + "/test3.txt");
        assertTrue(unzippedFile.isFile());
        content = FileUtils.readFileToString(unzippedFile, "UTF-8");
        assertEquals(SpincastTestingUtils.TEST_STRING + "3", content);

        unzippedFile = new File(unzippedSubDir.getAbsolutePath() + "/test4.txt");
        assertTrue(unzippedFile.isFile());
        content = FileUtils.readFileToString(unzippedFile, "UTF-8");
        assertEquals(SpincastTestingUtils.TEST_STRING + "4", content);

    }

    @Test
    public void removeCacheBusterCodeNoCode() throws Exception {

        String result = getSpincastUtils().removeCacheBusterCodes("test");
        assertNotNull(result);
        assertEquals("test", result);
    }

    @Test
    public void removeCacheBusterCodeOneOccurence() throws Exception {

        String code = getSpincastUtils().getCacheBusterCode();

        String result = getSpincastUtils().removeCacheBusterCodes("111" + code + "222");
        assertNotNull(result);
        assertEquals("111222", result);
    }

    @Test
    public void removeCacheBusterCodeMultipleOccurences() throws Exception {

        String code = getSpincastUtils().getCacheBusterCode();

        String result = getSpincastUtils().removeCacheBusterCodes(code + "111" + code + "222" + code);
        assertNotNull(result);
        assertEquals("111222", result);
    }

    @Test
    public void removeCacheBusterCodeInvalid() throws Exception {

        String result = getSpincastUtils().removeCacheBusterCodes("111spincastcb_a_222");
        assertNotNull(result);
        assertEquals("111spincastcb_a_222", result);
    }

    @Test
    public void removeCacheBusterCodeInvalid2() throws Exception {

        String result = getSpincastUtils().removeCacheBusterCodes("111spincastcb_012345678901_222");
        assertNotNull(result);
        assertEquals("111spincastcb_012345678901_222", result);
    }

    @Test
    public void removeCacheBusterCodeInvalid3() throws Exception {

        String result = getSpincastUtils().removeCacheBusterCodes("111spincastcb_01234567890123_222");
        assertNotNull(result);
        assertEquals("111spincastcb_01234567890123_222", result);
    }

    @Test
    public void removeCacheBusterCodeValidEvenIfNotCurrentCode() throws Exception {

        String result = getSpincastUtils().removeCacheBusterCodes("111spincastcb_0123456789012_222");
        assertNotNull(result);
        assertEquals("111222", result);
    }

    @Test
    public void readClasspathFile() throws Exception {

        String content = getSpincastUtils().readClasspathFile("/someFile.txt");
        assertNotNull(content);
        assertEquals("Le bœuf et l'éléphant!", content);
    }

    @Test
    public void readClasspathFileNoSlash() throws Exception {

        String content = getSpincastUtils().readClasspathFile("someFile.txt");
        assertNotNull(content);
        assertEquals("Le bœuf et l'éléphant!", content);
    }

    @Test
    public void copyClasspathDirToFileSystem() throws Exception {

        File targetDir = createTestingDir();

        getSpincastUtils().copyClasspathDirToFileSystem("/oneDir", targetDir);

        assertTrue(new File(targetDir, "file2.txt").exists());
        assertTrue(new File(targetDir, "dir2/dir3/image4.jpg").exists());
    }

    @Test
    public void copyClasspathDirToFileSystemDoesntExist() throws Exception {

        File targetDir = createTestingDir();

        try {
            getSpincastUtils().copyClasspathDirToFileSystem("/nope", targetDir);
            fail();
        } catch (Exception ex) {
        }
    }

    @Test
    public void copyClasspathFileToFileSystem() throws Exception {

        File file = new File(createTestingFilePath());
        assertFalse(file.exists());

        getSpincastUtils().copyClasspathFileToFileSystem("/oneDir/file2.txt", file);

        assertTrue(file.exists());
    }

    @Test
    public void copyClasspathFileToFileSystemInsideJar() throws Exception {

        int port = SpincastTestingUtils.findFreePort();

        //==========================================
        // Execute the project's jar which is going
        // to copy a classpath dir to the specified
        // "targetDir" passed as an argument.
        //==========================================
        File targetFile = new File(createTestingFilePath());
        assertFalse(targetFile.isFile());

        JarExecutionHandlerDefault handler = new JarExecutionHandlerDefault();
        getSpincastProcessUtils().executeJar(getTestMavenJarFile().getAbsolutePath(),
                                             Lists.newArrayList(String.valueOf(port),
                                                                "/quick-start/public/js/main.js",
                                                                targetFile.getAbsolutePath()),
                                             handler);
        try {
            handler.waitForPortOpen("localhost", port, 10, 1000);

            String urlBase = "http://localhost:" + port;
            HttpResponse response = getHttpClient().GET(urlBase + "/file").send();
            assertEquals(HttpStatus.SC_OK, response.getStatus());
            assertTrue(targetFile.isFile());
        } finally {
            handler.killJarProcess();
        }
    }

    @Test
    public void copyClasspathDirToFileSystemInsideJar() throws Exception {

        int port = SpincastTestingUtils.findFreePort();

        //==========================================
        // Execute the project's jar which is going
        // to copy a classpath dir to the specified
        // "targetDir" passed as an argument.
        //==========================================
        File targetDir = createTestingDir();
        assertFalse(new File(targetDir, "bg.jpg").isFile());
        assertFalse(new File(targetDir, "logo.png").isFile());

        JarExecutionHandlerDefault handler = new JarExecutionHandlerDefault();
        getSpincastProcessUtils().executeJar(getTestMavenJarFile().getAbsolutePath(),
                                             Lists.newArrayList(String.valueOf(port),
                                                                "/quick-start/public/images",
                                                                targetDir.getAbsolutePath()),
                                             handler);
        try {
            handler.waitForPortOpen("localhost", port, 10, 1000);

            String urlBase = "http://localhost:" + port;
            HttpResponse response = getHttpClient().GET(urlBase + "/").send();
            assertEquals(HttpStatus.SC_OK, response.getStatus());

            assertTrue(new File(targetDir, "bg.jpg").isFile());
            assertTrue(new File(targetDir, "logo.png").isFile());
        } finally {
            handler.killJarProcess();
        }
    }

    @Test
    public void isClasspathResourceInJar() throws Exception {

        int port = SpincastTestingUtils.findFreePort();

        JarExecutionHandlerDefault handler = new JarExecutionHandlerDefault();
        getSpincastProcessUtils().executeJar(getTestMavenJarFile().getAbsolutePath(),
                                             Lists.newArrayList(String.valueOf(port)),
                                             handler);
        try {
            handler.waitForPortOpen("localhost", port, 10, 1000);

            String urlBase = "http://localhost:" + port;
            HttpResponse response = getHttpClient().GET(urlBase + "/resource").send();
            assertEquals(HttpStatus.SC_OK, response.getStatus());

            String body = response.getContentAsString();
            assertEquals("true", body);

        } finally {
            handler.killJarProcess();
        }
    }

    @Test
    public void convertToUrlToken() throws Exception {
        String urlToken = getSpincastUtils().convertToUrlToken("__‛'ïœ𣎴𠀋ᚡŠ šÈÆæÐ_-_-\n\tx__pð𝅘𝅥𝅯’\" àÉ  5---4-");
        assertEquals("is-se-x-p-ae-5-4", urlToken);
    }

    @Test
    public void convertToUrlTokenEmpty() throws Exception {
        String uuid = UUID.randomUUID().toString();
        String urlToken = getSpincastUtils().convertToUrlToken("");
        assertTrue(uuid.length() == urlToken.length());
    }

    @Test
    public void convertToUrlTokenEmpty2() throws Exception {
        String uuid = UUID.randomUUID().toString();
        String urlToken = getSpincastUtils().convertToUrlToken("  ");
        assertTrue(uuid.length() == urlToken.length());
    }

    @Test
    public void convertToUrlTokenEmpty3() throws Exception {
        String uuid = UUID.randomUUID().toString();
        String urlToken = getSpincastUtils().convertToUrlToken("Æ");
        assertTrue(uuid.length() == urlToken.length());
    }

    @Test
    public void convertToUrlTokenEmpty4() throws Exception {
        String uuid = UUID.randomUUID().toString();
        String urlToken = getSpincastUtils().convertToUrlToken("Æ _-");
        assertTrue(uuid.length() == urlToken.length());
    }

    @Test
    public void convertToUrlTokenNull() throws Exception {
        String uuid = UUID.randomUUID().toString();
        String urlToken = getSpincastUtils().convertToUrlToken(null);
        assertTrue(uuid.length() == urlToken.length());
    }

    @Test
    public void convertToUrlTokenEmptyWithDefault() throws Exception {
        String uuid = UUID.randomUUID().toString();
        String urlToken = getSpincastUtils().convertToUrlToken("", uuid);
        assertEquals(uuid, urlToken);
    }

    @Test
    public void convertToUrlTokenEmptyWithDefault2() throws Exception {
        String uuid = UUID.randomUUID().toString();
        String urlToken = getSpincastUtils().convertToUrlToken("Æ _-", uuid);
        assertEquals(uuid, urlToken);
    }

    @Test
    public void convertToUrlTokenNullWithDefault() throws Exception {
        String uuid = UUID.randomUUID().toString();
        String urlToken = getSpincastUtils().convertToUrlToken(null, uuid);
        assertEquals(uuid, urlToken);
    }
}
