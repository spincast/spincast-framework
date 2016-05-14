package org.spincast.tests.xml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.spincast.core.json.IJsonManager;
import org.spincast.core.json.IJsonObject;
import org.spincast.core.xml.IXmlManager;
import org.spincast.defaults.tests.DefaultIntegrationTestingBase;
import org.spincast.defaults.tests.DefaultTestingModule;
import org.spincast.plugins.jacksonxml.ISpincastXmlManagerConfig;

import com.google.inject.Inject;
import com.google.inject.Module;

public class XmlPrettyPrintConfigTest extends DefaultIntegrationTestingBase {

    @Inject
    protected IJsonManager jsonManager;

    @Inject
    protected IXmlManager xmlManager;

    @Override
    public Module getTestingModule() {
        return new DefaultTestingModule(getMainArgsToUse()) {

            @Override
            protected void configure() {
                super.configure();

                //==========================================
                // Bind custom XML Manager configs
                //==========================================
                bind(ISpincastXmlManagerConfig.class).toInstance(new ISpincastXmlManagerConfig() {

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

    @Test
    public void toXmlPretty() throws Exception {

        IJsonObject jsonObj = this.jsonManager.create();
        jsonObj.put("someInt", 123);

        String xml = this.xmlManager.toXml(jsonObj, true);
        assertNotNull(xml);

        StringBuilder expected = new StringBuilder();
        expected.append("<JsonObject>\r\n");
        expected.append("  <someInt>123</someInt>\r\n");
        expected.append("</JsonObject>");

        assertEquals(expected.toString(), xml);
    }

}
