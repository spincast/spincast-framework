package org.spincast.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.spincast.core.routing.IETag;
import org.spincast.core.routing.IETagFactory;
import org.spincast.defaults.tests.SpincastDefaultTestingModule;
import org.spincast.testing.core.SpincastTestBase;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

public class EtagTest extends SpincastTestBase {

    @Override
    protected Injector createInjector() {
        return Guice.createInjector(new SpincastDefaultTestingModule());
    }

    @Inject
    protected IETagFactory eTagFactory;

    protected IETagFactory getETagFactory() {
        return this.eTagFactory;
    }

    @Test
    public void matchDefault() throws Exception {

        IETag eTag1 = getETagFactory().create("123");
        assertNotNull(eTag1);

        IETag eTag2 = getETagFactory().create("123");
        assertNotNull(eTag2);

        assertTrue(eTag1.matches(eTag2));
    }

    @Test
    public void matchWeak() throws Exception {

        IETag eTag1 = getETagFactory().create("123");
        assertNotNull(eTag1);

        IETag eTag2 = getETagFactory().create("123");
        assertNotNull(eTag2);

        assertTrue(eTag1.matches(eTag2, true));
    }

    @Test
    public void matchNoMatch() throws Exception {

        IETag eTag1 = getETagFactory().create("123");
        assertNotNull(eTag1);

        IETag eTag2 = getETagFactory().create("456");
        assertNotNull(eTag2);

        assertFalse(eTag1.matches(eTag2));
    }

    @Test
    public void matchNoMatchWeak() throws Exception {

        IETag eTag1 = getETagFactory().create("123");
        assertNotNull(eTag1);

        IETag eTag2 = getETagFactory().create("456");
        assertNotNull(eTag2);

        assertFalse(eTag1.matches(eTag2, true));
    }

    @Test
    public void matchWeak2() throws Exception {

        IETag eTag1 = getETagFactory().create("123", true);
        assertNotNull(eTag1);

        IETag eTag2 = getETagFactory().create("123");
        assertNotNull(eTag2);

        assertFalse(eTag1.matches(eTag2));
    }

    @Test
    public void matchWeak2WeakComparison() throws Exception {

        IETag eTag1 = getETagFactory().create("123", true);
        assertNotNull(eTag1);

        IETag eTag2 = getETagFactory().create("123");
        assertNotNull(eTag2);

        assertTrue(eTag1.matches(eTag2, true));
    }

    @Test
    public void matchWeak3() throws Exception {

        IETag eTag1 = getETagFactory().create("123");
        assertNotNull(eTag1);

        IETag eTag2 = getETagFactory().create("123", true);
        assertNotNull(eTag2);

        assertFalse(eTag1.matches(eTag2));
    }

    @Test
    public void matchWeak3WeakComparison() throws Exception {

        IETag eTag1 = getETagFactory().create("123");
        assertNotNull(eTag1);

        IETag eTag2 = getETagFactory().create("123", true);
        assertNotNull(eTag2);

        assertTrue(eTag1.matches(eTag2, true));
    }

    @Test
    public void matchWeak4() throws Exception {

        IETag eTag1 = getETagFactory().create("123", true);
        assertNotNull(eTag1);

        IETag eTag2 = getETagFactory().create("123", true);
        assertNotNull(eTag2);

        assertFalse(eTag1.matches(eTag2));
    }

    @Test
    public void matchWeak4WeakComparison() throws Exception {

        IETag eTag1 = getETagFactory().create("123", true);
        assertNotNull(eTag1);

        IETag eTag2 = getETagFactory().create("123", true);
        assertNotNull(eTag2);

        assertTrue(eTag1.matches(eTag2, true));
    }

    @Test
    public void matchNoMatchWeak2() throws Exception {

        IETag eTag1 = getETagFactory().create("123", true);
        assertNotNull(eTag1);

        IETag eTag2 = getETagFactory().create("456");
        assertNotNull(eTag2);

        assertFalse(eTag1.matches(eTag2));
    }

    @Test
    public void matchNoMatchWeak3() throws Exception {

        IETag eTag1 = getETagFactory().create("123");
        assertNotNull(eTag1);

        IETag eTag2 = getETagFactory().create("456", true);
        assertNotNull(eTag2);

        assertFalse(eTag1.matches(eTag2));
    }

    @Test
    public void matchNoMatchWeak4() throws Exception {

        IETag eTag1 = getETagFactory().create("123", true);
        assertNotNull(eTag1);

        IETag eTag2 = getETagFactory().create("456", true);
        assertNotNull(eTag2);

        assertFalse(eTag1.matches(eTag2));
    }

    @Test
    public void matchNoMatchWeakWeakComparison() throws Exception {

        IETag eTag1 = getETagFactory().create("123", true);
        assertNotNull(eTag1);

        IETag eTag2 = getETagFactory().create("456", true);
        assertNotNull(eTag2);

        assertFalse(eTag1.matches(eTag2, true));
    }

    @Test
    public void matchWildcard() throws Exception {

        IETag eTag1 = getETagFactory().create("*", false, true);
        assertNotNull(eTag1);

        IETag eTag2 = getETagFactory().create("123");
        assertNotNull(eTag2);

        assertTrue(eTag1.matches(eTag2));
    }

    @Test
    public void matchWildcard2() throws Exception {

        IETag eTag1 = getETagFactory().create("123");
        assertNotNull(eTag1);

        IETag eTag2 = getETagFactory().create("*", false, true);
        assertNotNull(eTag2);

        assertTrue(eTag1.matches(eTag2));
    }

    @Test
    public void matchWildcard3() throws Exception {

        IETag eTag1 = getETagFactory().create("*", false, true);
        assertNotNull(eTag1);

        IETag eTag2 = getETagFactory().create("*", false, true);
        assertNotNull(eTag2);

        assertTrue(eTag1.matches(eTag2));
    }

    @Test
    public void matchWildcardWeakComparison() throws Exception {

        IETag eTag1 = getETagFactory().create("123");
        assertNotNull(eTag1);

        IETag eTag2 = getETagFactory().create("*", false, true);
        assertNotNull(eTag2);

        assertTrue(eTag1.matches(eTag2, true));
    }

    @Test
    public void matchWildcardNull() throws Exception {

        IETag eTag1 = getETagFactory().create("*", false, true);
        assertNotNull(eTag1);

        assertTrue(eTag1.matches(null));
    }

    @Test
    public void matchNull() throws Exception {

        IETag eTag1 = getETagFactory().create("123");
        assertNotNull(eTag1);

        assertFalse(eTag1.matches(null));
    }

    @Test
    public void deserializeETagHeaderValid() throws Exception {

        IETag eTag = getETagFactory().deserializeHeaderValue("\"test\"");
        assertNotNull(eTag);
        assertEquals("test", eTag.getTag());
        assertFalse(eTag.isWeak());
        assertFalse(eTag.isWildcard());
    }

    @Test
    public void deserializeETagHeaderValidWeak() throws Exception {

        IETag eTag = getETagFactory().deserializeHeaderValue("W/\"test\"");
        assertNotNull(eTag);
        assertEquals("test", eTag.getTag());
        assertTrue(eTag.isWeak());
        assertFalse(eTag.isWildcard());
    }

    @Test
    public void deserializeETagHeaderValidWildcard() throws Exception {

        IETag eTag = getETagFactory().deserializeHeaderValue("*");
        assertNotNull(eTag);
        assertEquals("*", eTag.getTag());
        assertFalse(eTag.isWeak());
        assertTrue(eTag.isWildcard());
    }

    @Test
    public void deserializeETagHeaderWildcardQuotedIsNotWildcard() throws Exception {

        IETag eTag = getETagFactory().deserializeHeaderValue("\"*\"");
        assertNotNull(eTag);
        assertEquals("*", eTag.getTag());
        assertFalse(eTag.isWeak());
        assertFalse(eTag.isWildcard());
    }

    @Test
    public void deserializeETagHeaderNull() throws Exception {

        try {
            getETagFactory().deserializeHeaderValue(null);
            fail();
        } catch(Exception ex) {
        }
    }

    @Test
    public void deserializeETagHeaderEmpty() throws Exception {

        try {
            getETagFactory().deserializeHeaderValue("");
            fail();
        } catch(Exception ex) {
        }
    }

    @Test
    public void deserializeETagHeaderNoQuotes() throws Exception {

        try {
            getETagFactory().deserializeHeaderValue("test");
            fail();
        } catch(Exception ex) {
        }
    }

    @Test
    public void deserializeETagHeaderInvalidPrefix() throws Exception {

        try {
            getETagFactory().deserializeHeaderValue("X/\"test\"");
            fail();
        } catch(Exception ex) {
        }
    }

    @Test
    public void createETag() throws Exception {

        IETag eTag = getETagFactory().create("test");
        assertNotNull(eTag);
        assertEquals("test", eTag.getTag());
        assertFalse(eTag.isWeak());
        assertFalse(eTag.isWildcard());
    }

    @Test
    public void createETagWeak() throws Exception {

        IETag eTag = getETagFactory().create("test", true);
        assertNotNull(eTag);
        assertEquals("test", eTag.getTag());
        assertTrue(eTag.isWeak());
        assertFalse(eTag.isWildcard());
    }

    @Test
    public void createETagAsteriskTagIsNotWildcard() throws Exception {

        IETag eTag = getETagFactory().create("*");
        assertNotNull(eTag);
        assertEquals("*", eTag.getTag());
        assertFalse(eTag.isWeak());
        assertFalse(eTag.isWildcard());
    }

    @Test
    public void createETagInvalidNull() throws Exception {

        try {
            getETagFactory().create(null);
            fail();
        } catch(Exception ex) {
        }
    }

    @Test
    public void createETagInvalidEmpty() throws Exception {

        try {
            getETagFactory().create("");
            fail();
        } catch(Exception ex) {
        }
    }

    @Test
    public void createETagInvalidQuotes() throws Exception {

        try {
            getETagFactory().create("aaa\"bbb");
            fail();
        } catch(Exception ex) {
        }
    }

    @Test
    public void createETagWildcardAsteriskTag() throws Exception {

        IETag eTag = getETagFactory().create("*", false, true);
        assertNotNull(eTag);
        assertEquals("*", eTag.getTag());
        assertFalse(eTag.isWeak());
        assertTrue(eTag.isWildcard());
    }

    @Test
    public void createETagWildcardNullTag() throws Exception {

        IETag eTag = getETagFactory().create(null, false, true);
        assertNotNull(eTag);
        assertEquals("*", eTag.getTag());
        assertFalse(eTag.isWeak());
        assertTrue(eTag.isWildcard());
    }

    @Test
    public void createETagWildcardEmptyTag() throws Exception {

        IETag eTag = getETagFactory().create("", false, true);
        assertNotNull(eTag);
        assertEquals("*", eTag.getTag());
        assertFalse(eTag.isWeak());
        assertTrue(eTag.isWildcard());
    }

    @Test
    public void createETagWildcardWeakFlagIsInvalid() throws Exception {

        try {
            getETagFactory().create("*", true, true);
            fail();
        } catch(Exception ex) {
        }
    }

}