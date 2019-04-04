package org.spincast.demos.supercalifragilisticexpialidocious;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.config.SpincastConfig;
import org.spincast.core.server.Server;
import org.spincast.defaults.bootstrapping.Spincast;
import org.spincast.plugins.httpclient.SpincastHttpClientPlugin;

import com.google.inject.Inject;

public class App {

    protected static final Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        Spincast.configure()
                .module(new AppModule())
                .plugin(new SpincastHttpClientPlugin())
                .requestContextImplementationClass(AppRequestContextDefault.class)
                .init(args);
    }

    @Inject
    protected void init(Server server, SpincastConfig spincastConfig) {
        server.start();
        logger.info("Application started on port " + spincastConfig.getHttpServerPort() + "!");
        logger.info("Try this route : http://localhost:" + spincastConfig.getHttpServerPort() + "/github-source/spincast");
    }
}
