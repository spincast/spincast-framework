package org.spincast.tests.json;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.json.JsonManager;
import org.spincast.core.json.JsonObject;
import org.spincast.defaults.testing.NoAppTestingBase;
import org.spincast.plugins.jacksonjson.SpincastJsonManagerConfig;

import com.google.inject.Inject;
import com.google.inject.Module;

public class JsonPrettyPrintConfigTest extends NoAppTestingBase {

    @Inject
    protected JsonManager jsonManager;

    @Override
    protected Module getExtraOverridingModule() {
        return new SpincastGuiceModuleBase() {

            @Override
            protected void configure() {

                //==========================================
                // Bind custom Json Manager configs
                //==========================================
                bind(SpincastJsonManagerConfig.class).toInstance(new SpincastJsonManagerConfig() {

                    @Override
                    public String getPrettyPrinterNewlineChars() {
                        return "\r\n";
                    }

                    @Override
                    public int getPrettyPrinterIndentationSpaceNumber() {
                        return 2;
                    }

                    @Override
                    public boolean isSerializeEnumsToNameAndLabelObjects() {
                        return true;
                    }
                });
            }
        };
    }

    @Test
    public void toJsonStringPretty() throws Exception {

        JsonObject jsonObj = this.jsonManager.create();
        jsonObj.set("someInt", 123);

        String jsonStr = this.jsonManager.toJsonString(jsonObj, true);
        assertNotNull(jsonStr);

        StringBuilder expected = new StringBuilder();

        expected.append("{\r\n");
        expected.append("  \"someInt\" : 123\r\n");
        expected.append("}");

        assertEquals(expected.toString(), jsonStr);

    }

}
