package org.spincast.tests.staticresources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;

import org.junit.Test;
import org.spincast.core.exchange.DefaultRequestContext;
import org.spincast.core.routing.Handler;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.defaults.testing.NoAppStartHttpServerTestingBase;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.plugins.routing.SpincastRouterConfig;
import org.spincast.shaded.org.apache.commons.io.FileUtils;
import org.spincast.shaded.org.apache.http.HttpStatus;

import com.google.inject.Inject;

public class StaticResourcesDynamicTest extends NoAppStartHttpServerTestingBase {

    @Inject
    protected SpincastRouterConfig spincastRouterConfig;

    protected SpincastRouterConfig getSpincastRouterConfig() {
        return this.spincastRouterConfig;
    }

    @Override
    public void beforeTest() {
        super.beforeTest();

        try {
            FileUtils.cleanDirectory(getTestingWritableDir());
        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    @Test
    public void dynamicFileSavedByMainHandler() throws Exception {

        String generatedCssFilePath = createTestingFilePath("generated.css");
        final File generatedCssFile = new File(generatedCssFilePath);
        assertFalse(generatedCssFile.isFile());

        final int[] nbrTimeCalled = new int[]{0};
        final String content1 = "body{ font-size:12px;}";

        getRouter().file("/generated.css").pathAbsolute(generatedCssFilePath).save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                try {

                    nbrTimeCalled[0]++;

                    FileUtils.writeStringToFile(generatedCssFile,
                                                content1,
                                                "UTF-8");
                    assertTrue(generatedCssFile.isFile());

                    context.response()
                           .sendCharacters(content1,
                                           "text/css");
                } catch (Exception ex) {
                    throw SpincastStatics.runtimize(ex);
                }
            }
        });

        HttpResponse response = GET("/generated.css").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("text/css", response.getContentType());
        assertEquals(content1, response.getContentAsString());

        assertEquals(1, nbrTimeCalled[0]);
        assertTrue(generatedCssFile.isFile());

        response = GET("/generated.css").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("text/css", response.getContentType());
        assertEquals(content1, response.getContentAsString());

        // Still called only once!
        assertEquals(1, nbrTimeCalled[0]);
        assertTrue(generatedCssFile.isFile());
    }

    @Test
    public void dynamicFileSavedByFilter() throws Exception {

        String generatedCssFilePath = createTestingFilePath("generated.css");
        final File generatedCssFile = new File(generatedCssFilePath);
        assertFalse(generatedCssFile.isFile());

        final int[] nbrTimeCalled = new int[]{0};
        final String content1 = "body{ font-size:12px;}";

        getRouter().file("/generated.css").pathAbsolute(generatedCssFilePath).save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                try {

                    nbrTimeCalled[0]++;

                    context.response().sendCharacters(content1, "text/css");
                } catch (Exception ex) {
                    throw SpincastStatics.runtimize(ex);
                }
            }
        });

        HttpResponse response = GET("/generated.css").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("text/css", response.getContentType());
        assertEquals(content1, response.getContentAsString());

        assertEquals(1, nbrTimeCalled[0]);
        assertTrue(generatedCssFile.isFile());

        response = GET("/generated.css").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("text/css", response.getContentType());
        assertEquals(content1, response.getContentAsString());

        // Still called only once!
        assertEquals(1, nbrTimeCalled[0]);
        assertTrue(generatedCssFile.isFile());
    }

    @Test
    public void dynamicFileDisableSaveByFilterByFlushing() throws Exception {

        String generatedCssFilePath = createTestingFilePath("generated.css");
        final File generatedCssFile = new File(generatedCssFilePath);
        assertFalse(generatedCssFile.isFile());

        final int[] nbrTimeCalled = new int[]{0};
        final String content1 = "body{ font-size:12px;}";

        getRouter().file("/generated.css").pathAbsolute(generatedCssFilePath).save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                try {

                    nbrTimeCalled[0]++;

                    // flush!
                    context.response().sendCharacters(content1, "text/css", true);
                } catch (Exception ex) {
                    throw SpincastStatics.runtimize(ex);
                }
            }
        });

        HttpResponse response = GET("/generated.css").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("text/css", response.getContentType());
        assertEquals(content1, response.getContentAsString());

        assertEquals(1, nbrTimeCalled[0]);
        assertFalse(generatedCssFile.isFile());

        response = GET("/generated.css").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("text/css", response.getContentType());
        assertEquals(content1, response.getContentAsString());

        // Called 2 times now because the filter can't save the resource!
        assertEquals(2, nbrTimeCalled[0]);
        assertFalse(generatedCssFile.isFile());
    }

    @Test
    public void dynamicDirResourcesSavedByMainHandler() throws Exception {

        String dynamicDirPath = createTestingFilePath("generated");
        final File dynamicDir = new File(dynamicDirPath);
        assertFalse(dynamicDir.exists());

        final int[] nbrTimeCalled = new int[]{0};

        getRouter().dir("/generated/*{resourcePath}").pathAbsolute(dynamicDir.getAbsolutePath())
                   .save(new Handler<DefaultRequestContext>() {

                       @Override
                       public void handle(DefaultRequestContext context) {

                           try {

                               nbrTimeCalled[0]++;

                               String resourcePath = context.request().getPathParam("resourcePath");

                               File newFile = new File(dynamicDir.getAbsolutePath() + "/" + resourcePath);

                               FileUtils.writeStringToFile(newFile, "path : " + resourcePath, "UTF-8");
                               assertTrue(newFile.isFile());

                               context.response().sendCharacters("path : " + resourcePath, "text/css");
                           } catch (Exception ex) {
                               throw SpincastStatics.runtimize(ex);
                           }
                       }
                   });

        HttpResponse response = GET("/generated/test1.css").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("text/css", response.getContentType());
        assertEquals("path : test1.css", response.getContentAsString());
        assertEquals(1, nbrTimeCalled[0]);

        response = GET("/generated/test1.css").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("text/css", response.getContentType());
        assertEquals("path : test1.css", response.getContentAsString());
        // Still called only once!
        assertEquals(1, nbrTimeCalled[0]);

        response = GET("/generated/test2.css").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("text/css", response.getContentType());
        assertEquals("path : test2.css", response.getContentAsString());
        assertEquals(2, nbrTimeCalled[0]);

        response = GET("/generated/test2.css").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("text/css", response.getContentType());
        assertEquals("path : test2.css", response.getContentAsString());
        // Still 2!
        assertEquals(2, nbrTimeCalled[0]);

    }

    @Test
    public void dynamicDirUnsafePath() throws Exception {

        String dynamicDirPath = createTestingFilePath("generated");
        final File dynamicDir = new File(dynamicDirPath);
        assertFalse(dynamicDir.exists());

        final int[] nbrTimeCalled = new int[]{0};

        getRouter().dir("/generated/*{resourcePath}").pathAbsolute(dynamicDir.getAbsolutePath())
                   .save(new Handler<DefaultRequestContext>() {

                       @Override
                       public void handle(DefaultRequestContext context) {

                           try {

                               nbrTimeCalled[0]++;

                               String resourcePath = context.request().getPathParam("resourcePath");

                               File newFile = new File(dynamicDir.getAbsolutePath() + "/" + resourcePath);

                               FileUtils.writeStringToFile(newFile, "path : " + resourcePath, "UTF-8");
                               assertTrue(newFile.isFile());

                               context.response().sendCharacters("path : " + resourcePath, "text/css");
                           } catch (Exception ex) {
                               throw SpincastStatics.runtimize(ex);
                           }
                       }
                   });

        HttpResponse response = GET("/generated/../test1.css").send();
        assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, response.getStatus());
    }

    @Test
    public void dynamicDirResourcesSavedByFilter() throws Exception {

        String dynamicDirPath = createTestingFilePath("generated");
        final File dynamicDir = new File(dynamicDirPath);
        assertFalse(dynamicDir.isFile());

        final int[] nbrTimeCalled = new int[]{0};

        getRouter().dir("/generated/*{resourcePath}").pathAbsolute(dynamicDir.getAbsolutePath())
                   .save(new Handler<DefaultRequestContext>() {

                       @Override
                       public void handle(DefaultRequestContext context) {

                           try {

                               nbrTimeCalled[0]++;

                               String resourcePath = context.request().getPathParam("resourcePath");
                               context.response().sendCharacters("path : " + resourcePath, "text/css");
                           } catch (Exception ex) {
                               throw SpincastStatics.runtimize(ex);
                           }
                       }
                   });

        HttpResponse response = GET("/generated/test1.css").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("text/css", response.getContentType());
        assertEquals("path : test1.css", response.getContentAsString());
        assertEquals(1, nbrTimeCalled[0]);

        response = GET("/generated/test1.css").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("text/css", response.getContentType());
        assertEquals("path : test1.css", response.getContentAsString());
        // Still called only once!
        assertEquals(1, nbrTimeCalled[0]);

        response = GET("/generated/someDir/test2.css").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("text/css", response.getContentType());
        assertEquals("path : someDir/test2.css", response.getContentAsString());
        assertEquals(2, nbrTimeCalled[0]);

        response = GET("/generated/someDir/test2.css").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("text/css", response.getContentType());
        assertEquals("path : someDir/test2.css", response.getContentAsString());
        // Still 2!
        assertEquals(2, nbrTimeCalled[0]);
    }

    @Test
    public void dynamicDirSplatParamUsedInTargetPath() throws Exception {

        try {
            getRouter().dir("/generated/*{splat}").pathAbsolute(getTestingWritableDir().getCanonicalPath() + "/*{splat}")
                       .save(new Handler<DefaultRequestContext>() {

                           @Override
                           public void handle(DefaultRequestContext context) {
                               fail();
                           }
                       });
            fail();
        } catch (Exception ex) {
        }
    }

    @Test
    public void dynamicDirDynParamUserdInTargetPath() throws Exception {

        try {
            getRouter().dir("/generated/*{splat}").pathAbsolute(getTestingWritableDir().getCanonicalPath() + "/${splat}")
                       .save(new Handler<DefaultRequestContext>() {

                           @Override
                           public void handle(DefaultRequestContext context) {
                               fail();
                           }
                       });
            fail();
        } catch (Exception ex) {
        }
    }

    @Test
    public void dynamicFileWithDynParams() throws Exception {

        final int[] nbrTimeCalled = new int[]{0};
        final String content = "body{ font-size:12px;}";

        File file = new File(getTestingWritableDir() + "/somepath/test1.css");
        assertFalse(file.exists());

        getRouter().file("/test/${name}")
                   .pathAbsolute(getTestingWritableDir() + "/somepath/${name}")
                   .save(new Handler<DefaultRequestContext>() {

                       @Override
                       public void handle(DefaultRequestContext context) {
                           nbrTimeCalled[0]++;
                           context.response().sendCharacters(content, "text/css");
                       }
                   });

        HttpResponse response = GET("/test/test1.css").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("text/css", response.getContentType());
        assertEquals(content, response.getContentAsString());

        assertEquals(1, nbrTimeCalled[0]);
        assertTrue(file.isFile());

        response = GET("/test/test1.css").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("text/css", response.getContentType());
        assertEquals(content, response.getContentAsString());

        // Still called only once!
        assertEquals(1, nbrTimeCalled[0]);
        assertTrue(file.isFile());
    }

    @Test
    public void dynamicFileWithDynParamNotLast() throws Exception {

        final int[] nbrTimeCalled = new int[]{0};
        final String content = "body{ font-size:12px;}";

        File file = new File(getTestingWritableDir() + "/somepath/test1.css");
        assertFalse(file.exists());

        getRouter().file("/test/${name}/coco")
                   .pathAbsolute(getTestingWritableDir() + "/somepath/${name}.css")
                   .save(new Handler<DefaultRequestContext>() {

                       @Override
                       public void handle(DefaultRequestContext context) {
                           nbrTimeCalled[0]++;
                           context.response().sendCharacters(content, "text/css");
                       }
                   });

        HttpResponse response = GET("/test/test1").send();
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = GET("/test/test1/coco").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("text/css", response.getContentType());
        assertEquals(content, response.getContentAsString());

        assertEquals(1, nbrTimeCalled[0]);
        assertTrue(file.isFile());

        response = GET("/test/test1/coco").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("text/css", response.getContentType());
        assertEquals(content, response.getContentAsString());

        // Still called only once!
        assertEquals(1, nbrTimeCalled[0]);
        assertTrue(file.isFile());
    }

    @Test
    public void dynamicFileWithTwoDynParams() throws Exception {

        final int[] nbrTimeCalled = new int[]{0};
        final String content = "body{ font-size:12px;}";

        File file = new File(getTestingWritableDir() + "/somepath/test2/test1.css");
        assertFalse(file.exists());

        getRouter().file("/${param1}/${param2}")
                   .pathAbsolute(getTestingWritableDir() + "/somepath/${param2}/${param1}.css")
                   .save(new Handler<DefaultRequestContext>() {

                       @Override
                       public void handle(DefaultRequestContext context) {
                           nbrTimeCalled[0]++;
                           context.response().sendCharacters(content, "text/css");
                       }
                   });

        HttpResponse response = GET("/test1/test2").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("text/css", response.getContentType());
        assertEquals(content, response.getContentAsString());

        assertEquals(1, nbrTimeCalled[0]);
        assertTrue(file.isFile());

        response = GET("/test1/test2").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("text/css", response.getContentType());
        assertEquals(content, response.getContentAsString());

        // Still called only once!
        assertEquals(1, nbrTimeCalled[0]);
        assertTrue(file.isFile());
    }

    @Test
    public void dynamicFileWithTwoDynParamButSpecificTargetFile() throws Exception {

        final int[] nbrTimeCalled = new int[]{0};
        final String content = "body{ font-size:12px;}";

        File file = new File(getTestingWritableDir() + "/somepath/toto.css");
        assertFalse(file.exists());

        getRouter().file("/${param1}")
                   .pathAbsolute(getTestingWritableDir() + "/somepath/toto.css")
                   .save(new Handler<DefaultRequestContext>() {

                       @Override
                       public void handle(DefaultRequestContext context) {
                           nbrTimeCalled[0]++;
                           context.response().sendCharacters(content, "text/css");
                       }
                   });

        HttpResponse response = GET("/test").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("text/css", response.getContentType());
        assertEquals(content, response.getContentAsString());

        assertEquals(1, nbrTimeCalled[0]);
        assertTrue(file.isFile());

        response = GET("/test").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("text/css", response.getContentType());
        assertEquals(content, response.getContentAsString());

        // Still called only once!
        assertEquals(1, nbrTimeCalled[0]);
        assertTrue(file.isFile());
    }

    @Test
    public void dynamicFileWithDynParamsInvalid() throws Exception {

        final int[] nbrTimeCalled = new int[]{0};
        final String content = "body{ font-size:12px;}";

        File file = new File(getTestingWritableDir() + "/somepath/test1.css");
        assertFalse(file.exists());

        File file2 = new File(getTestingWritableDir() + "/somepath/nope/test1.css");
        assertFalse(file2.exists());

        getRouter().file("/test/${name}")
                   .pathAbsolute(getTestingWritableDir() + "/somepath/${name}")
                   .save(new Handler<DefaultRequestContext>() {

                       @Override
                       public void handle(DefaultRequestContext context) {
                           nbrTimeCalled[0]++;
                           context.response().sendCharacters(content, "text/css");
                       }
                   });

        HttpResponse response = GET("/test/nope/test1.css").send();

        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        assertEquals(0, nbrTimeCalled[0]);
        assertFalse(file.isFile());
        assertFalse(file2.isFile());
    }

    @Test
    public void dynamicFileWithSplatParams() throws Exception {

        try {
            getRouter().file("/test/*{name}")
                       .pathAbsolute(getTestingWritableDir() + "/somepath/titi")
                       .save(new Handler<DefaultRequestContext>() {

                           @Override
                           public void handle(DefaultRequestContext context) {
                               fail();
                           }
                       });
            fail();
        } catch (Exception ex) {
        }
    }

    @Test
    public void queryStringDisableCacheByDefault() throws Exception {

        String generatedCssFilePath = createTestingFilePath("generated.txt");
        final File generatedCssFile = new File(generatedCssFilePath);
        assertFalse(generatedCssFile.isFile());

        final int[] nbrTimeCalled = new int[]{0};

        getRouter().file("/generated.txt").pathAbsolute(generatedCssFilePath).save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                try {

                    nbrTimeCalled[0]++;

                    context.response().sendPlainText(context.request().getQueryString(false));
                } catch (Exception ex) {
                    throw SpincastStatics.runtimize(ex);
                }
            }
        });

        HttpResponse response = GET("/generated.txt").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("", response.getContentAsString());
        assertEquals(1, nbrTimeCalled[0]);
        assertTrue(generatedCssFile.isFile());

        response = GET("/generated.txt").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("", response.getContentAsString());
        assertEquals(1, nbrTimeCalled[0]);
        assertTrue(generatedCssFile.isFile());

        response = GET("/generated.txt?test=123").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("test=123", response.getContentAsString());
        assertEquals(2, nbrTimeCalled[0]);
        assertTrue(generatedCssFile.isFile());
    }

    @Test
    public void queryStringDisableCacheForced() throws Exception {

        String generatedCssFilePath = createTestingFilePath("generated.txt");
        final File generatedCssFile = new File(generatedCssFilePath);
        assertFalse(generatedCssFile.isFile());

        final int[] nbrTimeCalled = new int[]{0};

        getRouter().file("/generated.txt").pathAbsolute(generatedCssFilePath).save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                try {

                    nbrTimeCalled[0]++;

                    context.response().sendPlainText(context.request().getQueryString(false));
                } catch (Exception ex) {
                    throw SpincastStatics.runtimize(ex);
                }
            }
        }, false); // false!

        HttpResponse response = GET("/generated.txt").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("", response.getContentAsString());
        assertEquals(1, nbrTimeCalled[0]);
        assertTrue(generatedCssFile.isFile());

        response = GET("/generated.txt").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("", response.getContentAsString());
        assertEquals(1, nbrTimeCalled[0]);
        assertTrue(generatedCssFile.isFile());

        response = GET("/generated.txt?test=123").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("test=123", response.getContentAsString());
        assertEquals(2, nbrTimeCalled[0]);
        assertTrue(generatedCssFile.isFile());
    }

    @Test
    public void ignoreQueryString() throws Exception {

        String generatedCssFilePath = createTestingFilePath("generated.txt");
        final File generatedCssFile = new File(generatedCssFilePath);
        assertFalse(generatedCssFile.isFile());

        final int[] nbrTimeCalled = new int[]{0};

        getRouter().file("/generated.txt").pathAbsolute(generatedCssFilePath).save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                try {

                    nbrTimeCalled[0]++;

                    context.response().sendPlainText(context.request().getQueryString(false));
                } catch (Exception ex) {
                    throw SpincastStatics.runtimize(ex);
                }
            }
        }, true); // true!

        HttpResponse response = GET("/generated.txt").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("", response.getContentAsString());
        assertEquals(1, nbrTimeCalled[0]);
        assertTrue(generatedCssFile.isFile());

        response = GET("/generated.txt").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("", response.getContentAsString());
        assertEquals(1, nbrTimeCalled[0]);
        assertTrue(generatedCssFile.isFile());

        // Gets the cached version
        response = GET("/generated.txt?test=123").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("", response.getContentAsString());
        assertEquals(1, nbrTimeCalled[0]);
        assertTrue(generatedCssFile.isFile());
    }

}
