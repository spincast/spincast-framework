package org.spincast.plugins.jacksonxml.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.spincast.core.xml.IXmlManager;
import org.spincast.defaults.tests.DefaultIntegrationTestingBase;

import com.google.inject.Inject;

public class NoMixinTest extends DefaultIntegrationTestingBase {

    @Inject
    IXmlManager xmlManager;

    protected IXmlManager getXmlManager() {
        return this.xmlManager;
    }

    @Test
    public void noMixinSerialize() throws Exception {

        IUser user = new User();
        user.setName("Stromgol");
        user.setAge(123);
        user.setTitle("alien");

        String xml = getXmlManager().toXml(user);
        assertNotNull(xml);
        assertEquals("<User><name>Stromgol</name><age>123</age><title>Title is: alien</title></User>", xml);
    }

    @Test
    public void noMixinDeserialize() throws Exception {

        String xml = "<User><name>Stromgol</name><age>123</age><title>alien</title></User>";

        IUser user = getXmlManager().fromXml(xml, User.class);
        assertNotNull(user);
        assertEquals("Stromgol", user.getName());
        assertEquals(123, user.getAge());
        assertEquals("Title is: alien", user.getTitle());

    }

}
