package org.spincast.core.config;

import java.util.Map;
import java.util.Set;

import org.spincast.core.dictionary.Dictionary;
import org.spincast.core.dictionary.DictionaryEntries;
import org.spincast.core.dictionary.DictionaryKeyNotFoundException;
import org.spincast.core.dictionary.SpincastCoreDictionaryEntries;

import com.google.inject.Inject;

/**
 * Init validation performs by Spincast when
 * an application starts.
 */
public class SpincastInitValidator {

    private final SpincastConfig spincastConfig;
    private final Dictionary dictionary;
    private final Set<DictionaryEntries> dictionaryEntries;

    /**
     * Constructor
     */
    @Inject
    public SpincastInitValidator(SpincastConfig spincastConfig,
                                 Dictionary dictionary,
                                 SpincastCoreDictionaryEntries spincastCoreDictionaryEntries,
                                 Set<DictionaryEntries> dictionaryEntries) {
        this.spincastConfig = spincastConfig;
        this.dictionary = dictionary;
        this.dictionaryEntries = dictionaryEntries;
    }

    protected SpincastConfig getSpincastConfig() {
        return this.spincastConfig;
    }

    protected Dictionary getDictionary() {
        return this.dictionary;
    }

    protected Set<DictionaryEntries> getDictionaryEntries() {
        return this.dictionaryEntries;
    }

    /**
     * Init
     */
    @Inject
    protected void init() {
        validateLocalhostHost();
        validateDictionaryEntries();
    }

    protected void validateLocalhostHost() {

        if (!getSpincastConfig().isDevelopmentMode() && !"local".equals(getSpincastConfig().getEnvironmentName()) &&
            "localhost".equals(getSpincastConfig().getPublicServerHost()) &&
            getSpincastConfig().isValidateLocalhostHost()) {
            throw new RuntimeException("Did you forget to override the SpincastConfig#getPublicUrlBase() " +
                                       "method? The application was started on an environment other than 'local', with the debug mode disabled, " +
                                       "but the host returned by SpincastConfig#getPublicUrlBase() is 'localhost'... Make " +
                                       "sure the host specified in this config is the *public* one. Note : you can disable this validation " +
                                       "by changing the SpincastConfig#isValidateLocalhostHost() config.");
        }
    }

    /**
     * Validates that all bounded Dictionary entry has
     * a value.
     */
    protected void validateDictionaryEntries() {

        Set<DictionaryEntries> dictionaryEntriesSet = getDictionaryEntries();
        if (dictionaryEntriesSet == null) {
            throw new RuntimeException("The core Spincast Dictionary messages don't seem to be bound! See " +
                                       SpincastCoreDictionaryEntries.class.getName() + " .");
        }

        for (DictionaryEntries dictionaryEntriesOne : dictionaryEntriesSet) {
            Map<String, Map<String, String>> messages = dictionaryEntriesOne.getDictionaryEntries();
            if (messages == null) {
                continue;
            }

            for (String requiredKey : messages.keySet()) {
                try {
                    getDictionary().get(requiredKey);
                } catch (DictionaryKeyNotFoundException ex) {
                    throw new RuntimeException("The key \"" + requiredKey + "\" was not found in the current " +
                                               Dictionary.class.getName() + " implementation and this is required entry for " +
                                               "Spincast or one of the plugin.");
                }
            }
        }
    }

}
