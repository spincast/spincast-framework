package org.spincast.core.dictionary;

import java.util.Map;

/**
 * A Set of entries to add to the {@link Dictionary}.
 * <p>
 * This allows plugins to add there own localizable messages.
 */
public interface DictionaryEntries {

    public Map<String, Map<String, String>> getDictionaryEntries();

}
