package org.spincast.plugins.session;

import java.time.Instant;

import org.spincast.core.json.JsonObject;

/**
 * Manager for {@link SpincastSession}.
 */
public interface SpincastSessionManager {

    /**
     * Generates a new session id.
     */
    public String generateNewSessionId();

    /**
     * Creates a new session.
     */
    public SpincastSession createNewSession();

    /**
     * Creates a session from infos, with existing
     * attributes if any (<code>null</code> otherwise)
     */
    public SpincastSession createSession(String sessionId,
                                         Instant creationDate,
                                         Instant modificationDate,
                                         JsonObject attributes);

    /**
     * Gets the session of the current user, from the
     * request context. If not in a request context,
     * returns <code>null</code>.
     */
    public SpincastSession getCurrentSession();

    /**
     * Gets a session from the database/data source.
     * 
     * @return the session or <code>null</code> if not found.
     */
    public SpincastSession getSavedSession(String sessionId);

    /**
     * Saves a session, but update its modification date first.
     * <p>
     * The session to save must have a session id.
     */
    public void updateModificationDateAndSaveSession(SpincastSession session);

    /**
     * Save session.
     */
    public void saveSession(SpincastSession session);

    /**
     * Deletes a saved session.
     */
    public void deleteSession(String sessionId);

    /**
     * Deletes the current session in the request
     * context, if any.
     */
    public void deleteCurrentSession();

    /**
     * Will save the session id to the user (by default using
     * a cookie). 
     * 
     * @param permanent if <code>true</code>, the session id will be kept
     * when the browser session expires.
     */
    public void saveSessionIdOnUser(String sessionId, boolean permanent);

    /**
     * Deletes the session id on the user. By default, will delete
     * the session id cookie.
     */
    public void deleteSessionIdOnUser();

    /**
     * Deletes the deletes that were inactive for too long.
     * This means their last "modification date" must be older than
     * the specified number of mminutes.
     */
    public void deleteOldInactiveSession(int sessionMaxInactiveMinutes);

}
