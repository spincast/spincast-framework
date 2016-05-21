package org.spincast.website.services;

import java.util.List;

import org.spincast.website.IAppConfig;
import org.spincast.website.models.INewsEntriesAndTotalNbr;
import org.spincast.website.models.INewsEntry;
import org.spincast.website.repositories.INewsRepository;

import com.google.inject.Inject;

/**
 * Spincast news service implementation.
 */
public class NewsService implements INewsService {

    private final INewsRepository newsRepository;
    private final IAppConfig appConfig;

    /**
     * Constructor
     */
    @Inject
    public NewsService(INewsRepository newsRepository,
                       IAppConfig appConfig) {
        this.newsRepository = newsRepository;
        this.appConfig = appConfig;
    }

    protected INewsRepository getNewsRepository() {
        return this.newsRepository;
    }

    protected IAppConfig getAppConfig() {
        return this.appConfig;
    }

    @Override
    public List<INewsEntry> getNewsEntries(boolean ascOrder) {
        return getNewsRepository().getNewsEntries(ascOrder);
    }

    @Override
    public INewsEntriesAndTotalNbr getNewsEntries(int startPos, int endPos, boolean ascOrder) {
        return getNewsRepository().getNewsEntriesAndTotalNbr(startPos, endPos, ascOrder);
    }

    @Override
    public INewsEntry getNewsEntry(long newsId) {
        return getNewsRepository().getNewsEntry(newsId);
    }

    @Override
    public List<INewsEntry> getFeedNewsEntries() {

        int newsEntriesTotalNumber = getNewsRepository().getNewsEntriesTotalNumber();

        int nbrNewsEntriesPerFeedRequest = getAppConfig().getNbrNewsEntriesPerFeedRequest();
        int startPos = newsEntriesTotalNumber - nbrNewsEntriesPerFeedRequest + 1;
        if(startPos < 1) {
            startPos = 1;
        }

        return getNewsRepository().getNewsEntries(startPos, newsEntriesTotalNumber, true);
    }

}
