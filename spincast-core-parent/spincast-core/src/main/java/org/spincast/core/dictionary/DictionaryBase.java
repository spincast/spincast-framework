package org.spincast.core.dictionary;

import java.util.HashMap;
import java.util.Map;

import org.spincast.core.utils.Pair;

/**
 * Base class that can be used for a {@link Dictionary}
 * implementation.
 */
public abstract class DictionaryBase {

    private Map<String, Map<String, String>> messages;

    /**
     * Adds a message.
     */
    protected final void key(String key, Pair... langMsgs) {
        if (langMsgs == null || langMsgs.length == 0) {
            return;
        }

        Map<String, String> map = getMessages().get(key);
        if (map == null) {
            map = new HashMap<String, String>();
            getMessages().put(key, map);
        }

        for (Pair langMsg : langMsgs) {
            map.put(langMsg.getKey(), String.valueOf(langMsg.getValue()));
        }
    }

    /**
     * Creates a localized message.
     */
    protected Pair msg(String lang, String msg) {
        return Pair.of(lang, msg);
    }

    protected Map<String, Map<String, String>> getMessages() {
        if (this.messages == null) {
            this.messages = new HashMap<String, Map<String, String>>();
        }
        return this.messages;
    }

}
