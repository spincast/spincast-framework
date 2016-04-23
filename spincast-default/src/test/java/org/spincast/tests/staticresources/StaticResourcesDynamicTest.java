package org.spincast.tests.staticresources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;
import org.spincast.core.exchange.IDefaultRequestContext;
import org.spincast.core.routing.IHandler;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.defaults.tests.DefaultIntegrationTestingBase;
import org.spincast.shaded.org.apache.commons.io.FileUtils;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.testing.core.utils.SpincastTestHttpResponse;

public class StaticResourcesDynamicTest extends DefaultIntegrationTestingBase {

    @Test
    public void dynamicFileSavedByMainHandler() throws Exception {

        String generatedCssFilePath = createTestingFilePath("generated.css");
        final File generatedCssFile = new File(generatedCssFilePath);
        if(generatedCssFile.isFile()) {
            generatedCssFile.delete();
        }

        final int[] nbrTimeCalled = new int[]{0};
        final String content1 = "body{ font-size:12px;}";

        getRouter().file("/generated.css").fileSystem(generatedCssFilePath).save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                try {

                    nbrTimeCalled[0]++;

                    FileUtils.writeStringToFile(generatedCssFile,
                                                content1,
                                                "UTF-8");
                    assertTrue(generatedCssFile.isFile());

                    context.response()
                           .sendCharacters(content1,
                                           "text/css");
                } catch(Exception ex) {
                    throw SpincastStatics.runtimize(ex);
                }
            }
        });

        SpincastTestHttpResponse response = get("/generated.css");

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("text/css", response.getContentType());
        assertEquals(content1, response.getContent());

        assertEquals(1, nbrTimeCalled[0]);
        assertTrue(generatedCssFile.isFile());

        response = get("/generated.css");

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("text/css", response.getContentType());
        assertEquals(content1, response.getContent());

        // Still called only once!
        assertEquals(1, nbrTimeCalled[0]);
        assertTrue(generatedCssFile.isFile());
    }

    @Test
    public void dynamicFileSavedByFilter() throws Exception {

        String generatedCssFilePath = createTestingFilePath("generated.css");
        final File generatedCssFile = new File(generatedCssFilePath);
        if(generatedCssFile.isFile()) {
            generatedCssFile.delete();
        }

        final int[] nbrTimeCalled = new int[]{0};
        final String content1 = "body{ font-size:12px;}";

        getRouter().file("/generated.css").fileSystem(generatedCssFilePath).save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                try {

                    nbrTimeCalled[0]++;

                    context.response().sendCharacters(content1, "text/css");
                } catch(Exception ex) {
                    throw SpincastStatics.runtimize(ex);
                }
            }
        });

        SpincastTestHttpResponse response = get("/generated.css");

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("text/css", response.getContentType());
        assertEquals(content1, response.getContent());

        assertEquals(1, nbrTimeCalled[0]);
        assertTrue(generatedCssFile.isFile());

        response = get("/generated.css");

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("text/css", response.getContentType());
        assertEquals(content1, response.getContent());

        // Still called only once!
        assertEquals(1, nbrTimeCalled[0]);
        assertTrue(generatedCssFile.isFile());
    }

    @Test
    public void dynamicFileDisableSaveByFilterByFlushing() throws Exception {

        String generatedCssFilePath = createTestingFilePath("generated.css");
        final File generatedCssFile = new File(generatedCssFilePath);
        if(generatedCssFile.isFile()) {
            generatedCssFile.delete();
        }

        final int[] nbrTimeCalled = new int[]{0};
        final String content1 = "body{ font-size:12px;}";

        getRouter().file("/generated.css").fileSystem(generatedCssFilePath).save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                try {

                    nbrTimeCalled[0]++;

                    // flush!
                    context.response().sendCharacters(content1, "text/css", true);
                } catch(Exception ex) {
                    throw SpincastStatics.runtimize(ex);
                }
            }
        });

        SpincastTestHttpResponse response = get("/generated.css");

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("text/css", response.getContentType());
        assertEquals(content1, response.getContent());

        assertEquals(1, nbrTimeCalled[0]);
        assertFalse(generatedCssFile.isFile());

        response = get("/generated.css");

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("text/css", response.getContentType());
        assertEquals(content1, response.getContent());

        // Called 2 times now because the filter can't save the resource!
        assertEquals(2, nbrTimeCalled[0]);
        assertFalse(generatedCssFile.isFile());
    }

    @Test
    public void dynamicDirResourcesSavedByMainHandler() throws Exception {

        String dynamicDirPath = createTestingFilePath("generated");
        final File dynamicDir = new File(dynamicDirPath);
        if(dynamicDir.isDirectory()) {
            FileUtils.deleteQuietly(dynamicDir);
        }

        final int[] nbrTimeCalled = new int[]{0};

        getRouter().dir("/generated/*{resourcePath}").fileSystem(dynamicDir.getAbsolutePath())
                   .save(new IHandler<IDefaultRequestContext>() {

                       @Override
                       public void handle(IDefaultRequestContext context) {

                           try {

                               nbrTimeCalled[0]++;

                               String resourcePath = context.request().getPathParam("resourcePath");

                               File newFile = new File(dynamicDir.getAbsolutePath() + "/" + resourcePath);

                               FileUtils.writeStringToFile(newFile, "path : " + resourcePath, "UTF-8");
                               assertTrue(newFile.isFile());

                               context.response().sendCharacters("path : " + resourcePath, "text/css");
                           } catch(Exception ex) {
                               throw SpincastStatics.runtimize(ex);
                           }
                       }
                   });

        SpincastTestHttpResponse response = get("/generated/test1.css");
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("text/css", response.getContentType());
        assertEquals("path : test1.css", response.getContent());
        assertEquals(1, nbrTimeCalled[0]);

        response = get("/generated/test1.css");
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("text/css", response.getContentType());
        assertEquals("path : test1.css", response.getContent());
        // Still called only once!
        assertEquals(1, nbrTimeCalled[0]);

        response = get("/generated/test2.css");
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("text/css", response.getContentType());
        assertEquals("path : test2.css", response.getContent());
        assertEquals(2, nbrTimeCalled[0]);

        response = get("/generated/test2.css");
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("text/css", response.getContentType());
        assertEquals("path : test2.css", response.getContent());
        // Still 2!
        assertEquals(2, nbrTimeCalled[0]);

    }

    @Test
    public void dynamicDirUnsafePath() throws Exception {

        String dynamicDirPath = createTestingFilePath("generated");
        final File dynamicDir = new File(dynamicDirPath);
        if(dynamicDir.isDirectory()) {
            FileUtils.deleteQuietly(dynamicDir);
        }

        final int[] nbrTimeCalled = new int[]{0};

        getRouter().dir("/generated/*{resourcePath}").fileSystem(dynamicDir.getAbsolutePath())
                   .save(new IHandler<IDefaultRequestContext>() {

                       @Override
                       public void handle(IDefaultRequestContext context) {

                           try {

                               nbrTimeCalled[0]++;

                               String resourcePath = context.request().getPathParam("resourcePath");

                               File newFile = new File(dynamicDir.getAbsolutePath() + "/" + resourcePath);

                               FileUtils.writeStringToFile(newFile, "path : " + resourcePath, "UTF-8");
                               assertTrue(newFile.isFile());

                               context.response().sendCharacters("path : " + resourcePath, "text/css");
                           } catch(Exception ex) {
                               throw SpincastStatics.runtimize(ex);
                           }
                       }
                   });

        SpincastTestHttpResponse response = get("/generated/../test1.css");
        assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, response.getStatus());
    }

    @Test
    public void dynamicDirResourcesSavedByFilter() throws Exception {

        String dynamicDirPath = createTestingFilePath("generated");
        final File dynamicDir = new File(dynamicDirPath);
        if(dynamicDir.isDirectory()) {
            FileUtils.deleteQuietly(dynamicDir);
        }

        final int[] nbrTimeCalled = new int[]{0};

        getRouter().dir("/generated/*{resourcePath}").fileSystem(dynamicDir.getAbsolutePath())
                   .save(new IHandler<IDefaultRequestContext>() {

                       @Override
                       public void handle(IDefaultRequestContext context) {

                           try {

                               nbrTimeCalled[0]++;

                               String resourcePath = context.request().getPathParam("resourcePath");
                               context.response().sendCharacters("path : " + resourcePath, "text/css");
                           } catch(Exception ex) {
                               throw SpincastStatics.runtimize(ex);
                           }
                       }
                   });

        SpincastTestHttpResponse response = get("/generated/test1.css");
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("text/css", response.getContentType());
        assertEquals("path : test1.css", response.getContent());
        assertEquals(1, nbrTimeCalled[0]);

        response = get("/generated/test1.css");
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("text/css", response.getContentType());
        assertEquals("path : test1.css", response.getContent());
        // Still called only once!
        assertEquals(1, nbrTimeCalled[0]);

        response = get("/generated/someDir/test2.css");
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("text/css", response.getContentType());
        assertEquals("path : someDir/test2.css", response.getContent());
        assertEquals(2, nbrTimeCalled[0]);

        response = get("/generated/someDir/test2.css");
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("text/css", response.getContentType());
        assertEquals("path : someDir/test2.css", response.getContent());
        // Still 2!
        assertEquals(2, nbrTimeCalled[0]);

    }

}
