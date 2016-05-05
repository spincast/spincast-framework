package org.spincast.plugins.configpropsfile.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.InputStream;

import javax.annotation.Nullable;

import org.junit.Test;
import org.spincast.core.config.ISpincastConfig;
import org.spincast.core.guice.MainArgs;
import org.spincast.core.utils.ISpincastUtils;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.defaults.tests.DefaultIntegrationTestingBase;
import org.spincast.defaults.tests.DefaultTestingModule;
import org.spincast.plugins.configpropsfile.SpincastConfigPropsFileBased;
import org.spincast.shaded.org.apache.commons.io.FileUtils;

import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.Scopes;

public class CustomConfigClassTest extends DefaultIntegrationTestingBase {

    @Inject
    protected IAppConfig appConfig;

    protected String appPropertiesPath;

    //==========================================
    // We create the "app.properties" file to use.
    //==========================================
    @Override
    public void beforeClass() {

        createTempPropsFile();

        super.beforeClass();
    };

    protected void createTempPropsFile() {

        try {
            InputStream appPropsStream = getClass().getClassLoader().getResourceAsStream("app.properties");
            assertNotNull(appPropsStream);

            this.appPropertiesPath = createTestingFilePath();
            FileUtils.copyInputStreamToFile(appPropsStream, new File(this.appPropertiesPath));
        } catch(Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    @Override
    protected String[] getMainArgsToUse() {
        //==========================================
        // Pass the path to our configuation file.
        //==========================================
        return new String[]{this.appPropertiesPath};
    }

    public static interface IAppConfig extends ISpincastConfig {

        public String getTestString();

        public String getTestStringNotFound();

        public String getTestStringNotFoundDefaultValue();

        public Boolean getTestBoolean();

        public Boolean getTestBooleanNotFound();

        public Boolean getTestBooleanNotFoundDefaultValue();

        public Integer getTestInteger();

        public Integer getTestIntegerNotFound();

        public Integer getTestIntegerNotFoundDefaultValue();

    }

    public static class PropsFileBasedConfig extends SpincastConfigPropsFileBased implements IAppConfig {

        @Inject
        public PropsFileBasedConfig(ISpincastUtils spincastUtils,
                                    @MainArgs @Nullable String[] mainArgs) {
            super(spincastUtils, mainArgs);
        }

        @Override
        public String getTestString() {
            return getConfig("app.test.string");
        }

        @Override
        public String getTestStringNotFound() {
            return getConfig("app.nope");
        }

        @Override
        public String getTestStringNotFoundDefaultValue() {
            return getConfig("app.nope", "some default value");
        }

        @Override
        public Boolean getTestBoolean() {
            return getConfigBoolean("app.test.boolean");
        }

        @Override
        public Boolean getTestBooleanNotFound() {
            return getConfigBoolean("app.nope");
        }

        @Override
        public Boolean getTestBooleanNotFoundDefaultValue() {
            return getConfigBoolean("app.nope", false);
        }

        @Override
        public Integer getTestInteger() {
            return getConfigInteger("app.test.integer");
        }

        @Override
        public Integer getTestIntegerNotFound() {
            return getConfigInteger("app.nope");
        }

        @Override
        public Integer getTestIntegerNotFoundDefaultValue() {
            return getConfigInteger("app.nope", 42);
        }
    }

    @Override
    public Module getTestingModule() {
        return new DefaultTestingModule(getMainArgsToUse()) {

            @Override
            protected void configure() {
                super.configure();
                bind(IAppConfig.class).to(PropsFileBasedConfig.class).in(Scopes.SINGLETON);
            }

            @Override
            protected Class<? extends ISpincastConfig> getSpincastConfigClass() {
                return PropsFileBasedConfig.class;
            }
        };
    }

    protected IAppConfig getAppConfig() {
        return this.appConfig;
    }

    @Test
    public void testDefaultConfigOverriden() throws Exception {

        assertNotNull(getSpincastConfig().getHttpServerPort());
        assertEquals(33333, getSpincastConfig().getHttpServerPort());

        assertNotNull(getAppConfig().getHttpServerPort());
        assertEquals(33333, getAppConfig().getHttpServerPort());
    }

    @Test
    public void testDefaultConfigNotOverriden() throws Exception {

        assertNotNull(getSpincastConfig().getServerHost());
        assertEquals("0.0.0.0", getSpincastConfig().getServerHost());

        assertNotNull(getAppConfig().getServerHost());
        assertEquals("0.0.0.0", getAppConfig().getServerHost());
    }

    @Test
    public void testCustomConfigString() throws Exception {

        assertNotNull(getAppConfig().getTestString());
        assertEquals("été", getAppConfig().getTestString());
    }

    @Test
    public void testCustomConfigStringNotFound() throws Exception {

        try {
            getAppConfig().getTestStringNotFound();
            fail();
        } catch(Exception ex) {
        }
    }

    @Test
    public void testCustomConfigStringDefaultValue() throws Exception {

        assertNotNull(getAppConfig().getTestStringNotFoundDefaultValue());
        assertEquals("some default value", getAppConfig().getTestStringNotFoundDefaultValue());
    }

    @Test
    public void testCustomConfigBoolean() throws Exception {

        assertNotNull(getAppConfig().getTestBoolean());
        assertEquals(true, getAppConfig().getTestBoolean());
    }

    @Test
    public void testCustomConfigBooleanNotFound() throws Exception {

        try {
            getAppConfig().getTestBooleanNotFound();
            fail();
        } catch(Exception ex) {
        }
    }

    @Test
    public void testCustomConfigBooleanDefaultValue() throws Exception {

        assertNotNull(getAppConfig().getTestBooleanNotFoundDefaultValue());
        assertEquals(false, getAppConfig().getTestBooleanNotFoundDefaultValue());
    }

    @Test
    public void testCustomConfigInteger() throws Exception {

        assertNotNull(getAppConfig().getTestInteger());
        assertEquals(Integer.valueOf(123), getAppConfig().getTestInteger());
    }

    @Test
    public void testCustomConfigIntegerNotFound() throws Exception {

        try {
            getAppConfig().getTestIntegerNotFound();
            fail();
        } catch(Exception ex) {
        }
    }

    @Test
    public void testCustomConfigIntegerDefaultValue() throws Exception {

        assertNotNull(getAppConfig().getTestIntegerNotFoundDefaultValue());
        assertEquals(Integer.valueOf(42), getAppConfig().getTestIntegerNotFoundDefaultValue());
    }

}
