package org.spincast.website.models;

/**
 * Spincast news entry.
 */
public interface INewsEntry {

    /**
     * The news entry's id
     */
    public long getId();

    /**
     * The published date, format: "YYYY-MM-DD HH:mm"
     */
    public String getPublishedDate();

    /**
     * The title of the news entry.
     */
    public String getTitle();

    /**
     * The description of the news entry.
     */
    public String getDescription();

}
