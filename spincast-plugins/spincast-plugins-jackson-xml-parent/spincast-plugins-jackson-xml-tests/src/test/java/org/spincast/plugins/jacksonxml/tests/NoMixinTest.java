package org.spincast.plugins.jacksonxml.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.spincast.core.xml.XmlManager;
import org.spincast.defaults.testing.UnitTestDefaultContextsBase;

import com.google.inject.Inject;

public class NoMixinTest extends UnitTestDefaultContextsBase {

    @Inject
    XmlManager xmlManager;

    protected XmlManager getXmlManager() {
        return this.xmlManager;
    }

    @Test
    public void noMixinSerialize() throws Exception {

        User user = new UserDefault();
        user.setName("Stromgol");
        user.setAge(123);
        user.setTitle("alien");

        String xml = getXmlManager().toXml(user);
        assertNotNull(xml);
        assertEquals("<UserDefault><name>Stromgol</name><age>123</age><title>Title is: alien</title></UserDefault>", xml);
    }

    @Test
    public void noMixinDeserialize() throws Exception {

        String xml = "<User><name>Stromgol</name><age>123</age><title>alien</title></User>";

        User user = getXmlManager().fromXml(xml, UserDefault.class);
        assertNotNull(user);
        assertEquals("Stromgol", user.getName());
        assertEquals(123, user.getAge());
        assertEquals("Title is: alien", user.getTitle());

    }

}
