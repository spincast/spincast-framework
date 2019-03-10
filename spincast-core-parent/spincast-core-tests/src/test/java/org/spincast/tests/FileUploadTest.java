package org.spincast.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.spincast.core.exchange.DefaultRequestContext;
import org.spincast.core.routing.Handler;
import org.spincast.core.server.UploadedFile;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.shaded.org.apache.commons.io.FileUtils;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.testing.defaults.NoAppStartHttpServerTestingBase;

public class FileUploadTest extends NoAppStartHttpServerTestingBase {

    @Test
    public void oneFile() throws Exception {

        getRouter().POST("/one").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                try {
                    UploadedFile uploadedFile = context.request().getUploadedFileFirst("someName");
                    assertNotNull(uploadedFile);

                    String content = FileUtils.readFileToString(uploadedFile.getFile(), "UTF-8");
                    assertEquals("Le bœuf et l'éléphant!", content);
                } catch (Exception ex) {
                    throw SpincastStatics.runtimize(ex);
                }
            }
        });

        HttpResponse response = POST("/one").addFileToUploadBody("someFile.txt", true, "someName").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void twoFilesSameName() throws Exception {

        getRouter().POST("/one").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                try {
                    List<UploadedFile> uploadedFiles = context.request().getUploadedFiles("someName");
                    assertEquals(2, uploadedFiles.size());

                    String content1 = FileUtils.readFileToString(uploadedFiles.get(0).getFile(), "UTF-8");
                    assertEquals("Le bœuf et l'éléphant!", content1);

                    String content2 = FileUtils.readFileToString(uploadedFiles.get(1).getFile(), "UTF-8");
                    assertEquals("Le bœuf et l'éléphant! 2", content2);
                } catch (Exception ex) {
                    throw SpincastStatics.runtimize(ex);
                }
            }
        });

        HttpResponse response = POST("/one").addFileToUploadBody("someFile.txt", true, "someName")
                                            .addFileToUploadBody("someFile2.txt", true, "someName").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void multipleFiles() throws Exception {

        getRouter().POST("/one").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                try {
                    Map<String, List<UploadedFile>> uploadedFiles = context.request().getUploadedFiles();
                    assertNotNull(uploadedFiles);
                    assertEquals(3, uploadedFiles.size());

                    List<UploadedFile> uploadedFilesSomeName = context.request().getUploadedFiles("someName");
                    assertNotNull(uploadedFilesSomeName);
                    assertEquals(2, uploadedFilesSomeName.size());

                    File someName1 = uploadedFilesSomeName.get(0).getFile();
                    assertNotNull(someName1);
                    String content = FileUtils.readFileToString(someName1, "UTF-8");
                    assertEquals("Le bœuf et l'éléphant!", content);

                    File someName2 = uploadedFilesSomeName.get(1).getFile();
                    assertNotNull(someName2);
                    content = FileUtils.readFileToString(someName2, "UTF-8");
                    assertEquals("Le bœuf et l'éléphant! 2", content);

                    List<UploadedFile> uploadedFilesOther = context.request().getUploadedFiles("other");
                    assertNotNull(uploadedFilesOther);
                    assertEquals(1, uploadedFilesOther.size());

                    File uploadedFileOther = uploadedFilesOther.get(0).getFile();
                    assertNotNull(uploadedFileOther);
                    content = FileUtils.readFileToString(uploadedFileOther, "UTF-8");
                    assertEquals("Le bœuf et l'éléphant! 3", content);

                    List<UploadedFile> fileSystemBaseds = context.request().getUploadedFiles("fileSystemBased");
                    assertNotNull(fileSystemBaseds);
                    assertEquals(1, fileSystemBaseds.size());

                    File fileSystemBased = fileSystemBaseds.get(0).getFile();
                    assertNotNull(fileSystemBased);
                    content = FileUtils.readFileToString(fileSystemBased, "UTF-8");
                    assertEquals("Le bœuf et l'éléphant!", content);

                } catch (Exception ex) {
                    throw SpincastStatics.runtimize(ex);
                }
            }
        });

        InputStream stream = getClass().getClassLoader().getResourceAsStream("someFile.txt");
        assertNotNull(stream);
        String testFilePath = createTestingFilePath();
        FileUtils.copyInputStreamToFile(stream, new File(testFilePath));

        HttpResponse response = POST("/one").addFileToUploadBody("someFile.txt", true, "someName")
                                            .addFileToUploadBody("someFile2.txt", true, "someName")
                                            .addFileToUploadBody("someFile3.txt", true, "other")
                                            .addFileToUploadBody(testFilePath, false, "fileSystemBased")
                                            .send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());

    }

}
