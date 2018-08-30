package org.spincast.plugins.session;

import java.time.Instant;

import org.spincast.core.json.JsonObject;

/**
 * The object representing the Session of a visitor.
 */
public interface SpincastSession {

    /**
     * The unique id of this session.
     */
    public String getId();

    /**
     * Returns <code>true</code> if the session
     * has been created in the current request.
     */
    public boolean isNew();

    /**
     * Returns a mutable {@link JsonObject} representing the
     * attributes of the session. You can use this object
     * to get/retrieve/delete attributes.
     */
    public JsonObject getAttributes();

    /**
     * The Instant at which the session was created.
     */
    public Instant getCreationDate();

    /**
     * The Instant at which the session was modified
     * for the last time.
     */
    public Instant getModificationDate();

    /**
     * Was the session modified since it was loaded?
     */
    public boolean isDirty();

    /**
     * This allows you to flag a session as being
     * dirty, even if its attributes don't change.
     * This will force the session to be saved in
     * the database.
     */
    public void setDirty();

    /**
     * Sets the session as "not valid anymore".
     */
    public void invalidate();

    /**
     * Has the session been invalidated?
     */
    public boolean isInvalidated();

}
