package org.spincast.website.models;

/**
 * Spincast news entry implementation.
 */
public class NewsEntry implements INewsEntry {

    private final String publishedDateISO;

    private final String title;

    private final String description;

    /**
     * Constructor
     */
    public NewsEntry(String publishedDateISO, String title, String description) {
        this.publishedDateISO = publishedDateISO;
        this.title = title;
        this.description = description;
    }

    @Override
    public String getPublishedDateISO() {
        return this.publishedDateISO;
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
        return getPublishedDateISO() + " - " + getTitle();
    }

}
