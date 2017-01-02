package org.spincast.plugins.configpropsfile.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.InputStream;

import javax.annotation.Nullable;

import org.junit.Test;
import org.spincast.core.config.SpincastConfig;
import org.spincast.core.guice.MainArgs;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.core.utils.SpincastUtils;
import org.spincast.defaults.bootstrapping.Spincast;
import org.spincast.defaults.testing.IntegrationTestNoAppDefaultContextsBase;
import org.spincast.plugins.configpropsfile.FreeKeyConfig;
import org.spincast.plugins.configpropsfile.SpincastConfigPropsFileBased;
import org.spincast.plugins.configpropsfile.SpincastConfigPropsFilePlugin;
import org.spincast.plugins.configpropsfile.SpincastConfigPropsFilePluginConfig;
import org.spincast.plugins.configpropsfile.SpincastConfigPropsFilePluginDefault;
import org.spincast.shaded.org.apache.commons.io.FileUtils;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Scopes;

public class CustomConfigsTest extends IntegrationTestNoAppDefaultContextsBase {

    /**
     * We'll manage the testing configurations by ourself.
     */
    @Override
    protected boolean isEnableGuiceTweakerTestingConfigMecanism() {
        return false;
    }

    @Override
    protected Injector createInjector() {

        return Spincast.configure()
                       .bindCurrentClass(false)
                       .disableDefaultConfigPlugin()
                       .plugin(new SpincastConfigPropsFilePlugin())
                       .module(new SpincastGuiceModuleBase() {

                           @Override
                           protected void configure() {

                               bind(SpincastConfig.class).to(PropsFileBasedConfigTesting.class).in(Scopes.SINGLETON);
                               bind(AppConfig.class).to(PropsFileBasedConfigTesting.class).in(Scopes.SINGLETON);

                               //==========================================
                               // The configuration for the prop file based
                               // config plugin
                               //==========================================
                               bind(SpincastConfigPropsFilePluginConfig.class).toInstance(new SpincastConfigPropsFilePluginDefault() {

                                   //==========================================
                                   // We enable the main arg strategy!
                                   //==========================================
                                   @Override
                                   public int getSpecificPathMainArgsPosition() {
                                       return 1;
                                   }
                               });
                           }
                       })
                       .mainArgs(new String[]{this.appPropertiesPath})
                       .init();
    }

    @Inject
    protected AppConfig appConfig;

    protected String appPropertiesPath;

    protected AppConfig getAppConfig() {
        return this.appConfig;
    }

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

    public static interface AppConfig extends SpincastConfig, FreeKeyConfig {

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

    public static class PropsFileBasedConfigTesting extends SpincastConfigPropsFileBased implements AppConfig {

        @Inject
        public PropsFileBasedConfigTesting(SpincastUtils spincastUtils,
                                           @MainArgs @Nullable String[] mainArgs,
                                           @Nullable SpincastConfigPropsFilePluginConfig pluginConfig) {
            super(spincastUtils, mainArgs, pluginConfig);
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

        @Override
        public boolean isDebugEnabled() {
            return false;
        }
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

    @Test
    public void freeKeyString() throws Exception {

        String config = getAppConfig().getConfig("app.test.string");
        assertNotNull(config);
        assertEquals("été", config);
    }

    @Test
    public void freeKeyStringDefault() throws Exception {

        String config = getAppConfig().getConfig("nope", "yep");
        assertNotNull(config);
        assertEquals("yep", config);
    }

    @Test
    public void freeKeyStringException() throws Exception {

        try {
            getAppConfig().getConfig("nope");
            fail();
        } catch(Exception ex) {
        }
    }

    @Test
    public void freeKeyBoolean() throws Exception {

        Boolean config = getAppConfig().getConfigBoolean("app.test.boolean");
        assertNotNull(config);
        assertEquals(true, config);
    }

    @Test
    public void freeKeyBooleanDefault() throws Exception {

        Boolean config = getAppConfig().getConfigBoolean("nope", true);
        assertNotNull(config);
        assertEquals(true, config);
    }

    @Test
    public void freeKeyBooleanException() throws Exception {

        try {
            getAppConfig().getConfigBoolean("nope");
            fail();
        } catch(Exception ex) {
        }
    }

    @Test
    public void freeKeyInteger() throws Exception {

        Integer config = getAppConfig().getConfigInteger("app.test.integer");
        assertNotNull(config);
        assertEquals(new Integer(123), config);
    }

    @Test
    public void freeKeyIntegerDefault() throws Exception {

        Integer config = getAppConfig().getConfigInteger("nope", 42);
        assertNotNull(config);
        assertEquals(new Integer(42), config);
    }

    @Test
    public void freeKeyIntegerException() throws Exception {

        try {
            getAppConfig().getConfigInteger("nope");
            fail();
        } catch(Exception ex) {
        }
    }

}
