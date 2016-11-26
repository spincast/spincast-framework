package org.spincast.website.services;

import java.util.List;

import org.spincast.website.models.NewsEntriesAndTotalNbr;
import org.spincast.website.models.NewsEntry;

/**
 * Spincast News service.
 */
public interface NewsService {

    /**
     * Gets all the news entries.
     * 
     * @param ascOrder If <code>true</code>, returns the entries by their
     * publication date in ascending order. Otherwise, in descending
     * order.
     */
    public List<NewsEntry> getNewsEntries(boolean ascOrder);

    /**
     * Gets news entries.
     * 
     * @param startPos The position of the first entry to return. The first element
     * is "1", not "0".
     * @param endPos The position of the last entry to return (inclusive).
     * 
     * @param ascOrder If <code>true</code>, returns the entries by their
     * publication date in ascending order. Otherwise, in descending
     * order.
     * 
     * @return the news entries list and the total number of entries in the 
     * repository.
     */
    public NewsEntriesAndTotalNbr getNewsEntries(int startPos, int endPos, boolean ascOrder);

    /**
     * Gets a specific news entry.
     * 
     * @return the news entry or <code>null</code> if not found.
     */
    public NewsEntry getNewsEntry(long newsId);

    /**
     * Gets the news entries for the RSS feed.
     */
    public List<NewsEntry> getFeedNewsEntries();

}
