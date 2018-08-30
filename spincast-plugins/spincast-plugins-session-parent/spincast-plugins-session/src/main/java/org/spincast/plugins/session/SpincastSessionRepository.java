package org.spincast.plugins.session;

/**
 * The repository to CRUD the sessions.
 */
public interface SpincastSessionRepository {

    /**
     * Save a session.
     */
    public void saveSession(SpincastSession session);

    /**
     * Gets a session from the database/data source.
     * 
     * @return the session or <code>null</code> if not found.
     */
    public SpincastSession getSession(String sessionId);

    /**
     * Deletes a saved session.
     */
    public void deleteSession(String sessionId);

    /**
     * Deletes the deletes that were inactive for too long.
     * This means their last "modification date" must be older than
     * the specified number of mminutes.
     */
    public void deleteOldInactiveSession(int sessionMaxInactiveMinutes);
}
