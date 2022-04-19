package org.spincast.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.junit.Test;
import org.spincast.core.guice.SpincastPlugin;
import org.spincast.core.templating.TemplatingEngine;
import org.spincast.core.utils.SpincastUtils;
import org.spincast.plugins.httpclient.HttpClient;
import org.spincast.plugins.httpclient.SpincastHttpClientPlugin;
import org.spincast.shaded.org.apache.commons.io.FileUtils;
import org.spincast.testing.core.utils.SpincastTestingUtils;
import org.spincast.testing.defaults.NoAppTestingBase;

import com.google.inject.Inject;

public class SpincastUtilsTest extends NoAppTestingBase {

    @Override
    protected List<SpincastPlugin> getExtraPlugins() {
        List<SpincastPlugin> extraPlugins = super.getExtraPlugins();
        extraPlugins.add(new SpincastHttpClientPlugin());
        return extraPlugins;
    }

    @Inject
    private SpincastUtils spincastUtils;

    @Inject
    private TemplatingEngine templatingEngine;

    @Inject
    private HttpClient httpClient;

    protected SpincastUtils getSpincastUtils() {
        return this.spincastUtils;
    }

    protected TemplatingEngine getTemplatingEngine() {
        return this.templatingEngine;
    }

    protected HttpClient getHttpClient() {
        return this.httpClient;
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
    public void convertToFriendlyToken() throws Exception {
        String urlToken = getSpincastUtils().convertToFriendlyToken("__‛'ïœ𣎴𠀋ᚡŠ šÈÆæÐ_-_-\n\tx__pð𝅘𝅥𝅯’\" àÉ  5---4-");
        assertEquals("is-se-x-p-ae-5-4", urlToken);
    }

    @Test
    public void convertToFriendlyTokenEmpty() throws Exception {
        String uuid = UUID.randomUUID().toString();
        String urlToken = getSpincastUtils().convertToFriendlyToken("");
        assertTrue(uuid.length() == urlToken.length());
    }

    @Test
    public void convertToFriendlyTokenEmpty2() throws Exception {
        String uuid = UUID.randomUUID().toString();
        String urlToken = getSpincastUtils().convertToFriendlyToken("  ");
        assertTrue(uuid.length() == urlToken.length());
    }

    @Test
    public void convertToFriendlyTokenEmpty3() throws Exception {
        String uuid = UUID.randomUUID().toString();
        String urlToken = getSpincastUtils().convertToFriendlyToken("Æ");
        assertTrue(uuid.length() == urlToken.length());
    }

    @Test
    public void convertToFriendlyTokenEmpty4() throws Exception {
        String uuid = UUID.randomUUID().toString();
        String urlToken = getSpincastUtils().convertToFriendlyToken("Æ _-");
        assertTrue(uuid.length() == urlToken.length());
    }

    @Test
    public void convertToFriendlyTokenNull() throws Exception {
        String uuid = UUID.randomUUID().toString();
        String urlToken = getSpincastUtils().convertToFriendlyToken(null);
        assertTrue(uuid.length() == urlToken.length());
    }

    @Test
    public void convertToFriendlyTokenEmptyWithDefault() throws Exception {
        String uuid = UUID.randomUUID().toString();
        String urlToken = getSpincastUtils().convertToFriendlyToken("", uuid);
        assertEquals(uuid, urlToken);
    }

    @Test
    public void convertToFriendlyTokenEmptyWithDefault2() throws Exception {
        String uuid = UUID.randomUUID().toString();
        String urlToken = getSpincastUtils().convertToFriendlyToken("Æ _-", uuid);
        assertEquals(uuid, urlToken);
    }

    @Test
    public void convertToFriendlyTokenNullWithDefault() throws Exception {
        String uuid = UUID.randomUUID().toString();
        String urlToken = getSpincastUtils().convertToFriendlyToken(null, uuid);
        assertEquals(uuid, urlToken);
    }

    @Test
    public void getClassLocationDirOrJar() throws Exception {

        File file = getSpincastUtils().getClassLocationDirOrJarFile(getClass());
        if (getSpincastUtils().isClassLoadedFromJar(getClass())) {
            assertTrue(file.isFile());
            assertTrue(file.getAbsolutePath().toLowerCase().endsWith(".jar"));
        } else {
            assertTrue(file.isDirectory());
        }
    }

    @Test
    public void sortMapByValues() throws Exception {

        Map<String, String> map = new HashMap<String, String>();
        map.put("key1", "val2");
        map.put("key2", "val3");
        map.put("key3", "val1");
        map.put("key4", "val4");
        map.put("key5", "val0");

        LinkedHashMap<String, String> sortedMap = getSpincastUtils().sortMapByValues(map);
        int pos = 0;
        for (Entry<String, String> entry : sortedMap.entrySet()) {
            if (pos == 0) {
                assertEquals("val0", entry.getValue());
            } else if (pos == 1) {
                assertEquals("val1", entry.getValue());
            } else if (pos == 2) {
                assertEquals("val2", entry.getValue());
            } else if (pos == 3) {
                assertEquals("val3", entry.getValue());
            } else if (pos == 4) {
                assertEquals("val4", entry.getValue());
            }
            pos++;
        }

    }

}
