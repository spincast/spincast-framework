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
import org.spincast.plugins.httpclient.IHttpResponse;
import org.spincast.shaded.org.apache.commons.io.FileUtils;
import org.spincast.shaded.org.apache.commons.lang3.StringUtils;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.testing.core.utils.SpincastTestUtils;

public class StaticResourcesTest extends DefaultIntegrationTestingBase {

    @Test
    public void fileNotSaved() throws Exception {

        getRouter().file("/one").classpath("/someFile.txt");

        IHttpResponse response = GET("/one").send();

        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());
    }

    @Test
    public void fileClasspathSlash() throws Exception {

        getRouter().file("/one").classpath("/someFile.txt").save();

        IHttpResponse response = GET("/one").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("text/plain", response.getContentType());
        assertEquals("Le bœuf et l'éléphant!", response.getContentAsString());
    }

    @Test
    public void fileClasspathTextPlain() throws Exception {

        getRouter().file("/file").classpath("someFile.txt").save();

        IHttpResponse response = GET("/file").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("text/plain", response.getContentType());
        assertEquals("Le bœuf et l'éléphant!", response.getContentAsString());
    }

    @Test
    public void fileClasspathImage() throws Exception {

        getRouter().file("/one/two/three").classpath("/image.jpg").save();

        IHttpResponse response = GET("/one/two/three").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("image/jpeg", response.getContentType());
    }

    @Test
    public void dirClasspath() throws Exception {

        getRouter().dir("/one/two/three").classpath("/oneDir").save();

        IHttpResponse response = GET("/one/two/three/file2.txt").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("text/plain", response.getContentType());
        assertEquals("file content 2", response.getContentAsString());

        response = GET("/one/two/three/image2.jpg").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("image/jpeg", response.getContentType());

        response = GET("/one/two/three").send();
        assertEquals(HttpStatus.SC_FORBIDDEN, response.getStatus());

    }

    @Test
    public void dirNotFile() throws Exception {

        getRouter().dir("/dir").classpath("/oneDir").save();

        IHttpResponse response = GET("/dir/file2.txt").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("text/plain", response.getContentType());
        assertEquals("file content 2", response.getContentAsString());

        response = GET("/dir").send();
        assertEquals(HttpStatus.SC_FORBIDDEN, response.getStatus());

        response = GET("/dir/").send();
        assertEquals(HttpStatus.SC_FORBIDDEN, response.getStatus());

        response = GET("/dir/nope").send();
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

        IHttpResponse response = GET("/route1").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("route1", response.getContentAsString());

        response = GET("/dir/routes2").send();
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = GET("/dir/file2.txt").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("text/plain", response.getContentType());
        assertEquals("file content 2", response.getContentAsString());
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

        IHttpResponse response = GET("/").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("text/plain", response.getContentType());
        assertEquals("file content 2", response.getContentAsString());

        response = GET("/routes2").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("routes2", response.getContentAsString());

        response = GET("/route3").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("text/plain", response.getContentType());
        assertEquals("file content 2", response.getContentAsString());
    }

    @Test
    public void fileFileSystem() throws Exception {

        File fileTarget = SpincastTestUtils.generateTempClassFile(getTestingWritableDir());
        assertTrue(fileTarget.isFile());

        getRouter().file("/file").fileSystem(fileTarget.getAbsolutePath()).save();

        IHttpResponse response = GET("/file").send();
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

        IHttpResponse response = GET("/dir/").send();
        assertEquals(HttpStatus.SC_FORBIDDEN, response.getStatus());

        response = GET("/dir/" + fileTarget.getName()).send();
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

        IHttpResponse response = GET("/one/two/three").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("image/jpeg", response.getContentType());
    }

    @Test
    public void valid() throws Exception {

        getRouter().dir("/one").classpath("/oneDir").save();

        IHttpResponse response = GET("/one/image2.jpg").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("image/jpeg", response.getContentType());
        assertFalse(StringUtils.isBlank(response.getContentAsString()));
    }

    @Test
    public void valid2() throws Exception {

        getRouter().dir("/one").classpath("/oneDir").save();

        IHttpResponse response = GET("/one/dir2/image3.jpg").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("image/jpeg", response.getContentType());
        assertFalse(StringUtils.isBlank(response.getContentAsString()));
    }

    @Test
    public void valid3() throws Exception {

        getRouter().dir("/one").classpath("/oneDir").save();

        IHttpResponse response = GET("/one/dir2/dir3/image4.jpg").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("image/jpeg", response.getContentType());
        assertFalse(StringUtils.isBlank(response.getContentAsString()));
    }

    @Test
    public void validButNotFound() throws Exception {

        getRouter().dir("/one").classpath("/oneDir").save();

        IHttpResponse response = GET("/one/dir2/dir3/nope.jpg").send();
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());
    }

    @Test
    public void splatValid() throws Exception {

        getRouter().dir("/one/*{remaining}").classpath("/oneDir").save();

        IHttpResponse response = GET("/one/image2.jpg").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("image/jpeg", response.getContentType());
        assertFalse(StringUtils.isBlank(response.getContentAsString()));
    }

    @Test
    public void splatValid2() throws Exception {

        getRouter().dir("/one/*{remaining}").classpath("/oneDir").save();

        IHttpResponse response = GET("/one/dir2/image3.jpg").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("image/jpeg", response.getContentType());
        assertFalse(StringUtils.isBlank(response.getContentAsString()));
    }

    @Test
    public void splatValid3() throws Exception {

        getRouter().dir("/one/*{remaining}").classpath("/oneDir").save();

        IHttpResponse response = GET("/one/dir2/dir3/image4.jpg").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("image/jpeg", response.getContentType());
        assertFalse(StringUtils.isBlank(response.getContentAsString()));
    }

    @Test
    public void splatValidButNotFound() throws Exception {

        getRouter().dir("/one/*{remaining}").classpath("/oneDir").save();

        IHttpResponse response = GET("/one/dir2/dir3/nope.jpg").send();
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
            public void handle(IDefaultRequestContext context) {
            }
        });

        try {
            getRouter().file("/one/${param1}").fileSystem(getTestingWritableDir() + "/one")
                       .save(new IHandler<IDefaultRequestContext>() {

                           @Override
                           public void handle(IDefaultRequestContext context) {
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

        IHttpResponse response = GET("/one").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("text/plain", response.getContentType());
        assertEquals("Le bœuf et l'éléphant!", response.getContentAsString());
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

        IHttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("titi", response.getContentAsString());
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

        IHttpResponse response = GET("/oneDir").send();
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("titi", response.getContentAsString());
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

        IHttpResponse response = GET("/oneDir").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("text/plain", response.getContentType());
        assertEquals("Le bœuf et l'éléphant!", response.getContentAsString());
    }

}
