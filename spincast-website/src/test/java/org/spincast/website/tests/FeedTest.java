package org.spincast.website.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.json.IJsonObject;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.core.xml.IXmlManager;
import org.spincast.plugins.httpclient.IHttpResponse;
import org.spincast.shaded.org.apache.commons.lang3.StringUtils;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.website.exchange.AppRequestContext;
import org.spincast.website.models.INewsEntry;
import org.spincast.website.models.NewsEntry;

import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;

public class FeedTest extends AppIntegrationTestBase {

    @Inject
    private IXmlManager xmlManager;

    protected IXmlManager getXmlManager() {
        return this.xmlManager;
    }

    @Override
    protected Module getOverridingModule() {

        return new SpincastGuiceModuleBase() {

            @Override
            protected void configure() {

                //==========================================
                // Bind one news entry only.
                //==========================================
                List<INewsEntry> newsEntries = new ArrayList<INewsEntry>();
                newsEntries.add(new NewsEntry("2000-01-02 19:00",
                                              "my title",
                                              "<p>my description</p>"));

                bind(new TypeLiteral<List<INewsEntry>>() {}).toInstance(newsEntries);
            }

            @Override
            protected Type getRequestContextType() {
                return AppRequestContext.class;
            }
        };
    }

    @Test
    public void rssFeed() throws Exception {

        IHttpResponse response = GET("/rss").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.XML.getMainVariationWithUtf8Charset(), response.getContentType());
        assertNotNull(response.getContentAsString());

        String contentAsString = response.getContentAsString();
        contentAsString = StringUtils.replaceChars(contentAsString, "\r\n", "");

        assertTrue(contentAsString.startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?><rss xmlns:dc=\"http://purl.org/dc/elements/1.1/\" version=\"2.0\">"));
        assertTrue(contentAsString.endsWith("</rss>"));

        IJsonObject xmlAsJsonObj = getXmlManager().fromXml(contentAsString);
        assertNotNull(xmlAsJsonObj);

        IJsonObject channelObj = xmlAsJsonObj.getJsonObject("channel");
        assertNotNull(channelObj);

        String title = channelObj.getString("title");
        assertNotNull(title);
        assertEquals("Spincast Framework", title);

        String link = channelObj.getString("link");
        assertNotNull(link);
        assertEquals("http://localhost:" + getSpincastConfig().getHttpServerPort(), link);

        String description = channelObj.getString("description");
        assertNotNull(description);
        assertEquals("What's new about the Spincast framework?", description);

        IJsonObject imageObj = channelObj.getJsonObject("image");
        assertNotNull(imageObj);

        title = imageObj.getString("title");
        assertNotNull(title);
        assertEquals("Spincast Framework", title);

        String url = imageObj.getString("url");
        assertNotNull(url);
        assertEquals("http://localhost:" + getSpincastConfig().getHttpServerPort() + "/public/images/feed.png", url);

        IJsonObject itemObj = channelObj.getJsonObject("item");
        assertNotNull(itemObj);

        title = itemObj.getString("title");
        assertNotNull(title);
        assertEquals("my title", title);

        description = itemObj.getString("description");
        assertNotNull(description);
        assertEquals("<p>my description</p>", description);

        String pubDate = itemObj.getString("pubDate");
        assertNotNull(pubDate);
        assertEquals("Sun, 02 Jan 2000 19:00:00 GMT", pubDate);

        String dcDate = itemObj.getString("date");
        assertNotNull(dcDate);
        assertEquals("2000-01-02T19:00:00Z", dcDate);

    }

}
