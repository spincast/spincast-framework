package org.spincast.website.models;

import java.util.List;

/**
 * A set of news entries and the number total of entries
 * in the repository.
 */
public interface INewsEntriesAndTotalNbr {

    public int getNbrNewsEntriesTotal();

    public List<INewsEntry> getNewsEntries();

}
