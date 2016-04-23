package org.spincast.tests.staticresources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.UUID;

import org.junit.Test;
import org.spincast.core.exceptions.NotFoundException;
import org.spincast.core.exchange.IDefaultRequestContext;
import org.spincast.core.routing.IHandler;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.defaults.tests.DefaultIntegrationTestingBase;
import org.spincast.shaded.org.apache.commons.io.FileUtils;
import org.spincast.shaded.org.apache.commons.lang3.StringUtils;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.testing.core.utils.SpincastTestHttpResponse;
import org.spincast.testing.core.utils.SpincastTestUtils;

public class StaticResourcesTest extends DefaultIntegrationTestingBase {

    @Test
    public void fileNotSaved() throws Exception {

        getRouter().file("/one").classpath("/someFile.txt");

        SpincastTestHttpResponse response = get("/one");

        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());
    }

    @Test
    public void fileClasspathSlash() throws Exception {

        getRouter().file("/one").classpath("/someFile.txt").save();

        SpincastTestHttpResponse response = get("/one");

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("text/plain", response.getContentType());
        assertEquals("Le bœuf et l'éléphant!", response.getContent());
    }

    @Test
    public void fileClasspathTextPlain() throws Exception {

        getRouter().file("/file").classpath("someFile.txt").save();

        SpincastTestHttpResponse response = get("/file");
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("text/plain", response.getContentType());
        assertEquals("Le bœuf et l'éléphant!", response.getContent());
    }

    @Test
    public void fileClasspathImage() throws Exception {

        getRouter().file("/one/two/three").classpath("/image.jpg").save();

        SpincastTestHttpResponse response = get("/one/two/three");
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("image/jpeg", response.getContentType());
    }

    @Test
    public void dirClasspath() throws Exception {

        getRouter().dir("/one/two/three").classpath("/oneDir").save();

        SpincastTestHttpResponse response = get("/one/two/three/file2.txt");
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("text/plain", response.getContentType());
        assertEquals("file content 2", response.getContent());

        response = get("/one/two/three/image2.jpg");
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("image/jpeg", response.getContentType());

        response = get("/one/two/three");
        assertEquals(HttpStatus.SC_FORBIDDEN, response.getStatus());

    }

    @Test
    public void dirNotFile() throws Exception {

        getRouter().dir("/dir").classpath("/oneDir").save();

        SpincastTestHttpResponse response = get("/dir/file2.txt");
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("text/plain", response.getContentType());
        assertEquals("file content 2", response.getContent());

        response = get("/dir");
        assertEquals(HttpStatus.SC_FORBIDDEN, response.getStatus());

        response = get("/dir/");
        assertEquals(HttpStatus.SC_FORBIDDEN, response.getStatus());

        response = get("/dir/nope");
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());
    }

    @Test
    public void dirHasPrecedenceOverStandardRoutes() throws Exception {

        getRouter().GET("/route1").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText("route1");
            }
        });

        getRouter().GET("/dir/routes2").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText("routes2");
            }
        });

        getRouter().dir("/dir").classpath("/oneDir").save();

        SpincastTestHttpResponse response = get("/route1");
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("route1", response.getContent());

        response = get("/dir/routes2");
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = get("/dir/file2.txt");
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("text/plain", response.getContentType());
        assertEquals("file content 2", response.getContent());
    }

    @Test
    public void fileHasPrecedenceOverStandardRoutes() throws Exception {

        getRouter().file("/route3").classpath("/oneDir/file2.txt").save();

        getRouter().GET("/").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText("route1");
            }
        });

        getRouter().GET("/routes2").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText("routes2");
            }
        });

        getRouter().GET("/route3").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText("route3");
            }
        });

        getRouter().file("/").classpath("/oneDir/file2.txt").save();

        SpincastTestHttpResponse response = get("/");
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("text/plain", response.getContentType());
        assertEquals("file content 2", response.getContent());

        response = get("/routes2");
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("routes2", response.getContent());

        response = get("/route3");
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("text/plain", response.getContentType());
        assertEquals("file content 2", response.getContent());
    }

    @Test
    public void fileFileSystem() throws Exception {

        File fileTarget = SpincastTestUtils.generateTempClassFile(getTestingWritableDir());
        assertTrue(fileTarget.isFile());

        getRouter().file("/file").fileSystem(fileTarget.getAbsolutePath()).save();

        SpincastTestHttpResponse response = get("/file");
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("application/java", response.getContentType());
    }

    @Test
    public void dirFileSystem() throws Exception {

        File dir = new File(getTestingWritableDir() + "/dirFileSystem");
        assertTrue(dir.mkdirs());

        File fileTemp = SpincastTestUtils.generateTempClassFile(getTestingWritableDir());
        assertTrue(fileTemp.isFile());

        File fileTarget = new File(dir.getAbsolutePath() + "/" + fileTemp.getName());
        FileUtils.copyFile(fileTemp, fileTarget);
        assertTrue(fileTarget.isFile());

        getRouter().dir("/dir").fileSystem(dir.getAbsolutePath()).save();

        SpincastTestHttpResponse response = get("/dir/");
        assertEquals(HttpStatus.SC_FORBIDDEN, response.getStatus());

        response = get("/dir/" + fileTarget.getName());
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("application/java", response.getContentType());
    }

    @Test
    public void pathRequired() throws Exception {

        try {
            getRouter().file("/one").save();
            fail();
        } catch(Exception ex) {
        }
    }

    @Test
    public void urlRequired() throws Exception {

        try {
            getRouter().file(null).classpath("/").save();
            fail();
        } catch(Exception ex) {
        }
    }

    @Test
    public void generatorsOnlyForFileSystemPaths() throws Exception {

        try {
            getRouter().file("/").classpath("/").save(SpincastTestUtils.dummyRouteHandler);
            fail();
        } catch(Exception ex) {
            System.out.println(ex);
        }
    }

    @Test
    public void lastPathWins() throws Exception {

        getRouter().file("/one/two/three")
                   .classpath("/nope")
                   .fileSystem("/nope")
                   .classpath("/image.jpg")
                   .save();

        SpincastTestHttpResponse response = get("/one/two/three");
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("image/jpeg", response.getContentType());
    }

    @Test
    public void valid() throws Exception {

        getRouter().dir("/one").classpath("/oneDir").save();

        SpincastTestHttpResponse response = get("/one/image2.jpg");
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("image/jpeg", response.getContentType());
        assertFalse(StringUtils.isBlank(response.getContent()));
    }

    @Test
    public void valid2() throws Exception {

        getRouter().dir("/one").classpath("/oneDir").save();

        SpincastTestHttpResponse response = get("/one/dir2/image3.jpg");
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("image/jpeg", response.getContentType());
        assertFalse(StringUtils.isBlank(response.getContent()));
    }

    @Test
    public void valid3() throws Exception {

        getRouter().dir("/one").classpath("/oneDir").save();

        SpincastTestHttpResponse response = get("/one/dir2/dir3/image4.jpg");
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("image/jpeg", response.getContentType());
        assertFalse(StringUtils.isBlank(response.getContent()));
    }

    @Test
    public void validButNotFound() throws Exception {

        getRouter().dir("/one").classpath("/oneDir").save();

        SpincastTestHttpResponse response = get("/one/dir2/dir3/nope.jpg");
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());
    }

    @Test
    public void splatValid() throws Exception {

        getRouter().dir("/one/*{remaining}").classpath("/oneDir").save();

        SpincastTestHttpResponse response = get("/one/image2.jpg");
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("image/jpeg", response.getContentType());
        assertFalse(StringUtils.isBlank(response.getContent()));
    }

    @Test
    public void splatValid2() throws Exception {

        getRouter().dir("/one/*{remaining}").classpath("/oneDir").save();

        SpincastTestHttpResponse response = get("/one/dir2/image3.jpg");
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("image/jpeg", response.getContentType());
        assertFalse(StringUtils.isBlank(response.getContent()));
    }

    @Test
    public void splatValid3() throws Exception {

        getRouter().dir("/one/*{remaining}").classpath("/oneDir").save();

        SpincastTestHttpResponse response = get("/one/dir2/dir3/image4.jpg");
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("image/jpeg", response.getContentType());
        assertFalse(StringUtils.isBlank(response.getContent()));
    }

    @Test
    public void splatValidButNotFound() throws Exception {

        getRouter().dir("/one/*{remaining}").classpath("/oneDir").save();

        SpincastTestHttpResponse response = get("/one/dir2/dir3/nope.jpg");
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());
    }

    @Test
    public void dirDynamicParametersInvalid() throws Exception {

        getRouter().dir("/one").fileSystem(getTestingWritableDir() + "/one").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
            }
        });

        try {
            getRouter().dir("/${param1}").fileSystem(getTestingWritableDir() + "/one")
                       .save(new IHandler<IDefaultRequestContext>() {

                           @Override
                           public void handle(IDefaultRequestContext context) {
                           }
                       });
            fail();
        } catch(Exception ex) {
        }

        try {
            getRouter().dir("/${param1}/one").fileSystem(getTestingWritableDir() + "/one")
                       .save(new IHandler<IDefaultRequestContext>() {

                           @Override
                           public void handle(IDefaultRequestContext context) {
                           }
                       });
            fail();
        } catch(Exception ex) {
        }

        try {
            getRouter().dir("/one/${param1}").fileSystem(getTestingWritableDir() + "/one")
                       .save(new IHandler<IDefaultRequestContext>() {

                           @Override
                           public void handle(IDefaultRequestContext context) {
                           }
                       });
            fail();
        } catch(Exception ex) {
        }

        try {
            getRouter().dir("/${path:a+}").fileSystem(getTestingWritableDir() + "/one")
                       .save(new IHandler<IDefaultRequestContext>() {

                           @Override
                           public void handle(IDefaultRequestContext context) {
                           }
                       });
            fail();
        } catch(Exception ex) {
        }

        try {
            getRouter().dir("/${path:<N>}").fileSystem(getTestingWritableDir() + "/one")
                       .save(new IHandler<IDefaultRequestContext>() {

                           @Override
                           public void handle(IDefaultRequestContext context) {
                           }
                       });
            fail();
        } catch(Exception ex) {
        }

        try {
            getRouter().dir("/*{param1}/one").fileSystem(getTestingWritableDir() + "/one")
                       .save(new IHandler<IDefaultRequestContext>() {

                           @Override
                           public void handle(IDefaultRequestContext context) {
                           }
                       });
            fail();
        } catch(Exception ex) {
        }

        try {
            getRouter().dir("/one/*{param1}/one").fileSystem(getTestingWritableDir() + "/one")
                       .save(new IHandler<IDefaultRequestContext>() {

                           @Override
                           public void handle(IDefaultRequestContext context) {
                           }
                       });
            fail();
        } catch(Exception ex) {
        }
    }

    @Test
    public void dynamicFileDynamicParameters() throws Exception {

        getRouter().file("/one").fileSystem(getTestingWritableDir() + "/one").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void
                    handle(IDefaultRequestContext context) {
            }
        });

        try {
            getRouter().file("/one/${param1}").fileSystem(getTestingWritableDir() + "/one")
                       .save(new IHandler<IDefaultRequestContext>() {

                           @Override
                           public void
                                   handle(IDefaultRequestContext context) {
                           }
                       });
            fail();
        } catch(Exception ex) {
        }

        try {
            getRouter().file("/one/*{param1}").fileSystem(getTestingWritableDir() + "/one")
                       .save(new IHandler<IDefaultRequestContext>() {

                           @Override
                           public void handle(IDefaultRequestContext context) {
                           }
                       });
            fail();
        } catch(Exception ex) {
        }

        try {
            getRouter().file("/${param1}").fileSystem(getTestingWritableDir() + "/one")
                       .save(new IHandler<IDefaultRequestContext>() {

                           @Override
                           public void handle(IDefaultRequestContext context) {
                           }
                       });
            fail();
        } catch(Exception ex) {
        }

        try {
            getRouter().file("/*{path}").fileSystem(getTestingWritableDir() + "/one")
                       .save(new IHandler<IDefaultRequestContext>() {

                           @Override
                           public void handle(IDefaultRequestContext context) {
                           }
                       });
            fail();
        } catch(Exception ex) {
        }

        try {
            getRouter().file("/${path:a+}").fileSystem(getTestingWritableDir() + "/one")
                       .save(new IHandler<IDefaultRequestContext>() {

                           @Override
                           public void handle(IDefaultRequestContext context) {
                           }
                       });
            fail();
        } catch(Exception ex) {
        }

        try {
            getRouter().file("/${path:<N>}").fileSystem(getTestingWritableDir() + "/one")
                       .save(new IHandler<IDefaultRequestContext>() {

                           @Override
                           public void handle(IDefaultRequestContext context) {
                           }
                       });
            fail();
        } catch(Exception ex) {
        }
    }

    @Test
    public void lastOneAddedWins1() throws Exception {

        getRouter().file("/one").fileSystem("/someFile.txt").save(SpincastTestUtils.dummyRouteHandler);

        getRouter().file("/one").classpath("/someFile.txt").save();

        SpincastTestHttpResponse response = get("/one");

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("text/plain", response.getContentType());
        assertEquals("Le bœuf et l'éléphant!", response.getContent());
    }

    @Test
    public void lastOneAddedWins2() throws Exception {

        getRouter().file("/one").classpath("/someFile.txt").save();

        getRouter().file("/one").fileSystem("/someFile.txt").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                throw new NotFoundException("titi");
            }
        });

        SpincastTestHttpResponse response = get("/one");
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("titi", response.getContent());
    }

    @Test
    public void lastOneAddedWins3() throws Exception {

        getRouter().dir("/oneDir").classpath("/test").save();

        getRouter().file("/oneDir").fileSystem("/test").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                throw new NotFoundException("titi");
            }
        });

        SpincastTestHttpResponse response = get("/oneDir");
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("titi", response.getContent());
    }

    @Test
    public void lastOneAddedWins4() throws Exception {

        String filPath = "/" + UUID.randomUUID().toString();
        while(new File(filPath).exists()) {
            filPath = "/" + UUID.randomUUID().toString();
        }
        getRouter().file("/oneDir").fileSystem(filPath).save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                throw new NotFoundException("titi");
            }
        });

        getRouter().dir("/oneDir").classpath("/someFile.txt").save();

        SpincastTestHttpResponse response = get("/oneDir");
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("text/plain", response.getContentType());
        assertEquals("Le bœuf et l'éléphant!", response.getContent());
    }

}
