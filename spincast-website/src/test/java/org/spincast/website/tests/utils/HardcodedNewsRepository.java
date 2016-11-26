package org.spincast.website.tests.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.spincast.website.AppConfig;
import org.spincast.website.models.NewsEntriesAndTotalNbr;
import org.spincast.website.models.NewsEntry;
import org.spincast.website.repositories.NewsRepository;

import com.google.common.collect.Lists;
import com.google.inject.Inject;

/**
 * Hardcoded implementation of the News repository.
 */
public abstract class HardcodedNewsRepository implements NewsRepository {

    private final AppConfig appConfig;
    private List<NewsEntry> newsEntriesAsc;
    private List<NewsEntry> newsEntriesDesc;
    private Map<Long, NewsEntry> newsEntriesById;

    @Inject
    public HardcodedNewsRepository(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    protected AppConfig getAppConfig() {
        return this.appConfig;
    }

    @Override
    public List<NewsEntry> getNewsEntries(boolean ascOrder) {

        if(ascOrder) {
            if(this.newsEntriesAsc == null) {

                List<NewsEntry> entries = getNewsEntriesLocal();

                Collections.sort(entries, new Comparator<NewsEntry>() {

                    @Override
                    public int compare(NewsEntry entry1, NewsEntry entry2) {

                        return entry1.getPublishedDate().compareTo(entry2.getPublishedDate());
                    }
                });

                this.newsEntriesAsc = entries;
            }
            return this.newsEntriesAsc;

        } else {
            if(this.newsEntriesDesc == null) {
                List<NewsEntry> newsEntriesAsc = getNewsEntries(true);
                this.newsEntriesDesc = Lists.reverse(newsEntriesAsc);
            }
            return this.newsEntriesDesc;
        }
    }

    @Override
    public NewsEntriesAndTotalNbr getNewsEntriesAndTotalNbr(int startPos, int endPos, boolean ascOrder) {

        if(startPos < 1) {
            startPos = 1;
        }

        if(endPos < 1) {
            endPos = 1;
        }

        if(endPos < startPos) {
            endPos = startPos;
        }

        final List<NewsEntry> entries = getNewsEntries(ascOrder);

        if(entries.size() == 0 || startPos > entries.size()) {

            return new NewsEntriesAndTotalNbr() {

                @Override
                public List<NewsEntry> getNewsEntries() {
                    return new ArrayList<NewsEntry>();
                }

                @Override
                public int getNbrNewsEntriesTotal() {
                    return entries.size();
                }
            };
        }

        if(endPos > entries.size()) {
            endPos = entries.size();
        }

        final int startPosFinal = startPos;
        final int endPosFinal = endPos;
        return new NewsEntriesAndTotalNbr() {

            @Override
            public List<NewsEntry> getNewsEntries() {
                return Collections.unmodifiableList(entries.subList(startPosFinal - 1, endPosFinal)); // endPos is exclusive here
            }

            @Override
            public int getNbrNewsEntriesTotal() {
                return entries.size();
            }
        };
    }

    @Override
    public NewsEntry getNewsEntry(long newsId) {
        return getNewsEntriesById().get(newsId);
    }

    protected Map<Long, NewsEntry> getNewsEntriesById() {

        if(this.newsEntriesById == null) {
            this.newsEntriesById = new HashMap<Long, NewsEntry>();
            List<NewsEntry> newsEntries = getNewsEntriesLocal();
            for(NewsEntry newsEntry : newsEntries) {
                this.newsEntriesById.put(newsEntry.getId(), newsEntry);
            }
        }

        return this.newsEntriesById;
    }

    protected abstract List<NewsEntry> getNewsEntriesLocal();

}
