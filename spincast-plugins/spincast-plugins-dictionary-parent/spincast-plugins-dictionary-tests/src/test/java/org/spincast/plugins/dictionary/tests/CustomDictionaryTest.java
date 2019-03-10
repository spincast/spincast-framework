package org.spincast.plugins.dictionary.tests;

import static org.junit.Assert.assertNotNull;

import java.util.Map;

import org.junit.Test;
import org.spincast.core.dictionary.Dictionary;
import org.spincast.core.dictionary.SpincastCoreDictionaryEntries;
import org.spincast.testing.defaults.NoAppTestingBase;

import com.google.inject.Inject;

public class CustomDictionaryTest extends NoAppTestingBase {

    @Inject
    Dictionary dictionary;

    @Inject
    SpincastCoreDictionaryEntries spincastCoreDictionaryEntries;

    protected Dictionary getDictionary() {
        return this.dictionary;
    }

    @Test
    public void hasSpincastCoreMessages() throws Exception {

        Map<String, Map<String, String>> coreDictionaryEntries = this.spincastCoreDictionaryEntries.getDictionaryEntries();
        assertNotNull(coreDictionaryEntries);

        for (String key : coreDictionaryEntries.keySet()) {
            assertNotNull(getDictionary().get(key));
        }
    }

}
