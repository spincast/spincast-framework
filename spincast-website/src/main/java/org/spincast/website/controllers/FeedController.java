package org.spincast.website.controllers;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.website.AppConfig;
import org.spincast.website.exchange.AppRequestContext;
import org.spincast.website.models.NewsEntry;
import org.spincast.website.services.NewsService;

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

    private final NewsService newsService;
    private final AppConfig appConfig;

    /**
     * Constructor
     */
    @Inject
    public FeedController(NewsService newsService,
                          AppConfig appConfig) {
        this.newsService = newsService;
        this.appConfig = appConfig;
    }

    protected NewsService getNewsService() {
        return this.newsService;
    }

    protected AppConfig getAppConfig() {
        return this.appConfig;
    }

    public void rss(AppRequestContext context) {
        String feed = generateFeed("rss_2.0");
        context.response().sendCharacters(feed, ContentTypeDefaults.XML.getMainVariationWithUtf8Charset());
    }

    protected String generateFeed(String feedType) {

        try {
            SyndFeed feed = new SyndFeedImpl();
            feed.setFeedType(feedType);

            feed.setTitle("Spincast Framework");
            feed.setLink(getAppConfig().getPublicUrlBase());
            feed.setEncoding("UTF-8");

            SyndImage feedImage = new SyndImageImpl();
            feedImage.setUrl(getAppConfig().getPublicUrlBase() + "/public/images/feed.png");
            feedImage.setTitle("Spincast Framework");
            feed.setImage(feedImage);

            feed.setDescription("What's new about Spincast Framework?");

            List<SyndEntry> entries = new ArrayList<SyndEntry>();

            for (NewsEntry newsEntry : getNewsService().getFeedNewsEntries()) {
                SyndEntry entry = new SyndEntryImpl();
                entry.setTitle(newsEntry.getTitle());
                entry.setLink(getAppConfig().getPublicUrlBase() + "/news/" + newsEntry.getId());
                entry.setPublishedDate(newsEntry.getPublishedDate());
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

        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }
}
