package org.spincast.plugins.watermarker;

import org.spincast.core.exchange.RequestContext;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.websocket.WebsocketContext;

import com.google.inject.assistedinject.FactoryModuleBuilder;

/**
 * Spincast Watermarker plugin module.
 */
public class SpincastWatermarkerPluginModule extends SpincastGuiceModuleBase {

    public SpincastWatermarkerPluginModule() {
        super();
    }

    public SpincastWatermarkerPluginModule(Class<? extends RequestContext<?>> requestContextImplementationClass,
                                           Class<? extends WebsocketContext<?>> websocketContextImplementationClass) {
        super(requestContextImplementationClass, websocketContextImplementationClass);
    }

    @Override
    protected void configure() {

        install(new FactoryModuleBuilder().implement(SpincastImageWatermarker.class,
                                                     getSpincastImageWatermarkerImpl())
                                          .implement(SpincastImageWatermarkerBuilder.class,
                                                     getSpincastImageWatermarkerBuilderImpl())
                                          .build(SpincastWatermarkerFactory.class));
    }

    protected Class<? extends SpincastImageWatermarker> getSpincastImageWatermarkerImpl() {
        return SpincastImageWatermarkerDefault.class;
    }

    protected Class<? extends SpincastImageWatermarkerBuilder> getSpincastImageWatermarkerBuilderImpl() {
        return SpincastImageWatermarkerBuilderDefault.class;
    }

}
