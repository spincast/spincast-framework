package org.spincast.tests.varia;

import java.lang.reflect.Type;

import org.spincast.core.config.SpincastConfig;
import org.spincast.core.controllers.FrontController;
import org.spincast.core.controllers.SpincastFrontController;
import org.spincast.core.dictionary.Dictionary;
import org.spincast.core.exchange.DefaultRequestContext;
import org.spincast.core.exchange.RequestContextFactory;
import org.spincast.core.exchange.RequestContextType;
import org.spincast.core.guice.SpincastRequestScope;
import org.spincast.core.json.JsonManager;
import org.spincast.core.routing.Router;
import org.spincast.core.server.Server;
import org.spincast.core.websocket.DefaultWebsocketContext;
import org.spincast.core.xml.XmlManager;

import com.google.inject.Inject;

public class CustomFrontController extends SpincastFrontController<DefaultRequestContext, DefaultWebsocketContext>
                                   implements FrontController {

    @Inject
    public CustomFrontController(Router<DefaultRequestContext, DefaultWebsocketContext> router,
                                 SpincastConfig spincastConfig,
                                 Dictionary dictionary,
                                 Server server,
                                 RequestContextFactory<DefaultRequestContext> requestCreationFactory,
                                 SpincastRequestScope spincastRequestScope,
                                 @RequestContextType Type requestContextType,
                                 JsonManager jsonManager,
                                 XmlManager xmlManager) {
        super(router,
              spincastConfig,
              dictionary,
              server,
              requestCreationFactory,
              spincastRequestScope,
              requestContextType,
              jsonManager,
              xmlManager);
    }

    @Override
    protected Object validateExchange(Object exchange) {
        throw new RuntimeException("Exchange not valid!");
    }

}
