package org.spincast.defaults.testing.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.spincast.defaults.testing.IntegrationTestNoAppDefaultContextsBase;
import org.spincast.plugins.httpclient.websocket.SpincastHttpClientWithWebsocketPluginModule;

import com.google.inject.Module;

public class ITNoAppGuiceTweakerDisabledTest extends IntegrationTestNoAppDefaultContextsBase {

    @Override
    protected boolean isEnableGuiceTweaker() {
        return false;
    }

    @Override
    protected Module getExtraOverridingModule() {

        //==========================================
        // The Http Client with Websocket module is required 
        // for integration testing. Since we disabled
        // the Guice Tweaker, we have to add this module
        // by ourself.
        //==========================================
        return new SpincastHttpClientWithWebsocketPluginModule();
    }

    //==========================================
    // The configs should be the default, 
    // non testing, ones.
    //==========================================
    @Test
    public void test() throws Exception {
        assertNotNull(getSpincastConfig());
        assertEquals(44419, getSpincastConfig().getHttpServerPort());
        assertTrue(getSpincastConfig().isDebugEnabled());
    }

}
