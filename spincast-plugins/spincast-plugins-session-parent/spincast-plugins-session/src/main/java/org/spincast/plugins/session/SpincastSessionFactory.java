package org.spincast.plugins.session;

import java.time.Instant;

import org.spincast.core.json.JsonObject;

import com.google.inject.assistedinject.Assisted;

public interface SpincastSessionFactory {

    /**
     * Creates a new session.
     * <p>
     * {@link SpincastSession#isNew()} will return
     * <code>true</code>.
     */
    public SpincastSession createNewSession();

    /**
     * Creates a session from saved infos.
     */
    public SpincastSession createSession(@Assisted("sessionId") String sessionId,
                                         @Assisted("creationDate") Instant creationDate,
                                         @Assisted("modificationDate") Instant modificationDate,
                                         @Assisted("attributes") JsonObject attributes);
}
