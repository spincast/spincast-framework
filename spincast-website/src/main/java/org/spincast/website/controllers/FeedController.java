package org.spincast.website.controllers;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.shaded.org.apache.commons.lang3.time.FastDateFormat;
import org.spincast.website.IAppConfig;
import org.spincast.website.exchange.IAppRequestContext;
import org.spincast.website.models.INewsEntry;
import org.spincast.website.services.INewsService;

import com.google.inject.Inject;
import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndContentImpl;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndEntryImpl;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.feed.synd.SyndFeedImpl;
import com.rometools.rome.feed.synd.SyndImage;
import com.rometools.rome.feed.synd.SyndImageImpl;
import com.rometools.rome.io.SyndFeedOutput;

public class FeedController {

    protected final Logger logger = LoggerFactory.getLogger(FeedController.class);

    private final INewsService newsService;
    private final IAppConfig appConfig;
    private final FastDateFormat feedDateFormatter =
            FastDateFormat.getInstance("yyyy-MM-dd HH:mm", TimeZone.getTimeZone("UTC"));

    /**
     * Constructor
     */
            @Inject
    public FeedController(INewsService newsService,
                          IAppConfig appConfig) {
        this.newsService = newsService;
        this.appConfig = appConfig;
    }

    protected INewsService getNewsService() {
        return this.newsService;
    }

    protected IAppConfig getAppConfig() {
        return this.appConfig;
    }

    protected FastDateFormat getFeedDateFormatter() {
        return this.feedDateFormatter;
    }

    public void rss(IAppRequestContext context) {
        String feed = generateFeed("rss_2.0");
        context.response().sendCharacters(feed, ContentTypeDefaults.XML.getMainVariationWithUtf8Charset());
    }

    protected String generateFeed(String feedType) {

        try {
            SyndFeed feed = new SyndFeedImpl();
            feed.setFeedType(feedType);

            feed.setTitle("Spincast Framework");
            feed.setLink(getAppConfig().getServerSchemeHostPort());
            feed.setEncoding("UTF-8");

            SyndImage feedImage = new SyndImageImpl();
            feedImage.setUrl(getAppConfig().getServerSchemeHostPort() + "/public/images/feed.png");
            feedImage.setTitle("Spincast Framework");
            feed.setImage(feedImage);

            feed.setDescription("What's new about Spincast Framework?");

            List<SyndEntry> entries = new ArrayList<SyndEntry>();

            for(INewsEntry newsEntry : getNewsService().getFeedNewsEntries()) {
                SyndEntry entry = new SyndEntryImpl();
                entry.setTitle(newsEntry.getTitle());
                entry.setLink(getAppConfig().getServerSchemeHostPort() + "/news/" + newsEntry.getId());
                entry.setPublishedDate(getFeedDateFormatter().parse(newsEntry.getPublishedDate()));
                SyndContent description = new SyndContentImpl();
                description.setType("text/html");
                description.setValue(newsEntry.getDescription());
                entry.setDescription(description);
                entries.add(entry);
            }
            feed.setEntries(entries);

            StringWriter stringWriter = new StringWriter();

            SyndFeedOutput output = new SyndFeedOutput();
            output.output(feed, stringWriter);
            stringWriter.close();

            return stringWriter.toString();

        } catch(Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }
}
