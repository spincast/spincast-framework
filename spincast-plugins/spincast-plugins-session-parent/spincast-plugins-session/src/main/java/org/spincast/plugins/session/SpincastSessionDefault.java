package org.spincast.plugins.session;

import java.time.Instant;

import javax.annotation.Nullable;

import org.spincast.core.json.JsonManager;
import org.spincast.core.json.JsonObject;
import org.spincast.shaded.org.apache.commons.codec.digest.DigestUtils;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

public class SpincastSessionDefault implements SpincastSession {

    private final JsonManager jsonManager;
    private final SpincastSessionManager spincastSessionManager;

    private String sessionId;
    private final boolean isNew;
    private final Instant creationDate;
    private final Instant modificationDate;
    private final JsonObject attributes;
    private final String attributesInitialHash;
    private boolean dirtyForced = false;
    private boolean hasBeenInvalidated = false;

    /**
     * Constructor for a *new* session.
     */
    @AssistedInject
    public SpincastSessionDefault(JsonManager jsonManager,
                                  SpincastSessionManager spincastSessionManager) {
        this(null,
             Instant.now(),
             Instant.now(),
             null,
             false,
             true,
             jsonManager,
             spincastSessionManager);
    }

    @AssistedInject
    public SpincastSessionDefault(@Assisted("sessionId") String sessionId,
                                  @Assisted("creationDate") Instant creationDate,
                                  @Assisted("modificationDate") Instant modificationDate,
                                  @Assisted("attributes") @Nullable JsonObject attributes,
                                  JsonManager jsonManager,
                                  SpincastSessionManager spincastSessionManager) {
        this(sessionId, creationDate, modificationDate, attributes, false, false, jsonManager, spincastSessionManager);
    }

    @AssistedInject
    public SpincastSessionDefault(@Assisted("sessionId") String sessionId,
                                  @Assisted("creationDate") Instant creationDate,
                                  @Assisted("modificationDate") Instant modificationDate,
                                  @Assisted("attributes") @Nullable JsonObject attributes,
                                  @Assisted("setAsDirty") boolean setAsDirty,
                                  JsonManager jsonManager,
                                  SpincastSessionManager spincastSessionManager) {
        this(sessionId, creationDate, modificationDate, attributes, setAsDirty, false, jsonManager, spincastSessionManager);
    }

    public SpincastSessionDefault(String sessionId,
                                  Instant creationDate,
                                  Instant modificationDate,
                                  JsonObject attributes,
                                  boolean setAsDirty,
                                  boolean isNew,
                                  JsonManager jsonManager,
                                  SpincastSessionManager spincastSessionManager) {
        this.jsonManager = jsonManager;
        this.spincastSessionManager = spincastSessionManager;

        this.sessionId = sessionId;
        this.isNew = isNew;
        this.creationDate = creationDate;
        this.modificationDate = modificationDate;

        if (attributes == null) {
            attributes = jsonManager.create();
        }
        this.attributes = attributes;
        this.attributesInitialHash = getAttributesHash();

        if (setAsDirty) {
            this.dirtyForced = true;
        }
    }

    protected JsonManager getJsonManager() {
        return this.jsonManager;
    }

    protected SpincastSessionManager getSpincastSessionManager() {
        return this.spincastSessionManager;
    }

    protected String getAttributesHash() {
        return DigestUtils.md5Hex(this.attributes.toJsonString());
    }

    protected String getAttributesInitialHash() {
        return this.attributesInitialHash;
    }

    protected boolean isForcedAsDirty() {
        return this.dirtyForced;
    }

    @Override
    public String getId() {

        if (this.sessionId == null) {
            this.sessionId = getSpincastSessionManager().generateNewSessionId();
        }
        return this.sessionId;
    }

    @Override
    public boolean isNew() {
        return this.isNew;
    }

    @Override
    public JsonObject getAttributes() {
        return this.attributes;
    }

    @Override
    public Instant getCreationDate() {
        return this.creationDate;
    }

    @Override
    public Instant getModificationDate() {
        return this.modificationDate;
    }

    @Override
    public boolean isDirty() {

        if (isForcedAsDirty()) {
            return true;
        }

        String initialHash = getAttributesInitialHash();
        String currentHash = getAttributesHash();

        return !initialHash.equals(currentHash);
    }

    @Override
    public void setDirty() {
        this.dirtyForced = true;
    }

    @Override
    public void invalidate() {
        this.hasBeenInvalidated = true;
    }

    @Override
    public boolean isInvalidated() {
        return this.hasBeenInvalidated;
    }

}
