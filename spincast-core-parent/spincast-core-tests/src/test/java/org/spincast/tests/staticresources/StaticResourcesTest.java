package org.spincast.tests.staticresources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.UUID;

import org.junit.Test;
import org.spincast.core.exceptions.NotFoundException;
import org.spincast.core.exchange.DefaultRequestContext;
import org.spincast.core.routing.Handler;
import org.spincast.core.routing.StaticResourceCacheConfig;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.defaults.tests.SpincastDefaultNoAppIntegrationTestBase;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.plugins.routing.SpincastRouterConfig;
import org.spincast.shaded.org.apache.commons.io.FileUtils;
import org.spincast.shaded.org.apache.commons.lang3.StringUtils;
import org.spincast.shaded.org.apache.http.HttpHeaders;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.testing.core.utils.SpincastTestUtils;

import com.google.inject.Inject;

public class StaticResourcesTest extends SpincastDefaultNoAppIntegrationTestBase {

    @Inject
    protected SpincastRouterConfig spincastRouterConfig;

    protected SpincastRouterConfig getSpincastRouterConfig() {
        return this.spincastRouterConfig;
    }

    private String getDefaultCacheControlHeaderValue(boolean isDynamicResource) {

        StaticResourceCacheConfig config = getSpincastConfig().getDefaultStaticResourceCacheConfig(isDynamicResource);

        StringBuilder builder = new StringBuilder();
        if(config.isCachePrivate()) {
            builder.append("private");
        } else {
            builder.append("public");
        }
        builder.append(", max-age=").append(config.getCacheSeconds());

        Integer cdnSeconds = config.getCacheSecondsCdn();
        if(cdnSeconds != null) {
            if(cdnSeconds < 0) {
                cdnSeconds = 0;
            }
            builder.append(", s-maxage=").append(cdnSeconds);
        }
        return builder.toString();
    }

    @Test
    public void fileNotSaved() throws Exception {

        getRouter().file("/one").classpath("/someFile.txt");

        HttpResponse response = GET("/one").send();

        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());
    }

    @Test
    public void fileClasspathSlash() throws Exception {

        getRouter().file("/one").classpath("/someFile.txt").save();

        HttpResponse response = GET("/one").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("text/plain", response.getContentType());
        assertEquals("Le bœuf et l'éléphant!", response.getContentAsString());
    }

    @Test
    public void fileClasspathTextPlain() throws Exception {

        getRouter().file("/file").classpath("someFile.txt").save();

        HttpResponse response = GET("/file").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("text/plain", response.getContentType());
        assertEquals("Le bœuf et l'éléphant!", response.getContentAsString());
    }

    @Test
    public void fileClasspathImage() throws Exception {

        getRouter().file("/one/two/three").classpath("/image.jpg").save();

        HttpResponse response = GET("/one/two/three").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("image/jpeg", response.getContentType());
    }

    @Test
    public void fileDynParam() throws Exception {

        try {
            getRouter().file("/${file}").classpath("/someFile.txt").save();
            fail();
        } catch(Exception ex) {
        }
    }

    @Test
    public void fileSplatParam() throws Exception {

        try {
            getRouter().file("/*{file}").classpath("/someFile.txt").save();
            fail();
        } catch(Exception ex) {
        }
    }

    @Test
    public void dirClasspath() throws Exception {

        getRouter().dir("/one/two/three").classpath("/oneDir").save();

        HttpResponse response = GET("/one/two/three/file2.txt").send();
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

        HttpResponse response = GET("/dir/file2.txt").send();
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

        getRouter().GET("/route1").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("route1");
            }
        });

        getRouter().GET("/dir/routes2").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("routes2");
            }
        });

        getRouter().dir("/dir").classpath("/oneDir").save();

        HttpResponse response = GET("/route1").send();
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

        getRouter().GET("/").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("route1");
            }
        });

        getRouter().GET("/routes2").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("routes2");
            }
        });

        getRouter().GET("/route3").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("route3");
            }
        });

        getRouter().file("/").classpath("/oneDir/file2.txt").save();

        HttpResponse response = GET("/").send();
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
    public void fileFileSystemAbsolute() throws Exception {

        File fileTarget = SpincastTestUtils.generateTempClassFile(getTestingWritableDir());
        assertTrue(fileTarget.isFile());

        getRouter().file("/file").pathAbsolute(fileTarget.getAbsolutePath()).save();

        HttpResponse response = GET("/file").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("application/java", response.getContentType());
    }

    @Test
    public void dirFileSystemAbsolute() throws Exception {

        File dir = new File(getTestingWritableDir() + "/dirFileSystem");
        assertTrue(dir.mkdirs());

        File fileTemp = SpincastTestUtils.generateTempClassFile(getTestingWritableDir());
        assertTrue(fileTemp.isFile());

        File fileTarget = new File(dir.getAbsolutePath() + "/" + fileTemp.getName());
        FileUtils.copyFile(fileTemp, fileTarget);
        assertTrue(fileTarget.isFile());

        getRouter().dir("/dir").pathAbsolute(dir.getAbsolutePath()).save();

        HttpResponse response = GET("/dir/").send();
        assertEquals(HttpStatus.SC_FORBIDDEN, response.getStatus());

        response = GET("/dir/" + fileTarget.getName()).send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("application/java", response.getContentType());
    }

    @Test
    public void fileFileSystemRelative() throws Exception {

        String fileRelativePath = "/testing/" + UUID.randomUUID().toString() + ".html";
        File file = new File(getSpincastConfig().getSpincastWritableDir(), fileRelativePath);
        FileUtils.writeStringToFile(file, "<h1>hi</h1>", "UTF-8");
        try {
            getRouter().file("/file").pathRelative(fileRelativePath).save();

            HttpResponse response = GET("/file").send();
            assertEquals(HttpStatus.SC_OK, response.getStatus());
            assertEquals("text/html", response.getContentType());
            assertEquals("<h1>hi</h1>", response.getContentAsString());
        } finally {
            FileUtils.deleteQuietly(file);
        }
    }

    @Test
    public void dirFileSystemRelative() throws Exception {

        File dir = null;
        File file = null;
        try {

            String dirRelativePath = "/testing/" + UUID.randomUUID().toString();
            dir = new File(getSpincastConfig().getSpincastWritableDir(), dirRelativePath);
            assertTrue(dir.mkdirs());

            String fileName = UUID.randomUUID().toString() + ".html";
            file = new File(dir.getAbsolutePath() + "/" + fileName);
            FileUtils.writeStringToFile(file, "<h1>hi</h1>", "UTF-8");

            getRouter().dir("/dir").pathRelative(dirRelativePath).save();

            HttpResponse response = GET("/dir/").send();
            assertEquals(HttpStatus.SC_FORBIDDEN, response.getStatus());

            response = GET("/dir/" + fileName).send();
            assertEquals(HttpStatus.SC_OK, response.getStatus());
            assertEquals("text/html", response.getContentType());
            assertEquals("<h1>hi</h1>", response.getContentAsString());

        } finally {
            FileUtils.deleteQuietly(dir);
            FileUtils.deleteQuietly(file);
        }
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
        }
    }

    @Test
    public void lastPathWins() throws Exception {

        getRouter().file("/one/two/three")
                   .classpath("/nope")
                   .pathAbsolute("/nope")
                   .classpath("/image.jpg")
                   .save();

        HttpResponse response = GET("/one/two/three").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("image/jpeg", response.getContentType());
    }

    @Test
    public void valid() throws Exception {

        getRouter().dir("/one").classpath("/oneDir").save();

        HttpResponse response = GET("/one/image2.jpg").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("image/jpeg", response.getContentType());
        assertFalse(StringUtils.isBlank(response.getContentAsString()));
    }

    @Test
    public void valid2() throws Exception {

        getRouter().dir("/one").classpath("/oneDir").save();

        HttpResponse response = GET("/one/dir2/image3.jpg").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("image/jpeg", response.getContentType());
        assertFalse(StringUtils.isBlank(response.getContentAsString()));
    }

    @Test
    public void valid3() throws Exception {

        getRouter().dir("/one").classpath("/oneDir").save();

        HttpResponse response = GET("/one/dir2/dir3/image4.jpg").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("image/jpeg", response.getContentType());
        assertFalse(StringUtils.isBlank(response.getContentAsString()));
    }

    @Test
    public void validButNotFound() throws Exception {

        getRouter().dir("/one").classpath("/oneDir").save();

        HttpResponse response = GET("/one/dir2/dir3/nope.jpg").send();
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());
    }

    @Test
    public void dirSplatClasspathValid() throws Exception {

        getRouter().dir("/one/*{remaining}").classpath("/oneDir").save();

        HttpResponse response = GET("/one/image2.jpg").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("image/jpeg", response.getContentType());
        assertFalse(StringUtils.isBlank(response.getContentAsString()));
    }

    @Test
    public void dirSplatClasspathValid2() throws Exception {

        getRouter().dir("/one/*{remaining}").classpath("/oneDir").save();

        HttpResponse response = GET("/one/dir2/image3.jpg").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("image/jpeg", response.getContentType());
        assertFalse(StringUtils.isBlank(response.getContentAsString()));
    }

    @Test
    public void dirSplatClasspathValid3() throws Exception {

        getRouter().dir("/one/*{remaining}").classpath("/oneDir").save();

        HttpResponse response = GET("/one/dir2/dir3/image4.jpg").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("image/jpeg", response.getContentType());
        assertFalse(StringUtils.isBlank(response.getContentAsString()));
    }

    @Test
    public void dirSplatAbsolutePathValid() throws Exception {

        File file = new File(getTestingWritableDir(), "dirSplatAbsolutePathValid.html");
        FileUtils.writeStringToFile(file, "test", "UTF-8");

        getRouter().dir("/one/*{remaining}").pathAbsolute(getTestingWritableDir().getAbsolutePath()).save();

        HttpResponse response = GET("/one/dirSplatAbsolutePathValid.html").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("text/html", response.getContentType());
        assertEquals("test", response.getContentAsString());
    }

    @Test
    public void dirSplatAbsolutePathNotFound() throws Exception {

        File file = new File(getTestingWritableDir(), "dirSplatAbsolutePathValid.html");
        FileUtils.writeStringToFile(file, "test", "UTF-8");

        getRouter().dir("/one/*{remaining}").pathAbsolute(getTestingWritableDir().getAbsolutePath()).save();

        HttpResponse response = GET("/one/dirSplatAbsolutePathValid.nope").send();
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());
    }

    @Test
    public void dirSplatRelativePathValid() throws Exception {

        File file = new File(getSpincastConfig().getSpincastWritableDir() + "/dirSplatRelativePathValid/test.html");
        FileUtils.writeStringToFile(file, "test", "UTF-8");
        try {
            getRouter().dir("/one/*{remaining}").pathRelative("/dirSplatRelativePathValid").save();

            HttpResponse response = GET("/one/test.html").send();
            assertEquals(HttpStatus.SC_OK, response.getStatus());
            assertEquals("text/html", response.getContentType());
            assertEquals("test", response.getContentAsString());
        } finally {
            FileUtils.deleteQuietly(file.getParentFile());
        }
    }

    @Test
    public void dirSplatRelativePathNotFound() throws Exception {

        File file = new File(getSpincastConfig().getSpincastWritableDir() + "/dirSplatRelativePathValid/test.html");
        FileUtils.writeStringToFile(file, "test", "UTF-8");
        try {
            getRouter().dir("/one/*{remaining}").pathRelative("/dirSplatRelativePathValid").save();

            HttpResponse response = GET("/one/nope.html").send();
            assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());
        } finally {
            FileUtils.deleteQuietly(file.getParentFile());
        }
    }

    @Test
    public void splatValidButNotFound() throws Exception {

        getRouter().dir("/one/*{remaining}").classpath("/oneDir").save();

        HttpResponse response = GET("/one/dir2/dir3/nope.jpg").send();
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());
    }

    @Test
    public void dirDynamicParametersInvalid() throws Exception {

        getRouter().dir("/one").pathAbsolute(getTestingWritableDir() + "/").save();

        try {
            getRouter().dir("/${param1}").pathAbsolute(getTestingWritableDir() + "/").save();
            fail();
        } catch(Exception ex) {
        }

        try {
            getRouter().dir("/${param1}/one").pathAbsolute(getTestingWritableDir() + "/").save();
            fail();
        } catch(Exception ex) {
        }

        try {
            getRouter().dir("/one/${param1}").pathAbsolute(getTestingWritableDir() + "/").save();
            fail();
        } catch(Exception ex) {
        }

        try {
            getRouter().dir("/${path:a+}").pathAbsolute(getTestingWritableDir() + "/").save();
            fail();
        } catch(Exception ex) {
        }

        try {
            getRouter().dir("/${path:<N>}").pathAbsolute(getTestingWritableDir() + "/").save();
            fail();
        } catch(Exception ex) {
        }

        try {
            getRouter().dir("/*{param1}/one").pathAbsolute(getTestingWritableDir() + "/").save();
            fail();
        } catch(Exception ex) {
        }

        try {
            getRouter().dir("/one/*{param1}/one").pathAbsolute(getTestingWritableDir() + "/").save();
            fail();
        } catch(Exception ex) {
        }

        try {
            getRouter().dir("/*{splat}/${param1}").pathAbsolute(getTestingWritableDir() + "/").save();
            fail();
        } catch(Exception ex) {
        }
    }

    @Test
    public void lastOneAddedWins1() throws Exception {

        getRouter().file("/one").pathAbsolute("/someFile.txt").save(SpincastTestUtils.dummyRouteHandler);

        getRouter().file("/one").classpath("/someFile.txt").save();

        HttpResponse response = GET("/one").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("text/plain", response.getContentType());
        assertEquals("Le bœuf et l'éléphant!", response.getContentAsString());
    }

    @Test
    public void lastOneAddedWins2() throws Exception {

        getRouter().file("/one").classpath("/someFile.txt").save();

        getRouter().file("/one").pathAbsolute("/someFile.txt").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                throw new NotFoundException("titi");
            }
        });

        HttpResponse response = GET("/one").send();
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
        getRouter().file("/oneDir").pathAbsolute(filPath).save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                throw new NotFoundException("titi");
            }
        });

        getRouter().dir("/oneDir").classpath("/oneDir").save();

        HttpResponse response = GET("/oneDir/file2.txt").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("text/plain", response.getContentType());
        assertEquals("file content 2", response.getContentAsString());
    }

    @Test
    public void fileFromFileSystemDoesntExist() throws Exception {

        String filPath = "/" + UUID.randomUUID().toString();
        while(new File(filPath).exists()) {
            filPath = "/" + UUID.randomUUID().toString();
        }

        try {
            getRouter().file("/test").pathAbsolute(filPath).save();
            fail();
        } catch(Exception ex) {
        }
    }

    @Test
    public void dirFromFileSystemDoesntExist() throws Exception {

        String filPath = "/" + UUID.randomUUID().toString();
        while(new File(filPath).exists()) {
            filPath = "/" + UUID.randomUUID().toString();
        }

        try {
            getRouter().dir("/test").pathAbsolute(filPath).save();
            fail();
        } catch(Exception ex) {
        }
    }

    @Test
    public void fileFromClasspathDoesntExist() throws Exception {

        try {
            getRouter().file("/test").classpath("/nope.txt").save();
            fail();
        } catch(Exception ex) {
        }
    }

    @Test
    public void dirFromClasspathDoesntExist() throws Exception {
        try {
            getRouter().dir("/oneDir").classpath("/nope").save();
            fail();
        } catch(Exception ex) {
            System.out.println();
        }
    }

    @Test
    public void cacheDefault() throws Exception {

        getRouter().file("/one").classpath("/someFile.txt").save();

        HttpResponse response = GET("/one").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("text/plain", response.getContentType());
        assertEquals("Le bœuf et l'éléphant!", response.getContentAsString());

        String cacheControl = response.getHeaderFirst(HttpHeaders.CACHE_CONTROL);
        assertNotNull(cacheControl);
        assertEquals(getDefaultCacheControlHeaderValue(false), cacheControl);

        String expires = response.getHeaderFirst(HttpHeaders.EXPIRES);
        assertNotNull(expires);

        String pragma = response.getHeaderFirst(HttpHeaders.PRAGMA);
        assertNull(pragma);
    }

    @Test
    public void cacheSeconds() throws Exception {

        getRouter().file("/one").cache(123).classpath("/someFile.txt").save();

        HttpResponse response = GET("/one").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("text/plain", response.getContentType());
        assertEquals("Le bœuf et l'éléphant!", response.getContentAsString());

        String cacheControl = response.getHeaderFirst(HttpHeaders.CACHE_CONTROL);
        assertNotNull(cacheControl);
        assertEquals("public, max-age=123", cacheControl);

        String expires = response.getHeaderFirst(HttpHeaders.EXPIRES);
        assertNotNull(expires);

        String pragma = response.getHeaderFirst(HttpHeaders.PRAGMA);
        assertNull(pragma);
    }

    @Test
    public void cacheSecondsPrivate() throws Exception {

        getRouter().file("/one").cache(123, true).classpath("/someFile.txt").save();

        HttpResponse response = GET("/one").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("text/plain", response.getContentType());
        assertEquals("Le bœuf et l'éléphant!", response.getContentAsString());

        String cacheControl = response.getHeaderFirst(HttpHeaders.CACHE_CONTROL);
        assertNotNull(cacheControl);
        assertEquals("private, max-age=123", cacheControl);

        String expires = response.getHeaderFirst(HttpHeaders.EXPIRES);
        assertNotNull(expires);

        String pragma = response.getHeaderFirst(HttpHeaders.PRAGMA);
        assertNull(pragma);
    }

    @Test
    public void cacheSecondsPrivateSecondsCdn() throws Exception {

        getRouter().file("/one").cache(123, true, 456).classpath("/someFile.txt").save();

        HttpResponse response = GET("/one").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("text/plain", response.getContentType());
        assertEquals("Le bœuf et l'éléphant!", response.getContentAsString());

        String cacheControl = response.getHeaderFirst(HttpHeaders.CACHE_CONTROL);
        assertNotNull(cacheControl);
        assertEquals("private, max-age=123, s-maxage=456", cacheControl);

        String expires = response.getHeaderFirst(HttpHeaders.EXPIRES);
        assertNotNull(expires);

        String pragma = response.getHeaderFirst(HttpHeaders.PRAGMA);
        assertNull(pragma);
    }

    @Test
    public void cacheDefaultWithGenerator() throws Exception {

        File file = new File(getTestingWritableDir() + "/" + UUID.randomUUID().toString() + ".txt");
        getRouter().file("/one").pathAbsolute(file.getAbsolutePath()).save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("Le bœuf et l'éléphant!");
            }
        });

        // First request: resource generated
        HttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("Le bœuf et l'éléphant!", response.getContentAsString());

        String cacheControl = response.getHeaderFirst(HttpHeaders.CACHE_CONTROL);
        assertNotNull(cacheControl);
        assertEquals(getDefaultCacheControlHeaderValue(true), cacheControl);

        String expires = response.getHeaderFirst(HttpHeaders.EXPIRES);
        assertNotNull(expires);

        String pragma = response.getHeaderFirst(HttpHeaders.PRAGMA);
        assertNull(pragma);

        // Second request: static resource served
        response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("text/plain", response.getContentType());
        assertEquals("Le bœuf et l'éléphant!", response.getContentAsString());

        cacheControl = response.getHeaderFirst(HttpHeaders.CACHE_CONTROL);
        assertNotNull(cacheControl);
        assertEquals(getDefaultCacheControlHeaderValue(true), cacheControl);

        expires = response.getHeaderFirst(HttpHeaders.EXPIRES);
        assertNotNull(expires);

        pragma = response.getHeaderFirst(HttpHeaders.PRAGMA);
        assertNull(pragma);

    }

    @Test
    public void cacheSecondsWithGenerator() throws Exception {

        File file = new File(getTestingWritableDir() + "/" + UUID.randomUUID().toString() + ".txt");
        getRouter().file("/one").cache(123).pathAbsolute(file.getAbsolutePath()).save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("Le bœuf et l'éléphant!");
            }
        });

        // First request: resource generated
        HttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("Le bœuf et l'éléphant!", response.getContentAsString());

        String cacheControl = response.getHeaderFirst(HttpHeaders.CACHE_CONTROL);
        assertNotNull(cacheControl);
        assertEquals("public, max-age=123", cacheControl);

        String expires = response.getHeaderFirst(HttpHeaders.EXPIRES);
        assertNotNull(expires);

        String pragma = response.getHeaderFirst(HttpHeaders.PRAGMA);
        assertNull(pragma);

        // Second request: static resource served
        response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("text/plain", response.getContentType());
        assertEquals("Le bœuf et l'éléphant!", response.getContentAsString());

        cacheControl = response.getHeaderFirst(HttpHeaders.CACHE_CONTROL);
        assertNotNull(cacheControl);
        assertEquals("public, max-age=123", cacheControl);

        expires = response.getHeaderFirst(HttpHeaders.EXPIRES);
        assertNotNull(expires);

        pragma = response.getHeaderFirst(HttpHeaders.PRAGMA);
        assertNull(pragma);
    }

    @Test
    public void cacheSecondsPrivateWithGenerator() throws Exception {

        File file = new File(getTestingWritableDir() + "/" + UUID.randomUUID().toString() + ".txt");
        getRouter().file("/one").cache(123, true).pathAbsolute(file.getAbsolutePath())
                   .save(new Handler<DefaultRequestContext>() {

                       @Override
                       public void handle(DefaultRequestContext context) {
                           context.response().sendPlainText("Le bœuf et l'éléphant!");
                       }
                   });

        // First request: resource generated
        HttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("Le bœuf et l'éléphant!", response.getContentAsString());

        String cacheControl = response.getHeaderFirst(HttpHeaders.CACHE_CONTROL);
        assertNotNull(cacheControl);
        assertEquals("private, max-age=123", cacheControl);

        String expires = response.getHeaderFirst(HttpHeaders.EXPIRES);
        assertNotNull(expires);

        String pragma = response.getHeaderFirst(HttpHeaders.PRAGMA);
        assertNull(pragma);

        // Second request: static resource served
        response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("text/plain", response.getContentType());
        assertEquals("Le bœuf et l'éléphant!", response.getContentAsString());

        cacheControl = response.getHeaderFirst(HttpHeaders.CACHE_CONTROL);
        assertNotNull(cacheControl);
        assertEquals("private, max-age=123", cacheControl);

        expires = response.getHeaderFirst(HttpHeaders.EXPIRES);
        assertNotNull(expires);

        pragma = response.getHeaderFirst(HttpHeaders.PRAGMA);
        assertNull(pragma);
    }

    @Test
    public void cacheSecondsPrivateSecondsCdnWithGenerator() throws Exception {

        File file = new File(getTestingWritableDir() + "/" + UUID.randomUUID().toString() + ".txt");
        getRouter().file("/one").cache(123, true, 456).pathAbsolute(file.getAbsolutePath())
                   .save(new Handler<DefaultRequestContext>() {

                       @Override
                       public void handle(DefaultRequestContext context) {
                           context.response().sendPlainText("Le bœuf et l'éléphant!");
                       }
                   });

        // First request: resource generated
        HttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("Le bœuf et l'éléphant!", response.getContentAsString());

        String cacheControl = response.getHeaderFirst(HttpHeaders.CACHE_CONTROL);
        assertNotNull(cacheControl);
        assertEquals("private, max-age=123, s-maxage=456", cacheControl);

        String expires = response.getHeaderFirst(HttpHeaders.EXPIRES);
        assertNotNull(expires);

        String pragma = response.getHeaderFirst(HttpHeaders.PRAGMA);
        assertNull(pragma);

        // Second request: static resource served
        response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("text/plain", response.getContentType());
        assertEquals("Le bœuf et l'éléphant!", response.getContentAsString());

        cacheControl = response.getHeaderFirst(HttpHeaders.CACHE_CONTROL);
        assertNotNull(cacheControl);
        assertEquals("private, max-age=123, s-maxage=456", cacheControl);

        expires = response.getHeaderFirst(HttpHeaders.EXPIRES);
        assertNotNull(expires);

        pragma = response.getHeaderFirst(HttpHeaders.PRAGMA);
        assertNull(pragma);
    }

    @Test
    public void cacheDefaultDir() throws Exception {

        getRouter().dir("/one").classpath("/oneDir").save();

        HttpResponse response = GET("/one/file2.txt").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("text/plain", response.getContentType());
        assertEquals("file content 2", response.getContentAsString());

        String cacheControl = response.getHeaderFirst(HttpHeaders.CACHE_CONTROL);
        assertNotNull(cacheControl);
        assertEquals(getDefaultCacheControlHeaderValue(false), cacheControl);

        String expires = response.getHeaderFirst(HttpHeaders.EXPIRES);
        assertNotNull(expires);

        String pragma = response.getHeaderFirst(HttpHeaders.PRAGMA);
        assertNull(pragma);
    }

    @Test
    public void cacheSecondsDir() throws Exception {

        getRouter().dir("/one").cache(123).classpath("/oneDir").save();

        HttpResponse response = GET("/one/file2.txt").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("text/plain", response.getContentType());
        assertEquals("file content 2", response.getContentAsString());

        String cacheControl = response.getHeaderFirst(HttpHeaders.CACHE_CONTROL);
        assertNotNull(cacheControl);
        assertEquals("public, max-age=123", cacheControl);

        String expires = response.getHeaderFirst(HttpHeaders.EXPIRES);
        assertNotNull(expires);

        String pragma = response.getHeaderFirst(HttpHeaders.PRAGMA);
        assertNull(pragma);
    }

    @Test
    public void cacheSecondsPrivateDir() throws Exception {

        getRouter().dir("/one").cache(123, true).classpath("/oneDir").save();

        HttpResponse response = GET("/one/file2.txt").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("text/plain", response.getContentType());
        assertEquals("file content 2", response.getContentAsString());

        String cacheControl = response.getHeaderFirst(HttpHeaders.CACHE_CONTROL);
        assertNotNull(cacheControl);
        assertEquals("private, max-age=123", cacheControl);

        String expires = response.getHeaderFirst(HttpHeaders.EXPIRES);
        assertNotNull(expires);

        String pragma = response.getHeaderFirst(HttpHeaders.PRAGMA);
        assertNull(pragma);
    }

    @Test
    public void cacheSecondsPrivateSecondsCdnDir() throws Exception {

        getRouter().dir("/one").cache(123, true, 456).classpath("/oneDir").save();

        HttpResponse response = GET("/one/file2.txt").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("text/plain", response.getContentType());
        assertEquals("file content 2", response.getContentAsString());

        String cacheControl = response.getHeaderFirst(HttpHeaders.CACHE_CONTROL);
        assertNotNull(cacheControl);
        assertEquals("private, max-age=123, s-maxage=456", cacheControl);

        String expires = response.getHeaderFirst(HttpHeaders.EXPIRES);
        assertNotNull(expires);

        String pragma = response.getHeaderFirst(HttpHeaders.PRAGMA);
        assertNull(pragma);
    }

    @Test
    public void cacheDefaultWithGeneratorDir() throws Exception {

        File dir = new File(getTestingWritableDir() + "/" + UUID.randomUUID().toString());
        getRouter().dir("/one/*{resourcePath}").pathAbsolute(dir.getAbsolutePath()).save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("Le bœuf et l'éléphant!");
            }
        });

        // First request: resource generated
        HttpResponse response = GET("/one/titi.txt").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("Le bœuf et l'éléphant!", response.getContentAsString());

        String cacheControl = response.getHeaderFirst(HttpHeaders.CACHE_CONTROL);
        assertNotNull(cacheControl);
        assertEquals(getDefaultCacheControlHeaderValue(true), cacheControl);

        String expires = response.getHeaderFirst(HttpHeaders.EXPIRES);
        assertNotNull(expires);

        String pragma = response.getHeaderFirst(HttpHeaders.PRAGMA);
        assertNull(pragma);

        // Second request: static resource served
        response = GET("/one/titi.txt").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("text/plain", response.getContentType());
        assertEquals("Le bœuf et l'éléphant!", response.getContentAsString());

        cacheControl = response.getHeaderFirst(HttpHeaders.CACHE_CONTROL);
        assertNotNull(cacheControl);
        assertEquals(getDefaultCacheControlHeaderValue(true), cacheControl);

        expires = response.getHeaderFirst(HttpHeaders.EXPIRES);
        assertNotNull(expires);

        pragma = response.getHeaderFirst(HttpHeaders.PRAGMA);
        assertNull(pragma);
    }

    @Test
    public void cacheSecondsWithGeneratorDir() throws Exception {

        File dir = new File(getTestingWritableDir() + "/" + UUID.randomUUID().toString());
        getRouter().dir("/one/*{resourcePath}").cache(123).pathAbsolute(dir.getAbsolutePath())
                   .save(new Handler<DefaultRequestContext>() {

                       @Override
                       public void handle(DefaultRequestContext context) {
                           context.response().sendPlainText("Le bœuf et l'éléphant!");
                       }
                   });

        // First request: resource generated
        HttpResponse response = GET("/one/titi.txt").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("Le bœuf et l'éléphant!", response.getContentAsString());

        String cacheControl = response.getHeaderFirst(HttpHeaders.CACHE_CONTROL);
        assertNotNull(cacheControl);
        assertEquals("public, max-age=123", cacheControl);

        String expires = response.getHeaderFirst(HttpHeaders.EXPIRES);
        assertNotNull(expires);

        String pragma = response.getHeaderFirst(HttpHeaders.PRAGMA);
        assertNull(pragma);

        // Second request: static resource served
        response = GET("/one/titi.txt").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("text/plain", response.getContentType());
        assertEquals("Le bœuf et l'éléphant!", response.getContentAsString());

        cacheControl = response.getHeaderFirst(HttpHeaders.CACHE_CONTROL);
        assertNotNull(cacheControl);
        assertEquals("public, max-age=123", cacheControl);

        expires = response.getHeaderFirst(HttpHeaders.EXPIRES);
        assertNotNull(expires);

        pragma = response.getHeaderFirst(HttpHeaders.PRAGMA);
        assertNull(pragma);
    }

    @Test
    public void cacheSecondsPrivateWithGeneratorDir() throws Exception {

        File dir = new File(getTestingWritableDir() + "/" + UUID.randomUUID().toString());
        getRouter().dir("/one/*{resourcePath}").cache(123, true).pathAbsolute(dir.getAbsolutePath())
                   .save(new Handler<DefaultRequestContext>() {

                       @Override
                       public void handle(DefaultRequestContext context) {
                           context.response().sendPlainText("Le bœuf et l'éléphant!");
                       }
                   });

        // First request: resource generated
        HttpResponse response = GET("/one/titi.txt").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("Le bœuf et l'éléphant!", response.getContentAsString());

        String cacheControl = response.getHeaderFirst(HttpHeaders.CACHE_CONTROL);
        assertNotNull(cacheControl);
        assertEquals("private, max-age=123", cacheControl);

        String expires = response.getHeaderFirst(HttpHeaders.EXPIRES);
        assertNotNull(expires);

        String pragma = response.getHeaderFirst(HttpHeaders.PRAGMA);
        assertNull(pragma);

        // Second request: static resource served
        response = GET("/one/titi.txt").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("text/plain", response.getContentType());
        assertEquals("Le bœuf et l'éléphant!", response.getContentAsString());

        cacheControl = response.getHeaderFirst(HttpHeaders.CACHE_CONTROL);
        assertNotNull(cacheControl);
        assertEquals("private, max-age=123", cacheControl);

        expires = response.getHeaderFirst(HttpHeaders.EXPIRES);
        assertNotNull(expires);

        pragma = response.getHeaderFirst(HttpHeaders.PRAGMA);
        assertNull(pragma);
    }

    @Test
    public void cacheSecondsPrivateSecondsCdnWithGeneratorDir() throws Exception {

        File dir = new File(getTestingWritableDir() + "/" + UUID.randomUUID().toString());
        getRouter().dir("/one/*{resourcePath}").cache(123, true, 456).pathAbsolute(dir.getAbsolutePath())
                   .save(new Handler<DefaultRequestContext>() {

                       @Override
                       public void handle(DefaultRequestContext context) {
                           context.response().sendPlainText("Le bœuf et l'éléphant!");
                       }
                   });

        // First request: resource generated
        HttpResponse response = GET("/one/titi.txt").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("Le bœuf et l'éléphant!", response.getContentAsString());

        String cacheControl = response.getHeaderFirst(HttpHeaders.CACHE_CONTROL);
        assertNotNull(cacheControl);
        assertEquals("private, max-age=123, s-maxage=456", cacheControl);

        String expires = response.getHeaderFirst(HttpHeaders.EXPIRES);
        assertNotNull(expires);

        String pragma = response.getHeaderFirst(HttpHeaders.PRAGMA);
        assertNull(pragma);

        // Second request: static resource served
        response = GET("/one/titi.txt").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("text/plain", response.getContentType());
        assertEquals("Le bœuf et l'éléphant!", response.getContentAsString());

        cacheControl = response.getHeaderFirst(HttpHeaders.CACHE_CONTROL);
        assertNotNull(cacheControl);
        assertEquals("private, max-age=123, s-maxage=456", cacheControl);

        expires = response.getHeaderFirst(HttpHeaders.EXPIRES);
        assertNotNull(expires);

        pragma = response.getHeaderFirst(HttpHeaders.PRAGMA);
        assertNull(pragma);
    }

    @Test
    public void exactPathWins() throws Exception {

        getRouter().dir("/one").cache(123).classpath("/oneDir").save();
        getRouter().file("/one/file2.txt").cache(456).classpath("/oneDir/file2.txt").save();

        HttpResponse response = GET("/one/file2.txt").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("text/plain", response.getContentType());
        assertEquals("file content 2", response.getContentAsString());

        String cacheControl = response.getHeaderFirst(HttpHeaders.CACHE_CONTROL);
        assertNotNull(cacheControl);
        assertEquals("public, max-age=456", cacheControl);

        String expires = response.getHeaderFirst(HttpHeaders.EXPIRES);
        assertNotNull(expires);

        String pragma = response.getHeaderFirst(HttpHeaders.PRAGMA);
        assertNull(pragma);
    }

    @Test
    public void exactPathWins2() throws Exception {

        getRouter().file("/one/file2.txt").cache(456).classpath("/oneDir/file2.txt").save();
        getRouter().dir("/one").cache(123).classpath("/oneDir").save();

        HttpResponse response = GET("/one/file2.txt").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("text/plain", response.getContentType());
        assertEquals("file content 2", response.getContentAsString());

        String cacheControl = response.getHeaderFirst(HttpHeaders.CACHE_CONTROL);
        assertNotNull(cacheControl);
        assertEquals("public, max-age=456", cacheControl);

        String expires = response.getHeaderFirst(HttpHeaders.EXPIRES);
        assertNotNull(expires);

        String pragma = response.getHeaderFirst(HttpHeaders.PRAGMA);
        assertNull(pragma);
    }

    @Test
    public void exactPathWins3() throws Exception {

        getRouter().dir("/one").cache(123).classpath("/oneDir").save();

        File file = new File(getTestingWritableDir() + "/" + UUID.randomUUID().toString() + ".txt");
        getRouter().file("/one/titi.txt").cache(123).pathAbsolute(file.getAbsolutePath())
                   .save(new Handler<DefaultRequestContext>() {

                       @Override
                       public void handle(DefaultRequestContext context) {
                           context.response().sendPlainText("Le bœuf et l'éléphant!");
                       }
                   });

        // First request: resource generated
        HttpResponse response = GET("/one/titi.txt").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("Le bœuf et l'éléphant!", response.getContentAsString());

        String cacheControl = response.getHeaderFirst(HttpHeaders.CACHE_CONTROL);
        assertNotNull(cacheControl);
        assertEquals("public, max-age=123", cacheControl);

        String expires = response.getHeaderFirst(HttpHeaders.EXPIRES);
        assertNotNull(expires);

        String pragma = response.getHeaderFirst(HttpHeaders.PRAGMA);
        assertNull(pragma);

        // Second request: static resource served
        response = GET("/one/titi.txt").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("text/plain", response.getContentType());
        assertEquals("Le bœuf et l'éléphant!", response.getContentAsString());

        cacheControl = response.getHeaderFirst(HttpHeaders.CACHE_CONTROL);
        assertNotNull(cacheControl);
        assertEquals("public, max-age=123", cacheControl);

        expires = response.getHeaderFirst(HttpHeaders.EXPIRES);
        assertNotNull(expires);

        pragma = response.getHeaderFirst(HttpHeaders.PRAGMA);
        assertNull(pragma);
    }

    @Test
    public void dirWithIndex() throws Exception {

        getRouter().dir("/dir").classpath("/dirWithIndex").save();

        HttpResponse response = GET("/dir").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("text/html", response.getContentType());
        assertEquals("<h1>Hello!</h1>", response.getContentAsString());
    }

    @Test
    public void dirWithIndex2() throws Exception {

        getRouter().dir("/dir").classpath("/dirWithIndex").save();

        HttpResponse response = GET("/dir/").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("text/html", response.getContentType());
        assertEquals("<h1>Hello!</h1>", response.getContentAsString());
    }

    @Test
    public void dirNoIndex() throws Exception {

        getRouter().dir("/dir").classpath("/oneDir").save();

        HttpResponse response = GET("/dir/").send();
        assertEquals(HttpStatus.SC_FORBIDDEN, response.getStatus());
    }

}
