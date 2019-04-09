package org.spincast.test;

import java.io.File;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.UUID;

import org.spincast.core.config.SpincastConfig;
import org.spincast.core.guice.MainArgs;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.guice.TestingMode;
import org.spincast.core.server.Server;
import org.spincast.core.utils.SpincastUtils;
import org.spincast.defaults.bootstrapping.Spincast;
import org.spincast.plugins.config.SpincastConfigDefault;
import org.spincast.plugins.config.SpincastConfigPluginConfig;
import org.spincast.plugins.routing.DefaultRouter;

import com.google.inject.BindingAnnotation;
import com.google.inject.Inject;
import com.google.inject.Scopes;


public class App {
    
    public static void main(String[] args) {

        int port = Integer.parseInt(args[0]);

        Spincast.configure()
                .module(new SpincastGuiceModuleBase() {

                    @Override
                    protected void configure() {
                        bind(Integer.class).annotatedWith(TestServerPort.class).toInstance(port);
                        bind(SpincastConfig.class).to(AppConfig.class).in(Scopes.SINGLETON);
                    }
                }).init(args);
    }
    
    @Target({ElementType.TYPE, ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @BindingAnnotation
    public static @interface TestServerPort {
    }

    public static class AppConfig extends SpincastConfigDefault {

        private final int port;

        @Inject
        protected AppConfig(SpincastConfigPluginConfig spincastConfigPluginConfig,
                            @TestingMode boolean testingMode,
                            @TestServerPort int port) {
            super(spincastConfigPluginConfig, testingMode);
            this.port = port;
        }

        @Override
        public int getHttpServerPort() {
            return this.port;
        }

        @Override
        public String getPublicUrlBase() {
            return "http://localhost:" + this.port;
        }
        
        @Override
        public File getWritableRootDir() {
            File writable = new File(super.getWritableRootDir(), UUID.randomUUID().toString());
            writable.mkdirs();
            return writable;
        }
        
    }

    @Inject
    protected void init(Server server,
                        DefaultRouter router,
                        SpincastUtils spincastUtils,
                        @MainArgs String[] args) {

        router.GET("/").handle((context) -> {
            context.response().sendPlainText("Hello " + args[1]);
        });

        server.start();
    }
}
