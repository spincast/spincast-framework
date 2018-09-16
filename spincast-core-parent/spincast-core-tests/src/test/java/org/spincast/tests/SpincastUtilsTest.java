package org.spincast.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;
import org.spincast.core.utils.SpincastUtils;
import org.spincast.defaults.testing.NoAppTestingBase;
import org.spincast.shaded.org.apache.commons.io.FileUtils;
import org.spincast.testing.core.utils.SpincastTestingUtils;

import com.google.inject.Inject;

public class SpincastUtilsTest extends NoAppTestingBase {

    @Inject
    private SpincastUtils spincastUtils;

    protected SpincastUtils getSpincastUtils() {
        return this.spincastUtils;
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

}
