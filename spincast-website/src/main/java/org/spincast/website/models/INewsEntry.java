package org.spincast.website.models;

/**
 * Spincast news entry.
 */
public interface INewsEntry {

    /**
     * The published date, ISO format : "YYYY-MM-DD"
     */
    public String getPublishedDateISO();

    /**
     * The title of the news entry.
     */
    public String getTitle();

    /**
     * The description of the news entry.
     */
    public String getDescription();

}
