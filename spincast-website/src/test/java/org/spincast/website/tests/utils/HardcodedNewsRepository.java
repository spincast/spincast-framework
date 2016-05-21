package org.spincast.website.tests.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.spincast.website.IAppConfig;
import org.spincast.website.models.INewsEntriesAndTotalNbr;
import org.spincast.website.models.INewsEntry;
import org.spincast.website.repositories.INewsRepository;

import com.google.common.collect.Lists;
import com.google.inject.Inject;

/**
 * Hardcoded implementation of the News repository.
 */
public abstract class HardcodedNewsRepository implements INewsRepository {

    private final IAppConfig appConfig;
    private List<INewsEntry> newsEntriesAsc;
    private List<INewsEntry> newsEntriesDesc;
    private Map<Long, INewsEntry> newsEntriesById;

    @Inject
    public HardcodedNewsRepository(IAppConfig appConfig) {
        this.appConfig = appConfig;
    }

    protected IAppConfig getAppConfig() {
        return this.appConfig;
    }

    @Override
    public List<INewsEntry> getNewsEntries(boolean ascOrder) {

        if(ascOrder) {
            if(this.newsEntriesAsc == null) {

                List<INewsEntry> entries = getNewsEntriesLocal();

                Collections.sort(entries, new Comparator<INewsEntry>() {

                    @Override
                    public int compare(INewsEntry entry1, INewsEntry entry2) {

                        return entry1.getPublishedDate().compareTo(entry2.getPublishedDate());
                    }
                });

                this.newsEntriesAsc = entries;
            }
            return this.newsEntriesAsc;

        } else {
            if(this.newsEntriesDesc == null) {
                List<INewsEntry> newsEntriesAsc = getNewsEntries(true);
                this.newsEntriesDesc = Lists.reverse(newsEntriesAsc);
            }
            return this.newsEntriesDesc;
        }
    }

    @Override
    public INewsEntriesAndTotalNbr getNewsEntriesAndTotalNbr(int startPos, int endPos, boolean ascOrder) {

        if(startPos < 1) {
            startPos = 1;
        }

        if(endPos < 1) {
            endPos = 1;
        }

        if(endPos < startPos) {
            endPos = startPos;
        }

        final List<INewsEntry> entries = getNewsEntries(ascOrder);

        if(entries.size() == 0 || startPos > entries.size()) {

            return new INewsEntriesAndTotalNbr() {

                @Override
                public List<INewsEntry> getNewsEntries() {
                    return new ArrayList<INewsEntry>();
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
        return new INewsEntriesAndTotalNbr() {

            @Override
            public List<INewsEntry> getNewsEntries() {
                return Collections.unmodifiableList(entries.subList(startPosFinal - 1, endPosFinal)); // endPos is exclusive here
            }

            @Override
            public int getNbrNewsEntriesTotal() {
                return entries.size();
            }
        };
    }

    @Override
    public INewsEntry getNewsEntry(long newsId) {
        return getNewsEntriesById().get(newsId);
    }

    protected Map<Long, INewsEntry> getNewsEntriesById() {

        if(this.newsEntriesById == null) {
            this.newsEntriesById = new HashMap<Long, INewsEntry>();
            List<INewsEntry> newsEntries = getNewsEntriesLocal();
            for(INewsEntry newsEntry : newsEntries) {
                this.newsEntriesById.put(newsEntry.getId(), newsEntry);
            }
        }

        return this.newsEntriesById;
    }

    protected abstract List<INewsEntry> getNewsEntriesLocal();

}
