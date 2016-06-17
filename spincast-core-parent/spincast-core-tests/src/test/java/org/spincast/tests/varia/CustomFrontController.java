package org.spincast.tests.varia;

import java.lang.reflect.Type;

import org.spincast.core.config.ISpincastConfig;
import org.spincast.core.config.ISpincastDictionary;
import org.spincast.core.controllers.IFrontController;
import org.spincast.core.controllers.SpincastFrontController;
import org.spincast.core.exchange.IDefaultRequestContext;
import org.spincast.core.exchange.IRequestContextFactory;
import org.spincast.core.exchange.RequestContextType;
import org.spincast.core.guice.SpincastRequestScope;
import org.spincast.core.routing.IRouter;
import org.spincast.core.server.IServer;
import org.spincast.core.websocket.IDefaultWebsocketContext;

import com.google.inject.Inject;

public class CustomFrontController extends SpincastFrontController<IDefaultRequestContext, IDefaultWebsocketContext>
                                   implements IFrontController {

    @Inject
    public CustomFrontController(IRouter<IDefaultRequestContext, IDefaultWebsocketContext> router,
                                 ISpincastConfig spincastConfig,
                                 ISpincastDictionary spincastDictionary,
                                 IServer server,
                                 IRequestContextFactory<IDefaultRequestContext> requestCreationFactory,
                                 SpincastRequestScope spincastRequestScope,
                                 @RequestContextType Type requestContextType) {
        super(router,
              spincastConfig,
              spincastDictionary,
              server,
              requestCreationFactory,
              spincastRequestScope,
              requestContextType);
    }

    @Override
    protected Object validateExchange(Object exchange) {
        throw new RuntimeException("Exchange not valid!");
    }

}
