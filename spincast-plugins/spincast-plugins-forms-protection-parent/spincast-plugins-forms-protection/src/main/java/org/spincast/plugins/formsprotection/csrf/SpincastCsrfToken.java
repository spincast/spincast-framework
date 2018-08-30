package org.spincast.plugins.formsprotection.csrf;

import java.time.Instant;

public class SpincastCsrfToken {

    private final String id;
    private final Instant creationDate;

    public SpincastCsrfToken(String id, Instant creationDate) {
        this.id = id;
        this.creationDate = creationDate;
    }

    public String getId() {
        return this.id;
    }

    public Instant getCreationDate() {
        return this.creationDate;
    }

    @Override
    public String toString() {
        return getCreationDate().toString() + " - " + getId();
    }
}
