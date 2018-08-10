package org.spincast.plugins.request;

import java.util.Map;

import org.spincast.core.dictionary.Dictionary;
import org.spincast.core.dictionary.DictionaryBase;
import org.spincast.core.dictionary.DictionaryEntries;

import com.google.inject.Inject;

/**
 * Messages to be added to the {@link Dictionary}.
 */
public class SpincastRequestPluginDictionaryEntries extends DictionaryBase implements DictionaryEntries {

    public static final String MESSAGE_KEY_FORM_GET_EMPTYNAME =
            SpincastRequestPluginDictionaryEntries.class.getName() + ".form.get.EmptyName";

    @Inject
    public void init() {

        key(MESSAGE_KEY_FORM_GET_EMPTYNAME,
            msg("", "The name can't be empty."));
    }

    @Override
    public Map<String, Map<String, String>> getDictionaryEntries() {
        return getMessages();
    }

}
