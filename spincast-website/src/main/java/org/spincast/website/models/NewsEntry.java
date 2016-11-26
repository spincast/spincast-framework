package org.spincast.website.models;

import java.util.Date;

/**
 * Spincast news entry.
 */
public interface NewsEntry {

    /**
     * The news entry's id
     */
    public long getId();

    /**
     * The published date
     */
    public Date getPublishedDate();

    /**
     * The title of the news entry.
     */
    public String getTitle();

    /**
     * The description of the news entry.
     */
    public String getDescription();

}
