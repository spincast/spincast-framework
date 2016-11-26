package org.spincast.website.services;

import java.util.List;

import org.spincast.website.AppConfig;
import org.spincast.website.models.NewsEntriesAndTotalNbr;
import org.spincast.website.models.NewsEntry;
import org.spincast.website.repositories.NewsRepository;

import com.google.inject.Inject;

/**
 * Spincast news service implementation.
 */
public class NewsServiceDefault implements NewsService {

    private final NewsRepository newsRepository;
    private final AppConfig appConfig;

    /**
     * Constructor
     */
    @Inject
    public NewsServiceDefault(NewsRepository newsRepository,
                              AppConfig appConfig) {
        this.newsRepository = newsRepository;
        this.appConfig = appConfig;
    }

    protected NewsRepository getNewsRepository() {
        return this.newsRepository;
    }

    protected AppConfig getAppConfig() {
        return this.appConfig;
    }

    @Override
    public List<NewsEntry> getNewsEntries(boolean ascOrder) {
        return getNewsRepository().getNewsEntries(ascOrder);
    }

    @Override
    public NewsEntriesAndTotalNbr getNewsEntries(int startPos, int endPos, boolean ascOrder) {
        return getNewsRepository().getNewsEntriesAndTotalNbr(startPos, endPos, ascOrder);
    }

    @Override
    public NewsEntry getNewsEntry(long newsId) {
        return getNewsRepository().getNewsEntry(newsId);
    }

    @Override
    public List<NewsEntry> getFeedNewsEntries() {

        int newsEntriesTotalNumber = getNewsRepository().getNewsEntriesTotalNumber();

        int nbrNewsEntriesPerFeedRequest = getAppConfig().getNbrNewsEntriesPerFeedRequest();
        int startPos = newsEntriesTotalNumber - nbrNewsEntriesPerFeedRequest + 1;
        if(startPos < 1) {
            startPos = 1;
        }

        return getNewsRepository().getNewsEntries(startPos, newsEntriesTotalNumber, true);
    }

}
