package org.spincast.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.spincast.core.routing.ETag;
import org.spincast.core.routing.ETagFactory;
import org.spincast.testing.defaults.NoAppTestingBase;

import com.google.inject.Inject;

public class EtagTest extends NoAppTestingBase {

    @Inject
    protected ETagFactory eTagFactory;

    protected ETagFactory getETagFactory() {
        return this.eTagFactory;
    }

    @Test
    public void matchDefault() throws Exception {

        ETag eTag1 = getETagFactory().create("123");
        assertNotNull(eTag1);

        ETag eTag2 = getETagFactory().create("123");
        assertNotNull(eTag2);

        assertTrue(eTag1.matches(eTag2));
    }

    @Test
    public void matchWeak() throws Exception {

        ETag eTag1 = getETagFactory().create("123");
        assertNotNull(eTag1);

        ETag eTag2 = getETagFactory().create("123");
        assertNotNull(eTag2);

        assertTrue(eTag1.matches(eTag2, true));
    }

    @Test
    public void matchNoMatch() throws Exception {

        ETag eTag1 = getETagFactory().create("123");
        assertNotNull(eTag1);

        ETag eTag2 = getETagFactory().create("456");
        assertNotNull(eTag2);

        assertFalse(eTag1.matches(eTag2));
    }

    @Test
    public void matchNoMatchWeak() throws Exception {

        ETag eTag1 = getETagFactory().create("123");
        assertNotNull(eTag1);

        ETag eTag2 = getETagFactory().create("456");
        assertNotNull(eTag2);

        assertFalse(eTag1.matches(eTag2, true));
    }

    @Test
    public void matchWeak2() throws Exception {

        ETag eTag1 = getETagFactory().create("123", true);
        assertNotNull(eTag1);

        ETag eTag2 = getETagFactory().create("123");
        assertNotNull(eTag2);

        assertFalse(eTag1.matches(eTag2));
    }

    @Test
    public void matchWeak2WeakComparison() throws Exception {

        ETag eTag1 = getETagFactory().create("123", true);
        assertNotNull(eTag1);

        ETag eTag2 = getETagFactory().create("123");
        assertNotNull(eTag2);

        assertTrue(eTag1.matches(eTag2, true));
    }

    @Test
    public void matchWeak3() throws Exception {

        ETag eTag1 = getETagFactory().create("123");
        assertNotNull(eTag1);

        ETag eTag2 = getETagFactory().create("123", true);
        assertNotNull(eTag2);

        assertFalse(eTag1.matches(eTag2));
    }

    @Test
    public void matchWeak3WeakComparison() throws Exception {

        ETag eTag1 = getETagFactory().create("123");
        assertNotNull(eTag1);

        ETag eTag2 = getETagFactory().create("123", true);
        assertNotNull(eTag2);

        assertTrue(eTag1.matches(eTag2, true));
    }

    @Test
    public void matchWeak4() throws Exception {

        ETag eTag1 = getETagFactory().create("123", true);
        assertNotNull(eTag1);

        ETag eTag2 = getETagFactory().create("123", true);
        assertNotNull(eTag2);

        assertFalse(eTag1.matches(eTag2));
    }

    @Test
    public void matchWeak4WeakComparison() throws Exception {

        ETag eTag1 = getETagFactory().create("123", true);
        assertNotNull(eTag1);

        ETag eTag2 = getETagFactory().create("123", true);
        assertNotNull(eTag2);

        assertTrue(eTag1.matches(eTag2, true));
    }

    @Test
    public void matchNoMatchWeak2() throws Exception {

        ETag eTag1 = getETagFactory().create("123", true);
        assertNotNull(eTag1);

        ETag eTag2 = getETagFactory().create("456");
        assertNotNull(eTag2);

        assertFalse(eTag1.matches(eTag2));
    }

    @Test
    public void matchNoMatchWeak3() throws Exception {

        ETag eTag1 = getETagFactory().create("123");
        assertNotNull(eTag1);

        ETag eTag2 = getETagFactory().create("456", true);
        assertNotNull(eTag2);

        assertFalse(eTag1.matches(eTag2));
    }

    @Test
    public void matchNoMatchWeak4() throws Exception {

        ETag eTag1 = getETagFactory().create("123", true);
        assertNotNull(eTag1);

        ETag eTag2 = getETagFactory().create("456", true);
        assertNotNull(eTag2);

        assertFalse(eTag1.matches(eTag2));
    }

    @Test
    public void matchNoMatchWeakWeakComparison() throws Exception {

        ETag eTag1 = getETagFactory().create("123", true);
        assertNotNull(eTag1);

        ETag eTag2 = getETagFactory().create("456", true);
        assertNotNull(eTag2);

        assertFalse(eTag1.matches(eTag2, true));
    }

    @Test
    public void matchWildcard() throws Exception {

        ETag eTag1 = getETagFactory().create("*", false, true);
        assertNotNull(eTag1);

        ETag eTag2 = getETagFactory().create("123");
        assertNotNull(eTag2);

        assertTrue(eTag1.matches(eTag2));
    }

    @Test
    public void matchWildcard2() throws Exception {

        ETag eTag1 = getETagFactory().create("123");
        assertNotNull(eTag1);

        ETag eTag2 = getETagFactory().create("*", false, true);
        assertNotNull(eTag2);

        assertTrue(eTag1.matches(eTag2));
    }

    @Test
    public void matchWildcard3() throws Exception {

        ETag eTag1 = getETagFactory().create("*", false, true);
        assertNotNull(eTag1);

        ETag eTag2 = getETagFactory().create("*", false, true);
        assertNotNull(eTag2);

        assertTrue(eTag1.matches(eTag2));
    }

    @Test
    public void matchWildcardWeakComparison() throws Exception {

        ETag eTag1 = getETagFactory().create("123");
        assertNotNull(eTag1);

        ETag eTag2 = getETagFactory().create("*", false, true);
        assertNotNull(eTag2);

        assertTrue(eTag1.matches(eTag2, true));
    }

    @Test
    public void matchWildcardNull() throws Exception {

        ETag eTag1 = getETagFactory().create("*", false, true);
        assertNotNull(eTag1);

        assertTrue(eTag1.matches(null));
    }

    @Test
    public void matchNull() throws Exception {

        ETag eTag1 = getETagFactory().create("123");
        assertNotNull(eTag1);

        assertFalse(eTag1.matches(null));
    }

    @Test
    public void deserializeETagHeaderValid() throws Exception {

        ETag eTag = getETagFactory().deserializeHeaderValue("\"test\"");
        assertNotNull(eTag);
        assertEquals("test", eTag.getTag());
        assertFalse(eTag.isWeak());
        assertFalse(eTag.isWildcard());
    }

    @Test
    public void deserializeETagHeaderValidWeak() throws Exception {

        ETag eTag = getETagFactory().deserializeHeaderValue("W/\"test\"");
        assertNotNull(eTag);
        assertEquals("test", eTag.getTag());
        assertTrue(eTag.isWeak());
        assertFalse(eTag.isWildcard());
    }

    @Test
    public void deserializeETagHeaderValidWildcard() throws Exception {

        ETag eTag = getETagFactory().deserializeHeaderValue("*");
        assertNotNull(eTag);
        assertEquals("*", eTag.getTag());
        assertFalse(eTag.isWeak());
        assertTrue(eTag.isWildcard());
    }

    @Test
    public void deserializeETagHeaderWildcardQuotedIsNotWildcard() throws Exception {

        ETag eTag = getETagFactory().deserializeHeaderValue("\"*\"");
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
        } catch (Exception ex) {
        }
    }

    @Test
    public void deserializeETagHeaderEmpty() throws Exception {

        try {
            getETagFactory().deserializeHeaderValue("");
            fail();
        } catch (Exception ex) {
        }
    }

    @Test
    public void deserializeETagHeaderNoQuotes() throws Exception {

        try {
            getETagFactory().deserializeHeaderValue("test");
            fail();
        } catch (Exception ex) {
        }
    }

    @Test
    public void deserializeETagHeaderInvalidPrefix() throws Exception {

        try {
            getETagFactory().deserializeHeaderValue("X/\"test\"");
            fail();
        } catch (Exception ex) {
        }
    }

    @Test
    public void createETag() throws Exception {

        ETag eTag = getETagFactory().create("test");
        assertNotNull(eTag);
        assertEquals("test", eTag.getTag());
        assertFalse(eTag.isWeak());
        assertFalse(eTag.isWildcard());
    }

    @Test
    public void createETagWeak() throws Exception {

        ETag eTag = getETagFactory().create("test", true);
        assertNotNull(eTag);
        assertEquals("test", eTag.getTag());
        assertTrue(eTag.isWeak());
        assertFalse(eTag.isWildcard());
    }

    @Test
    public void createETagAsteriskTagIsNotWildcard() throws Exception {

        ETag eTag = getETagFactory().create("*");
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
        } catch (Exception ex) {
        }
    }

    @Test
    public void createETagInvalidEmpty() throws Exception {

        try {
            getETagFactory().create("");
            fail();
        } catch (Exception ex) {
        }
    }

    @Test
    public void createETagInvalidQuotes() throws Exception {

        try {
            getETagFactory().create("aaa\"bbb");
            fail();
        } catch (Exception ex) {
        }
    }

    @Test
    public void createETagWildcardAsteriskTag() throws Exception {

        ETag eTag = getETagFactory().create("*", false, true);
        assertNotNull(eTag);
        assertEquals("*", eTag.getTag());
        assertFalse(eTag.isWeak());
        assertTrue(eTag.isWildcard());
    }

    @Test
    public void createETagWildcardNullTag() throws Exception {

        ETag eTag = getETagFactory().create(null, false, true);
        assertNotNull(eTag);
        assertEquals("*", eTag.getTag());
        assertFalse(eTag.isWeak());
        assertTrue(eTag.isWildcard());
    }

    @Test
    public void createETagWildcardEmptyTag() throws Exception {

        ETag eTag = getETagFactory().create("", false, true);
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
        } catch (Exception ex) {
        }
    }

}
