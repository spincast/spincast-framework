package org.spincast.plugins.externalconfig.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.spincast.core.config.SpincastConfig;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.guice.TestingMode;
import org.spincast.core.utils.ObjectConverter;
import org.spincast.plugins.config.SpincastConfigDefault;
import org.spincast.plugins.config.SpincastConfigPluginConfig;
import org.spincast.plugins.config.SpincastConfigPluginConfigDefault;

import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.Scopes;

public class ConfigTypesTest extends ConfigTestingBase {

    @Override
    protected Class<? extends SpincastConfig> getTestingConfigImplementationClass2() {
        return AppConfigDefault.class;
    }

    @Override
    protected Module getExtraOverridingModule() {
        return new SpincastGuiceModuleBase() {

            @Override
            protected void configure() {
                bind(SpincastConfigPluginConfig.class).to(AppSpincastConfigPluginConfig.class)
                                                      .in(Scopes.SINGLETON);
                bind(AppConfig.class).to(AppConfigDefault.class).in(Scopes.SINGLETON);
            }
        };
    }

    protected static class AppSpincastConfigPluginConfig extends SpincastConfigPluginConfigDefault {

        //==========================================
        // Custom classpath file name
        //==========================================
        @Override
        public String getClasspathFilePath() {
            return "other-config-file.yaml";
        }
    }

    @Inject
    private AppConfig appConfig;

    @Inject
    protected ObjectConverter objectConverter;

    protected AppConfig getAppConfig() {
        return this.appConfig;
    }

    protected ObjectConverter getObjectConverter() {
        return this.objectConverter;
    }

    public static interface AppConfig extends SpincastConfig {

        public String getTestString();

        public String getTestStringNotFound();

        public String getTestStringNotFoundDefaultValue();

        public List<String> getTestListStrings();

        public Boolean getTestBoolean();

        public Boolean getTestBooleanNotFound();

        public Boolean getTestBooleanNotFoundDefaultValue();

        public Integer getTestInteger();

        public Integer getTestIntegerNotFound();

        public Integer getTestIntegerNotFoundDefaultValue();

        public List<Integer> getTestListIntegers();

        public BigDecimal getTestBigDecimal();

        public Date getTestDate();

        public List<Date> getTestListDates();

        public List<Long> getTestListLongs();

        public List<BigDecimal> getTestListBigDecimal();

        public List<Boolean> getTestListBoolean();

    }

    public static class AppConfigDefault extends SpincastConfigDefault implements AppConfig {

        @Inject
        public AppConfigDefault(SpincastConfigPluginConfig spincastConfigPluginConfig, @TestingMode boolean testingMode) {
            super(spincastConfigPluginConfig, testingMode);
        }

        @Override
        public int getHttpServerPort() {
            return getInteger("server.http.port");
        }

        @Override
        public String getTestString() {
            return getString("app.test.string");
        }

        @Override
        public String getTestStringNotFound() {
            return getString("app.nope");
        }

        @Override
        public String getTestStringNotFoundDefaultValue() {
            return getString("app.nope", "some default value");
        }

        @Override
        public Boolean getTestBoolean() {
            return getBoolean("app.test.boolean");
        }

        @Override
        public Boolean getTestBooleanNotFound() {
            return getBoolean("app.nope");
        }

        @Override
        public Boolean getTestBooleanNotFoundDefaultValue() {
            return getBoolean("app.nope", false);
        }

        @Override
        public Integer getTestInteger() {
            return getInteger("app.test.integer");
        }

        @Override
        public Integer getTestIntegerNotFound() {
            return getInteger("app.nope");
        }

        @Override
        public Integer getTestIntegerNotFoundDefaultValue() {
            return getInteger("app.nope", 42);
        }

        @Override
        public boolean isDevelopmentMode() {
            return false;
        }

        @Override
        public BigDecimal getTestBigDecimal() {
            return getBigDecimal("app.test.integer");
        }

        @Override
        public Date getTestDate() {
            return getDate("app.test.date");
        }

        @Override
        public List<Date> getTestListDates() {
            return getDateList("app.test.dates");
        }

        @Override
        public List<String> getTestListStrings() {
            return getStringList("app.test.strings");
        }

        @Override
        public List<Integer> getTestListIntegers() {
            return getIntegerList("app.test.numbers");
        }

        @Override
        public List<Long> getTestListLongs() {
            return getLongList("app.test.numbers");
        }

        @Override
        public List<BigDecimal> getTestListBigDecimal() {
            return getBigDecimalList("app.test.numbers");
        }

        @Override
        public List<Boolean> getTestListBoolean() {
            return getBooleanList("app.test.bools");
        }
    }

    @Test
    public void testDefaultConfigOverriden() throws Exception {

        assertNotNull(getSpincastConfig().getHttpServerPort());
        assertEquals(55555, getSpincastConfig().getHttpServerPort());

        assertNotNull(getAppConfig().getHttpServerPort());
        assertEquals(55555, getAppConfig().getHttpServerPort());
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
        assertEquals("L'éléphant et le bœuf", getAppConfig().getTestString());
    }

    @Test
    public void testCustomConfigStringNotFound() throws Exception {

        String res = getAppConfig().getTestStringNotFound();
        assertNull(res);
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
        assertNull(getAppConfig().getTestBooleanNotFound());
    }

    @Test
    public void testCustomConfigBooleanDefaultValue() throws Exception {

        assertNotNull(getAppConfig().getTestBooleanNotFoundDefaultValue());
        assertEquals(false, getAppConfig().getTestBooleanNotFoundDefaultValue());
    }

    @Test
    public void testCustomConfigInteger() throws Exception {

        assertNotNull(getAppConfig().getTestInteger());
        assertEquals(Integer.valueOf(456), getAppConfig().getTestInteger());
    }

    @Test
    public void testCustomConfigIntegerNotFound() throws Exception {
        assertNull(getAppConfig().getTestIntegerNotFound());
    }

    @Test
    public void testCustomConfigIntegerDefaultValue() throws Exception {

        assertNotNull(getAppConfig().getTestIntegerNotFoundDefaultValue());
        assertEquals(Integer.valueOf(42), getAppConfig().getTestIntegerNotFoundDefaultValue());
    }

    @Test
    public void getBigDecimal() throws Exception {
        BigDecimal config = getAppConfig().getTestBigDecimal();
        assertNotNull(config);
        assertEquals(new BigDecimal("456"), config);
    }

    @Test
    public void getDate() throws Exception {
        Date config = getAppConfig().getTestDate();
        assertNotNull(config);

        String asString = getObjectConverter().convertToJsonDateFormat(config);
        assertEquals("2012-04-23T18:25:43.511Z", asString);
    }

    @Test
    public void getDateList() throws Exception {
        List<Date> config = getAppConfig().getTestListDates();
        assertNotNull(config);
        assertEquals(7, config.size());
        assertEquals("2013-04-23T00:00:00.000Z", getObjectConverter().convertToJsonDateFormat(config.get(0)));
        assertEquals("2013-04-23T18:25:00.000Z", getObjectConverter().convertToJsonDateFormat(config.get(1)));
        assertEquals("2014-04-23T18:25:43.000Z", getObjectConverter().convertToJsonDateFormat(config.get(2)));
        assertEquals("2015-04-23T18:25:43.000Z", getObjectConverter().convertToJsonDateFormat(config.get(3)));
        assertEquals("2016-04-23T18:25:43.000Z", getObjectConverter().convertToJsonDateFormat(config.get(4)));
        assertEquals("2016-04-23T18:25:43.123Z", getObjectConverter().convertToJsonDateFormat(config.get(5)));
        assertEquals("2016-04-23T18:25:43.123Z", getObjectConverter().convertToJsonDateFormat(config.get(6)));
    }

    @Test
    public void getStringList() throws Exception {
        List<String> config = getAppConfig().getTestListStrings();
        assertNotNull(config);
        assertEquals(3, config.size());
        assertEquals("aaa", config.get(0));
        assertEquals("bbb", config.get(1));
        assertEquals("ccc", config.get(2));
    }

    @Test
    public void getIntegerList() throws Exception {
        List<Integer> config = getAppConfig().getTestListIntegers();
        assertNotNull(config);
        assertEquals(3, config.size());
        assertEquals(Integer.valueOf(111), config.get(0));
        assertEquals(Integer.valueOf(222), config.get(1));
        assertEquals(Integer.valueOf(333), config.get(2));
    }

    @Test
    public void getLongList() throws Exception {
        List<Long> config = getAppConfig().getTestListLongs();
        assertNotNull(config);
        assertEquals(3, config.size());
        assertEquals(Long.valueOf(111), config.get(0));
        assertEquals(Long.valueOf(222), config.get(1));
        assertEquals(Long.valueOf(333), config.get(2));
    }

    @Test
    public void getBigDecimalList() throws Exception {
        List<BigDecimal> config = getAppConfig().getTestListBigDecimal();
        assertNotNull(config);
        assertEquals(3, config.size());
        assertEquals(new BigDecimal(111), config.get(0));
        assertEquals(new BigDecimal(222), config.get(1));
        assertEquals(new BigDecimal(333), config.get(2));
    }

    @Test
    public void getBooleanList() throws Exception {
        List<Boolean> config = getAppConfig().getTestListBoolean();
        assertNotNull(config);
        assertEquals(3, config.size());
        assertTrue(config.get(0));
        assertFalse(config.get(1));
        assertTrue(config.get(2));
    }
}
