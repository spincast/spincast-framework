package org.spincast.website.services;

import java.util.List;

import org.spincast.website.models.INewsEntriesAndTotalNbr;
import org.spincast.website.models.INewsEntry;
import org.spincast.website.repositories.INewsRepository;

import com.google.inject.Inject;

/**
 * Spincast news service implementation.
 */
public class NewsService implements INewsService {

    private final INewsRepository newsRepository;

    @Inject
    public NewsService(INewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    protected INewsRepository getNewsRepository() {
        return this.newsRepository;
    }

    @Override
    public List<INewsEntry> getNewsEntries(boolean ascOrder) {
        return getNewsRepository().getNewsEntries(ascOrder);
    }

    @Override
    public INewsEntriesAndTotalNbr getNewsEntries(int startPos, int endPos, boolean ascOrder) {
        return getNewsRepository().getNewsEntries(startPos, endPos, ascOrder);
    }

    @Override
    public INewsEntry getNewsEntry(long newsId) {
        return getNewsRepository().getNewsEntry(newsId);
    }

}
