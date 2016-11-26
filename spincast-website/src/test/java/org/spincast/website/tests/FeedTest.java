package org.spincast.website.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.junit.Test;
import org.spincast.core.exchange.RequestContext;
import org.spincast.core.json.JsonObject;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.core.websocket.WebsocketContext;
import org.spincast.core.xml.XmlManager;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.shaded.org.apache.commons.lang3.StringUtils;
import org.spincast.shaded.org.apache.commons.lang3.time.FastDateFormat;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.website.AppConfig;
import org.spincast.website.models.NewsEntry;
import org.spincast.website.models.NewsEntryDefault;
import org.spincast.website.repositories.NewsRepository;
import org.spincast.website.tests.utils.HardcodedNewsRepository;

import com.google.common.collect.Lists;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.Scopes;
import com.google.inject.util.Modules;

public class FeedTest extends WebsiteIntegrationTestBase {

    @Inject
    private XmlManager xmlManager;

    protected XmlManager getXmlManager() {
        return this.xmlManager;
    }

    /**
     * Test repository
     */
    protected static class TestNewsRepository extends HardcodedNewsRepository {

        @Inject
        public TestNewsRepository(AppConfig appConfig) {
            super(appConfig);
        }

        @Override
        protected List<NewsEntry> getNewsEntriesLocal() {

            try {
                FastDateFormat feedDateFormatter =
                        FastDateFormat.getInstance("yyyy-MM-dd HH:mm", TimeZone.getTimeZone("UTC"));

                Date date = feedDateFormatter.parse("2000-01-02 19:00");

                return Lists.newArrayList(new NewsEntryDefault(123,
                                                               date,
                                                               "my title",
                                                               "<p>my description</p>"));
            } catch(Exception ex) {
                throw SpincastStatics.runtimize(ex);
            }

        }

        @Override
        public int getNewsEntriesTotalNumber() {
            return getNewsEntriesLocal().size();
        }

        @Override
        public List<NewsEntry> getNewsEntries(int startPos, int endPos, boolean ascOrder) {
            return getNewsEntriesLocal().subList(startPos - 1, endPos);
        }
    }

    /**
     * Overriding Guice module
     */
    @Override
    protected Module getTestOverridingModule(Class<? extends RequestContext<?>> requestContextType,
                                             Class<? extends WebsocketContext<?>> websocketContextType) {

        Module baseModule = super.getDefaultOverridingModule(requestContextType, websocketContextType);

        Module localModule = new AbstractModule() {

            @Override
            protected void configure() {

                //==========================================
                // Overrides the News repository with our test one.
                //==========================================
                bind(NewsRepository.class).to(TestNewsRepository.class).in(Scopes.SINGLETON);
            }
        };

        if(baseModule == null) {
            return localModule;
        } else {
            return Modules.combine(baseModule, localModule);
        }
    }

    @Test
    public void rssFeed() throws Exception {

        HttpResponse response = GET("/rss").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.XML.getMainVariationWithUtf8Charset(), response.getContentType());
        assertNotNull(response.getContentAsString());

        String contentAsString = response.getContentAsString();
        contentAsString = StringUtils.replaceChars(contentAsString, "\r\n", "");

        assertTrue(contentAsString.startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"));
        assertTrue(contentAsString.endsWith("</rss>"));

        JsonObject xmlAsJsonObj = getXmlManager().fromXml(contentAsString);
        assertNotNull(xmlAsJsonObj);

        JsonObject channelObj = xmlAsJsonObj.getJsonObject("channel");
        assertNotNull(channelObj);

        String title = channelObj.getString("title");
        assertNotNull(title);
        assertEquals("Spincast Framework", title);

        String link = channelObj.getString("link");
        assertNotNull(link);
        assertEquals("http://localhost:" + getSpincastConfig().getHttpServerPort(), link);

        String description = channelObj.getString("description");
        assertNotNull(description);
        assertEquals("What's new about Spincast Framework?", description);

        JsonObject imageObj = channelObj.getJsonObject("image");
        assertNotNull(imageObj);

        title = imageObj.getString("title");
        assertNotNull(title);
        assertEquals("Spincast Framework", title);

        String url = imageObj.getString("url");
        assertNotNull(url);
        assertEquals("http://localhost:" + getSpincastConfig().getHttpServerPort() + "/public/images/feed.png", url);

        JsonObject itemObj = channelObj.getJsonObject("item");
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
