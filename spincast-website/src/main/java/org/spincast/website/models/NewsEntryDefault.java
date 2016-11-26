package org.spincast.website.models;

import java.util.Date;

/**
 * Spincast news entry implementation.
 */
public class NewsEntryDefault implements NewsEntry {

    private final long id;
    private final Date publishedDate;
    private final String title;
    private final String description;

    /**
     * Constructor
     */
    public NewsEntryDefault(long id, Date publishedDate, String title, String description) {
        this.id = id;
        this.publishedDate = publishedDate;
        this.title = title;
        this.description = description;
    }

    @Override
    public long getId() {
        return this.id;
    }

    @Override
    public Date getPublishedDate() {
        return this.publishedDate;
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public String toString() {
        return getPublishedDate() + " - " + getTitle();
    }

}
