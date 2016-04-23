package org.spincast.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.spincast.core.exchange.IDefaultRequestContext;
import org.spincast.core.routing.IHandler;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.defaults.tests.DefaultIntegrationTestingBase;
import org.spincast.shaded.org.apache.commons.io.FileUtils;
import org.spincast.shaded.org.apache.http.HttpEntity;
import org.spincast.shaded.org.apache.http.HttpResponse;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.shaded.org.apache.http.client.HttpClient;
import org.spincast.shaded.org.apache.http.client.methods.HttpPost;
import org.spincast.shaded.org.apache.http.entity.mime.MultipartEntityBuilder;

public class FileUploadTest extends DefaultIntegrationTestingBase {

    @Test
    public void oneFile() throws Exception {

        getRouter().POST("/one").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                try {
                    File uploadedFile = context.request().getUploadedFileFirst("someName");
                    assertNotNull(uploadedFile);

                    String content = FileUtils.readFileToString(uploadedFile, "UTF-8");
                    assertEquals("Le bœuf et l'éléphant!", content);
                } catch(Exception ex) {
                    throw SpincastStatics.runtimize(ex);
                }
            }
        });

        HttpClient httpclient = getHttpClient();

        URL resource = getClass().getClassLoader().getResource("someFile.txt");
        File file = new File(resource.toURI());

        HttpEntity httpEntity = MultipartEntityBuilder.create().addBinaryBody("someName", file).build();

        HttpPost httpPost = new HttpPost(createTestUrl("/one"));
        httpPost.setEntity(httpEntity);

        HttpResponse response = httpclient.execute(httpPost);
        int status = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_OK, status);
    }

    @Test
    public void twoFilesSameName() throws Exception {

        getRouter().POST("/one").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                try {
                    List<File> uploadedFiles = context.request().getUploadedFiles("someName");
                    assertEquals(2, uploadedFiles.size());

                    String content1 = FileUtils.readFileToString(uploadedFiles.get(0), "UTF-8");
                    assertEquals("Le bœuf et l'éléphant!", content1);

                    String content2 = FileUtils.readFileToString(uploadedFiles.get(1), "UTF-8");
                    assertEquals("Le bœuf et l'éléphant! 2", content2);
                } catch(Exception ex) {
                    throw SpincastStatics.runtimize(ex);
                }
            }
        });

        HttpClient httpclient = getHttpClient();

        URL resource = getClass().getClassLoader().getResource("someFile.txt");
        File file = new File(resource.toURI());

        resource = getClass().getClassLoader().getResource("someFile2.txt");
        File file2 = new File(resource.toURI());

        HttpEntity httpEntity = MultipartEntityBuilder.create()
                                                      .addBinaryBody("someName", file)
                                                      .addBinaryBody("someName", file2)
                                                      .build();

        HttpPost httpPost = new HttpPost(createTestUrl("/one"));
        httpPost.setEntity(httpEntity);

        HttpResponse response = httpclient.execute(httpPost);
        int status = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_OK, status);
    }

    @Test
    public void multipleFiles() throws Exception {

        getRouter().POST("/one").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                try {
                    Map<String, List<File>> uploadedFiles = context.request().getUploadedFiles();
                    assertNotNull(uploadedFiles);
                    assertEquals(2, uploadedFiles.size());

                    List<File> uploadedFilesSomeName = context.request().getUploadedFiles("someName");
                    assertNotNull(uploadedFilesSomeName);
                    assertEquals(2, uploadedFilesSomeName.size());

                    File someName1 = uploadedFilesSomeName.get(0);
                    assertNotNull(someName1);
                    String content = FileUtils.readFileToString(someName1, "UTF-8");
                    assertEquals("Le bœuf et l'éléphant!", content);

                    File someName2 = uploadedFilesSomeName.get(1);
                    assertNotNull(someName2);
                    content = FileUtils.readFileToString(someName2, "UTF-8");
                    assertEquals("Le bœuf et l'éléphant! 2", content);

                    List<File> uploadedFilesOther = context.request().getUploadedFiles("other");
                    assertNotNull(uploadedFilesOther);
                    assertEquals(1, uploadedFilesOther.size());

                    File uploadedFileOther = uploadedFilesOther.get(0);
                    assertNotNull(uploadedFileOther);
                    content = FileUtils.readFileToString(uploadedFileOther, "UTF-8");
                    assertEquals("Le bœuf et l'éléphant! 3", content);

                } catch(Exception ex) {
                    throw SpincastStatics.runtimize(ex);
                }
            }
        });

        HttpClient httpclient = getHttpClient();

        URL resource = getClass().getClassLoader().getResource("someFile.txt");
        File file1 = new File(resource.toURI());

        resource = getClass().getClassLoader().getResource("someFile2.txt");
        File file2 = new File(resource.toURI());

        resource = getClass().getClassLoader().getResource("someFile3.txt");
        File file3 = new File(resource.toURI());

        HttpEntity httpEntity = MultipartEntityBuilder.create()
                                                      .addBinaryBody("someName", file1)
                                                      .addBinaryBody("someName", file2)
                                                      .addBinaryBody("other", file3)
                                                      .build();

        HttpPost httpPost = new HttpPost(createTestUrl("/one"));
        httpPost.setEntity(httpEntity);

        HttpResponse response = httpclient.execute(httpPost);
        int status = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_OK, status);

    }

}
