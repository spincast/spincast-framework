package org.spincast.plugins.undertow.tests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;

import org.junit.Test;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.server.Server;
import org.spincast.plugins.undertow.SpincastUndertowServer;
import org.spincast.plugins.undertow.config.SpincastUndertowConfig;
import org.spincast.plugins.undertow.config.SpincastUndertowConfigDefault;
import org.spincast.testing.defaults.NoAppStartHttpServerTestingBase;

import com.google.inject.Module;
import com.google.inject.Scopes;
import com.google.inject.util.Modules;

import io.undertow.server.handlers.LearningPushHandler;

public class Http2LearningPushHandlerEnabledTest extends NoAppStartHttpServerTestingBase {

    public static class TestingSpincastConfig extends SpincastUndertowConfigDefault {

        @Override
        public boolean isEnableLearningPushHandler() {
            return true;
        }
    }

    @Override
    protected Module getExtraOverridingModule() {
        return Modules.override(super.getExtraOverridingModule()).with(new SpincastGuiceModuleBase() {

            @Override
            protected void configure() {
                bind(SpincastUndertowConfig.class).to(TestingSpincastConfig.class).in(Scopes.SINGLETON);
            }
        });
    }

    @Test
    public void learningPushHandlerEnabled() throws Exception {

        Server server = getServer();
        assertTrue(server instanceof SpincastUndertowServer);
        SpincastUndertowServer spincastUndertowServer = (SpincastUndertowServer)server;

        Field learningPushHandlerField = SpincastUndertowServer.class.getDeclaredField("learningPushHandler");
        learningPushHandlerField.setAccessible(true);
        LearningPushHandler learningPushHandler = (LearningPushHandler)learningPushHandlerField.get(spincastUndertowServer);
        assertNotNull(learningPushHandler);
    }

}
