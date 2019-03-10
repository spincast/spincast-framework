package org.spincast.tests.staticresources;

import static org.junit.Assert.assertEquals;

import java.net.URI;

import org.junit.Test;
import org.spincast.core.routing.StaticResource;
import org.spincast.core.routing.hotlinking.HotlinkingManager;
import org.spincast.core.routing.hotlinking.HotlinkingManagerDefault;
import org.spincast.core.routing.hotlinking.HotlinkingStategy;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.testing.defaults.NoAppStartHttpServerTestingBase;

import com.google.common.net.HttpHeaders;

public class HotlinkingTest extends NoAppStartHttpServerTestingBase {

    @Test
    public void hotlinkingProtectedDefault() throws Exception {

        getRouter().file("/file")
                   .classpath("someFile.txt")
                   .hotlinkingProtected()
                   .handle();

        //==========================================
        // No origin and no referer
        //==========================================
        HttpResponse response = GET("/file").disableRedirectHandling().send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("text/plain", response.getContentType());
        assertEquals("Le bœuf et l'éléphant!", response.getContentAsString());

        //==========================================
        // Right origin 
        //==========================================
        response = GET("/file").addHeaderValue(HttpHeaders.ORIGIN, getSpincastConfig().getPublicUrlBase())
                               .disableRedirectHandling()
                               .send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("text/plain", response.getContentType());
        assertEquals("Le bœuf et l'éléphant!", response.getContentAsString());

        //==========================================
        // Right referer 
        //==========================================
        response = GET("/file").addHeaderValue(HttpHeaders.REFERER, getSpincastConfig().getPublicUrlBase())
                               .disableRedirectHandling()
                               .send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("text/plain", response.getContentType());
        assertEquals("Le bœuf et l'éléphant!", response.getContentAsString());

        //==========================================
        // Right referer and origin 
        //==========================================
        response = GET("/file").addHeaderValue(HttpHeaders.ORIGIN, getSpincastConfig().getPublicUrlBase())
                               .addHeaderValue(HttpHeaders.REFERER, getSpincastConfig().getPublicUrlBase())
                               .disableRedirectHandling()
                               .send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("text/plain", response.getContentType());
        assertEquals("Le bœuf et l'éléphant!", response.getContentAsString());

        //==========================================
        // Wrong origin
        //==========================================
        response = GET("/file").addHeaderValue(HttpHeaders.ORIGIN, "https://example.com")
                               .disableRedirectHandling()
                               .send();
        assertEquals(HttpStatus.SC_FORBIDDEN, response.getStatus());

        //==========================================
        // Wrong referer
        //==========================================
        response = GET("/file").addHeaderValue(HttpHeaders.REFERER, "https://example.com")
                               .disableRedirectHandling()
                               .send();
        assertEquals(HttpStatus.SC_FORBIDDEN, response.getStatus());

        //==========================================
        // Wrong origin and referer
        //==========================================
        response = GET("/file").addHeaderValue(HttpHeaders.ORIGIN, "https://example.com")
                               .addHeaderValue(HttpHeaders.REFERER, "https://example.com")
                               .disableRedirectHandling()
                               .send();
        assertEquals(HttpStatus.SC_FORBIDDEN, response.getStatus());
    }

    @Test
    public void hotlinkingProtectedCustom() throws Exception {

        HotlinkingManager manager = new HotlinkingManagerDefault(getSpincastConfig()) {

            @Override
            public HotlinkingStategy getHotlinkingStategy(Object serverExchange, URI resourceURI, StaticResource<?> resource) {
                return HotlinkingStategy.REDIRECT;
            }

            @Override
            public String getRedirectUrl(Object serverExchange, URI resourceURI, StaticResource<?> resource) {
                return "https://toto";
            }
        };

        getRouter().file("/file")
                   .classpath("someFile.txt")
                   .hotlinkingProtected(manager)
                   .handle();

        //==========================================
        // No origin and no referer
        //==========================================
        HttpResponse response = GET("/file").disableRedirectHandling().send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("text/plain", response.getContentType());
        assertEquals("Le bœuf et l'éléphant!", response.getContentAsString());

        //==========================================
        // Right origin 
        //==========================================
        response = GET("/file").addHeaderValue(HttpHeaders.ORIGIN, getSpincastConfig().getPublicUrlBase())
                               .disableRedirectHandling()
                               .send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("text/plain", response.getContentType());
        assertEquals("Le bœuf et l'éléphant!", response.getContentAsString());

        //==========================================
        // Right referer 
        //==========================================
        response = GET("/file").addHeaderValue(HttpHeaders.REFERER, getSpincastConfig().getPublicUrlBase())
                               .disableRedirectHandling()
                               .send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("text/plain", response.getContentType());
        assertEquals("Le bœuf et l'éléphant!", response.getContentAsString());

        //==========================================
        // Right referer and origin 
        //==========================================
        response = GET("/file").addHeaderValue(HttpHeaders.ORIGIN, getSpincastConfig().getPublicUrlBase())
                               .addHeaderValue(HttpHeaders.REFERER, getSpincastConfig().getPublicUrlBase())
                               .disableRedirectHandling()
                               .send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("text/plain", response.getContentType());
        assertEquals("Le bœuf et l'éléphant!", response.getContentAsString());

        //==========================================
        // Wrong origin
        //==========================================
        response = GET("/file").addHeaderValue(HttpHeaders.ORIGIN, "https://example.com")
                               .disableRedirectHandling()
                               .send();
        assertEquals(HttpStatus.SC_MOVED_TEMPORARILY, response.getStatus());
        assertEquals("https://toto", response.getHeaderFirst(HttpHeaders.LOCATION));

        //==========================================
        // Wrong referer
        //==========================================
        response = GET("/file").addHeaderValue(HttpHeaders.REFERER, "https://example.com")
                               .disableRedirectHandling()
                               .send();
        assertEquals(HttpStatus.SC_MOVED_TEMPORARILY, response.getStatus());
        assertEquals("https://toto", response.getHeaderFirst(HttpHeaders.LOCATION));

        //==========================================
        // Wrong origin and referer
        //==========================================
        response = GET("/file").addHeaderValue(HttpHeaders.ORIGIN, "https://example.com")
                               .addHeaderValue(HttpHeaders.REFERER, "https://example.com")
                               .disableRedirectHandling()
                               .send();
        assertEquals(HttpStatus.SC_MOVED_TEMPORARILY, response.getStatus());
        assertEquals("https://toto", response.getHeaderFirst(HttpHeaders.LOCATION));
    }

    @Test
    public void neverActivateHotliking() throws Exception {

        HotlinkingManager manager = new HotlinkingManagerDefault(getSpincastConfig()) {

            @Override
            public boolean mustHotlinkingProtect(Object serverExchange,
                                                 URI resourceUri,
                                                 String requestOriginHeader,
                                                 String requestRefererHeader,
                                                 StaticResource<?> resource) {
                return false;
            }

            @Override
            public HotlinkingStategy getHotlinkingStategy(Object serverExchange, URI resourceURI, StaticResource<?> resource) {
                return HotlinkingStategy.REDIRECT;
            }

            @Override
            public String getRedirectUrl(Object serverExchange, URI resourceURI, StaticResource<?> resource) {
                return "https://toto";
            }
        };

        getRouter().file("/file")
                   .classpath("someFile.txt")
                   .hotlinkingProtected(manager)
                   .handle();

        //==========================================
        // No origin and no referer
        //==========================================
        HttpResponse response = GET("/file").disableRedirectHandling().send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("text/plain", response.getContentType());
        assertEquals("Le bœuf et l'éléphant!", response.getContentAsString());

        //==========================================
        // Right origin 
        //==========================================
        response = GET("/file").addHeaderValue(HttpHeaders.ORIGIN, getSpincastConfig().getPublicUrlBase())
                               .disableRedirectHandling()
                               .send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("text/plain", response.getContentType());
        assertEquals("Le bœuf et l'éléphant!", response.getContentAsString());

        //==========================================
        // Right referer 
        //==========================================
        response = GET("/file").addHeaderValue(HttpHeaders.REFERER, getSpincastConfig().getPublicUrlBase())
                               .disableRedirectHandling()
                               .send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("text/plain", response.getContentType());
        assertEquals("Le bœuf et l'éléphant!", response.getContentAsString());

        //==========================================
        // Right referer and origin 
        //==========================================
        response = GET("/file").addHeaderValue(HttpHeaders.ORIGIN, getSpincastConfig().getPublicUrlBase())
                               .addHeaderValue(HttpHeaders.REFERER, getSpincastConfig().getPublicUrlBase())
                               .disableRedirectHandling()
                               .send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("text/plain", response.getContentType());
        assertEquals("Le bœuf et l'éléphant!", response.getContentAsString());

        //==========================================
        // Wrong origin
        //==========================================
        response = GET("/file").addHeaderValue(HttpHeaders.ORIGIN, "https://example.com")
                               .disableRedirectHandling()
                               .send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("text/plain", response.getContentType());
        assertEquals("Le bœuf et l'éléphant!", response.getContentAsString());

        //==========================================
        // Wrong referer
        //==========================================
        response = GET("/file").addHeaderValue(HttpHeaders.REFERER, "https://example.com")
                               .disableRedirectHandling()
                               .send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("text/plain", response.getContentType());
        assertEquals("Le bœuf et l'éléphant!", response.getContentAsString());

        //==========================================
        // Wrong origin and referer
        //==========================================
        response = GET("/file").addHeaderValue(HttpHeaders.ORIGIN, "https://example.com")
                               .addHeaderValue(HttpHeaders.REFERER, "https://example.com")
                               .disableRedirectHandling()
                               .send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("text/plain", response.getContentType());
        assertEquals("Le bœuf et l'éléphant!", response.getContentAsString());
    }

    @Test
    public void alwaysActivateHotliking() throws Exception {

        HotlinkingManager manager = new HotlinkingManagerDefault(getSpincastConfig()) {

            @Override
            public boolean mustHotlinkingProtect(Object serverExchange,
                                                 URI resourceUri,
                                                 String requestOriginHeader,
                                                 String requestRefererHeader,
                                                 StaticResource<?> resource) {
                return true;
            }

            @Override
            public HotlinkingStategy getHotlinkingStategy(Object serverExchange, URI resourceURI, StaticResource<?> resource) {
                return HotlinkingStategy.REDIRECT;
            }

            @Override
            public String getRedirectUrl(Object serverExchange, URI resourceURI, StaticResource<?> resource) {
                return "https://toto";
            }
        };

        getRouter().file("/file")
                   .classpath("someFile.txt")
                   .hotlinkingProtected(manager)
                   .handle();

        //==========================================
        // No origin and no referer
        //==========================================
        HttpResponse response = GET("/file").disableRedirectHandling().send();
        assertEquals(HttpStatus.SC_MOVED_TEMPORARILY, response.getStatus());
        assertEquals("https://toto", response.getHeaderFirst(HttpHeaders.LOCATION));
        ;

        //==========================================
        // Right origin 
        //==========================================
        response = GET("/file").addHeaderValue(HttpHeaders.ORIGIN, getSpincastConfig().getPublicUrlBase())
                               .disableRedirectHandling()
                               .send();
        assertEquals(HttpStatus.SC_MOVED_TEMPORARILY, response.getStatus());
        assertEquals("https://toto", response.getHeaderFirst(HttpHeaders.LOCATION));

        //==========================================
        // Right referer 
        //==========================================
        response = GET("/file").addHeaderValue(HttpHeaders.REFERER, getSpincastConfig().getPublicUrlBase())
                               .disableRedirectHandling()
                               .send();
        assertEquals(HttpStatus.SC_MOVED_TEMPORARILY, response.getStatus());
        assertEquals("https://toto", response.getHeaderFirst(HttpHeaders.LOCATION));

        //==========================================
        // Right referer and origin 
        //==========================================
        response = GET("/file").addHeaderValue(HttpHeaders.ORIGIN, getSpincastConfig().getPublicUrlBase())
                               .addHeaderValue(HttpHeaders.REFERER, getSpincastConfig().getPublicUrlBase())
                               .disableRedirectHandling()
                               .send();
        assertEquals(HttpStatus.SC_MOVED_TEMPORARILY, response.getStatus());
        assertEquals("https://toto", response.getHeaderFirst(HttpHeaders.LOCATION));

        //==========================================
        // Wrong origin
        //==========================================
        response = GET("/file").addHeaderValue(HttpHeaders.ORIGIN, "https://example.com")
                               .disableRedirectHandling()
                               .send();
        assertEquals(HttpStatus.SC_MOVED_TEMPORARILY, response.getStatus());
        assertEquals("https://toto", response.getHeaderFirst(HttpHeaders.LOCATION));

        //==========================================
        // Wrong referer
        //==========================================
        response = GET("/file").addHeaderValue(HttpHeaders.REFERER, "https://example.com")
                               .disableRedirectHandling()
                               .send();
        assertEquals(HttpStatus.SC_MOVED_TEMPORARILY, response.getStatus());
        assertEquals("https://toto", response.getHeaderFirst(HttpHeaders.LOCATION));

        //==========================================
        // Wrong origin and referer
        //==========================================
        response = GET("/file").addHeaderValue(HttpHeaders.ORIGIN, "https://example.com")
                               .addHeaderValue(HttpHeaders.REFERER, "https://example.com")
                               .disableRedirectHandling()
                               .send();
        assertEquals(HttpStatus.SC_MOVED_TEMPORARILY, response.getStatus());
        assertEquals("https://toto", response.getHeaderFirst(HttpHeaders.LOCATION));
    }


}
