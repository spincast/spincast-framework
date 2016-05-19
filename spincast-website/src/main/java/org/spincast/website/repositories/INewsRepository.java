package org.spincast.website.repositories;

import java.util.List;

import org.spincast.website.models.INewsEntriesAndTotalNbr;
import org.spincast.website.models.INewsEntry;

/**
 * Repository for Spincast's news entry / blog entries.
 */
public interface INewsRepository {

    /**
     * Gets all the news entries.
     * 
     * @param ascOrder If <code>true</code>, returns the entries by their
     * publication date in ascending order. Otherwise, in descending
     * order.
     */
    public List<INewsEntry> getNewsEntries(boolean ascOrder);

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
     */
    public INewsEntriesAndTotalNbr getNewsEntries(int startPos, int endPos, boolean ascOrder);

    /**
     * Gets a specific news entry.
     * 
     * @return the news entry or <code>null</code> if not found.
     */
    public INewsEntry getNewsEntry(long newsId);

}
