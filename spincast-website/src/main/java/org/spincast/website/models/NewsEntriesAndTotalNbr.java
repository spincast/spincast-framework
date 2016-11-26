package org.spincast.website.models;

import java.util.List;

/**
 * A set of news entries and the number total of entries
 * in the repository.
 */
public interface NewsEntriesAndTotalNbr {

    public int getNbrNewsEntriesTotal();

    public List<NewsEntry> getNewsEntries();

}
