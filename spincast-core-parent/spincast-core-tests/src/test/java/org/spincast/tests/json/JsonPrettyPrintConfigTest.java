package org.spincast.tests.json;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.spincast.core.json.IJsonManager;
import org.spincast.core.json.IJsonObject;
import org.spincast.defaults.tests.DefaultTestingModule;
import org.spincast.plugins.jacksonjson.ISpincastJsonManagerConfig;
import org.spincast.testing.core.SpincastGuiceModuleBasedTestBase;

import com.google.inject.Inject;
import com.google.inject.Module;

public class JsonPrettyPrintConfigTest extends SpincastGuiceModuleBasedTestBase {

    @Inject
    protected IJsonManager jsonManager;

    @Override
    public Module getTestingModule() {
        return new DefaultTestingModule() {

            @Override
            protected void configure() {
                super.configure();

                //==========================================
                // Bind custom Json Manager configs
                //==========================================
                bind(ISpincastJsonManagerConfig.class).toInstance(new ISpincastJsonManagerConfig() {

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
    public void toJsonStringPretty() throws Exception {

        IJsonObject jsonObj = this.jsonManager.create();
        jsonObj.put("someInt", 123);

        String jsonStr = this.jsonManager.toJsonString(jsonObj, true);
        assertNotNull(jsonStr);

        StringBuilder expected = new StringBuilder();

        expected.append("{\r\n");
        expected.append("  \"someInt\" : 123\r\n");
        expected.append("}");

        assertEquals(expected.toString(), jsonStr);

    }

}
