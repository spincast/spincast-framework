package org.spincast.plugins.session.tests;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.junit.Test;
import org.spincast.core.json.JsonManager;
import org.spincast.core.json.JsonObject;
import org.spincast.plugins.session.SpincastSession;

import com.google.inject.Inject;

public class SessionManagerTest extends SessionTestBase {

    @Inject
    protected JsonManager jsonManager;

    protected JsonManager getJsonManager() {
        return this.jsonManager;
    }

    @Test
    public void generateNewSessionId() throws Exception {

        String sessionId = getSessionManager().generateNewSessionId();
        assertNotNull(sessionId);
        assertTrue(sessionId.length() > 8); // at least!

        String sessionId2 = getSessionManager().generateNewSessionId();
        assertNotNull(sessionId2);
        assertTrue(sessionId2.length() > 8); // at least!
        assertNotEquals(sessionId2, sessionId);
    }

    @Test
    public void createNewSession() throws Exception {
        SpincastSession session = getSessionManager().createNewSession();
        assertNotNull(session);
        assertNotNull(session.getAttributes());
    }

    @Test
    public void createSession() throws Exception {

        Instant creationDate = Instant.now();
        Instant modificationDate = creationDate.plus(2, ChronoUnit.HOURS);

        JsonObject attributes = getJsonManager().create();
        attributes.set("k1", "v1");
        attributes.set("k2", "v2");

        SpincastSession session = getSessionManager().createSession("123",
                                                                    creationDate,
                                                                    modificationDate,
                                                                    attributes);
        assertNotNull(session);
        assertNotNull(session.getAttributes());
    }

}
