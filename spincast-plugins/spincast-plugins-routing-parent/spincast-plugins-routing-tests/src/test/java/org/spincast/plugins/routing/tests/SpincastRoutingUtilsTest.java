package org.spincast.plugins.routing.tests;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.spincast.defaults.tests.SpincastDefaultTestingModule;
import org.spincast.plugins.routing.utils.IReplaceDynamicParamsResult;
import org.spincast.plugins.routing.utils.ISpincastRoutingUtils;
import org.spincast.testing.core.SpincastTestBase;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

public class SpincastRoutingUtilsTest extends SpincastTestBase {

    @Override
    protected Injector createInjector() {
        return Guice.createInjector(new SpincastDefaultTestingModule());
    }

    @Inject
    protected ISpincastRoutingUtils spincastRoutingUtils;

    protected ISpincastRoutingUtils getSpincastRoutingUtils() {
        return this.spincastRoutingUtils;
    }

    @Test
    public void replaceDynamicParamsInPathNoDynParam() throws Exception {

        String path = "/test";
        Map<String, String> dynamicParams = new HashMap<String, String>();

        IReplaceDynamicParamsResult result = getSpincastRoutingUtils().replaceDynamicParamsInPath(path, dynamicParams);
        assertEquals("/test", result.getPath());
        assertEquals(false, result.isPlaceholdersRemaining());
    }

    @Test
    public void replaceDynamicParamsInPathNullPath() throws Exception {

        String path = null;
        Map<String, String> dynamicParams = new HashMap<String, String>();

        IReplaceDynamicParamsResult result = getSpincastRoutingUtils().replaceDynamicParamsInPath(path, dynamicParams);
        assertEquals(null, result.getPath());
        assertEquals(false, result.isPlaceholdersRemaining());
    }

    @Test
    public void replaceDynamicParamsInPathNullParam() throws Exception {

        String path = "/test";
        Map<String, String> dynamicParams = null;

        IReplaceDynamicParamsResult result = getSpincastRoutingUtils().replaceDynamicParamsInPath(path, dynamicParams);
        assertEquals("/test", result.getPath());
        assertEquals(false, result.isPlaceholdersRemaining());
    }

    @Test
    public void replaceDynamicParamsInPathNullPathNullParam() throws Exception {

        String path = null;
        Map<String, String> dynamicParams = null;

        IReplaceDynamicParamsResult result = getSpincastRoutingUtils().replaceDynamicParamsInPath(path, dynamicParams);
        assertEquals(null, result.getPath());
        assertEquals(false, result.isPlaceholdersRemaining());
    }

    @Test
    public void replaceDynamicParamsInPathOneDynParam() throws Exception {

        String path = "/${param1}";
        Map<String, String> dynamicParams = new HashMap<String, String>();
        dynamicParams.put("param1", "value1");

        IReplaceDynamicParamsResult result = getSpincastRoutingUtils().replaceDynamicParamsInPath(path, dynamicParams);
        assertEquals("/value1", result.getPath());
        assertEquals(false, result.isPlaceholdersRemaining());
    }

    @Test
    public void replaceDynamicParamsInPathOneSplatParam() throws Exception {

        String path = "/*{param1}";
        Map<String, String> dynamicParams = new HashMap<String, String>();
        dynamicParams.put("param1", "value1");

        IReplaceDynamicParamsResult result = getSpincastRoutingUtils().replaceDynamicParamsInPath(path, dynamicParams);
        assertEquals("/value1", result.getPath());
        assertEquals(false, result.isPlaceholdersRemaining());
    }

    @Test
    public void replaceDynamicParamsInPathOneDynParamRemaining() throws Exception {

        String path = "/${param1}";
        Map<String, String> dynamicParams = new HashMap<String, String>();
        dynamicParams.put("param2", "value2");

        IReplaceDynamicParamsResult result = getSpincastRoutingUtils().replaceDynamicParamsInPath(path, dynamicParams);
        assertEquals("/${param1}", result.getPath());
        assertEquals(true, result.isPlaceholdersRemaining());
    }

    @Test
    public void replaceDynamicParamsInPathTwoDynParam() throws Exception {

        String path = "/test/${param1}/${param2}";
        Map<String, String> dynamicParams = new HashMap<String, String>();
        dynamicParams.put("param1", "value1");
        dynamicParams.put("param2", "value2");

        IReplaceDynamicParamsResult result = getSpincastRoutingUtils().replaceDynamicParamsInPath(path, dynamicParams);
        assertEquals("/test/value1/value2", result.getPath());
        assertEquals(false, result.isPlaceholdersRemaining());
    }

    @Test
    public void replaceDynamicParamsInPathSplatFormatWorksToo() throws Exception {

        String path = "/test/*{param1}/*{param2}";
        Map<String, String> dynamicParams = new HashMap<String, String>();
        dynamicParams.put("param1", "value1");
        dynamicParams.put("param2", "value2");

        IReplaceDynamicParamsResult result = getSpincastRoutingUtils().replaceDynamicParamsInPath(path, dynamicParams);
        assertEquals("/test/value1/value2", result.getPath());
        assertEquals(false, result.isPlaceholdersRemaining());
    }

    @Test
    public void replaceDynamicParamsInPathOneDynParamRepeat() throws Exception {

        String path = "/test/${param1}/${param1}";
        Map<String, String> dynamicParams = new HashMap<String, String>();
        dynamicParams.put("param1", "value1");
        dynamicParams.put("param2", "value2");

        IReplaceDynamicParamsResult result = getSpincastRoutingUtils().replaceDynamicParamsInPath(path, dynamicParams);
        assertEquals("/test/value1/value1", result.getPath());
        assertEquals(false, result.isPlaceholdersRemaining());
    }

    @Test
    public void replaceDynamicParamsInPathPattern() throws Exception {

        String path = "/${param1:\\d+}";
        Map<String, String> dynamicParams = new HashMap<String, String>();
        dynamicParams.put("param1", "123");

        IReplaceDynamicParamsResult result = getSpincastRoutingUtils().replaceDynamicParamsInPath(path, dynamicParams);
        assertEquals("/123", result.getPath());
        assertEquals(false, result.isPlaceholdersRemaining());
    }

    @Test
    public void replaceDynamicParamsInPathPatternAlias() throws Exception {

        String path = "/${param1:<N>}";
        Map<String, String> dynamicParams = new HashMap<String, String>();
        dynamicParams.put("param1", "123");

        IReplaceDynamicParamsResult result = getSpincastRoutingUtils().replaceDynamicParamsInPath(path, dynamicParams);
        assertEquals("/123", result.getPath());
        assertEquals(false, result.isPlaceholdersRemaining());
    }

    @Test
    public void replaceDynamicParamsInPathMultiple() throws Exception {

        String path = "/test/${param1:<N>}/titi/${param2:\\d+}/${param3:\\d+}/${param3:\\d+}/test2";
        Map<String, String> dynamicParams = new HashMap<String, String>();
        dynamicParams.put("param1", "123");
        dynamicParams.put("param2", "456");
        dynamicParams.put("param3", "789");

        IReplaceDynamicParamsResult result = getSpincastRoutingUtils().replaceDynamicParamsInPath(path, dynamicParams);
        assertEquals("/test/123/titi/456/789/789/test2", result.getPath());
        assertEquals(false, result.isPlaceholdersRemaining());
    }

    @Test
    public void replaceDynamicParamsInPathInvalid() throws Exception {

        String path = "/${param1:}";
        Map<String, String> dynamicParams = new HashMap<String, String>();
        dynamicParams.put("param1", "123");

        IReplaceDynamicParamsResult result = getSpincastRoutingUtils().replaceDynamicParamsInPath(path, dynamicParams);
        assertEquals("/${param1:}", result.getPath());
        assertEquals(true, result.isPlaceholdersRemaining());
    }

    @Test
    public void replaceDynamicParamsInPathInvalid2() throws Exception {

        String path = "/${param1<coco>}";
        Map<String, String> dynamicParams = new HashMap<String, String>();
        dynamicParams.put("param1", "123");

        IReplaceDynamicParamsResult result = getSpincastRoutingUtils().replaceDynamicParamsInPath(path, dynamicParams);
        assertEquals("/${param1<coco>}", result.getPath());
        assertEquals(true, result.isPlaceholdersRemaining());
    }
}
