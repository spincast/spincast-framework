package org.spincast.tests.xml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.json.JsonManager;
import org.spincast.core.json.JsonObject;
import org.spincast.core.xml.XmlManager;
import org.spincast.defaults.testing.IntegrationTestNoAppDefaultContextsBase;
import org.spincast.plugins.jacksonxml.SpincastXmlManagerConfig;

import com.google.inject.Inject;
import com.google.inject.Module;

public class XmlPrettyPrintConfigTest extends IntegrationTestNoAppDefaultContextsBase {

    @Override
    protected Module getExtraOverridingModule() {
        return new SpincastGuiceModuleBase() {

            @Override
            protected void configure() {

                //==========================================
                // Bind custom XML Manager configs
                //==========================================
                bind(SpincastXmlManagerConfig.class).toInstance(new SpincastXmlManagerConfig() {

                    @Override
                    public String getPrettyPrinterNewlineChars() {
                        return "\r\n";
                    }

                    @Override
                    public int getPrettyPrinterIndentationSpaceNumber() {
                        return 2;
                    }
                });
            }
        };
    }

    @Inject
    protected JsonManager jsonManager;

    @Inject
    protected XmlManager xmlManager;

    @Test
    public void toXmlPretty() throws Exception {

        JsonObject jsonObj = this.jsonManager.create();
        jsonObj.put("someInt", 123);

        String xml = this.xmlManager.toXml(jsonObj, true);
        assertNotNull(xml);

        StringBuilder expected = new StringBuilder();
        expected.append("<JsonObject>\r\n");
        expected.append("  <someInt>123</someInt>\r\n");
        expected.append("</JsonObject>\r\n");

        assertEquals(expected.toString(), xml);
    }

}
