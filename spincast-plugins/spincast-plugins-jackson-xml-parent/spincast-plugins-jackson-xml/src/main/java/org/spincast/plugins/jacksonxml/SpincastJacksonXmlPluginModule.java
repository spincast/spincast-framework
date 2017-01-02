package org.spincast.plugins.jacksonxml;

import org.spincast.core.exchange.RequestContext;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.websocket.WebsocketContext;
import org.spincast.core.xml.XmlManager;

import com.fasterxml.jackson.dataformat.xml.XmlPrettyPrinter;
import com.fasterxml.jackson.dataformat.xml.util.DefaultXmlPrettyPrinter.Indenter;
import com.google.inject.Scopes;
import com.google.inject.multibindings.Multibinder;

public class SpincastJacksonXmlPluginModule extends SpincastGuiceModuleBase {

    public SpincastJacksonXmlPluginModule() {
        super();
    }

    public SpincastJacksonXmlPluginModule(Class<? extends RequestContext<?>> requestContextImplementationClass,
                                          Class<? extends WebsocketContext<?>> websocketContextImplementationClass) {
        super(requestContextImplementationClass, websocketContextImplementationClass);
    }

    @Override
    protected void configure() {

        bindXmlManager();
        bindXmlMixinsMultiBinder();

        binxCustomXmlPrettyPrinter();
        bindCustomXmlIndenter();
    }

    protected void bindXmlManager() {
        bind(XmlManager.class).to(getSpincastXmlManagerClass()).in(Scopes.SINGLETON);
    }

    protected Class<? extends XmlManager> getSpincastXmlManagerClass() {
        return SpincastXmlManager.class;
    }

    protected void bindXmlMixinsMultiBinder() {

        //==========================================
        // Create the XML mixin info multibinder so it's not null,
        // even if no mixin infos are added.
        //==========================================
        @SuppressWarnings("unused")
        Multibinder<XmlMixinInfo> uriBinder = Multibinder.newSetBinder(binder(), XmlMixinInfo.class);
    }

    protected void binxCustomXmlPrettyPrinter() {
        // Not singleton! Because of SpincastXmlPrettyPrinter#createInstance()
        bind(XmlPrettyPrinter.class).to(getXmlPrettyPrinterClass());
    }

    protected Class<? extends XmlPrettyPrinter> getXmlPrettyPrinterClass() {
        return SpincastXmlPrettyPrinter.class;
    }

    protected void bindCustomXmlIndenter() {
        bind(Indenter.class).to(getXmlIndenterClass()).in(Scopes.SINGLETON);
    }

    protected Class<? extends Indenter> getXmlIndenterClass() {
        return SpincastXmlIndenter.class;
    }

}
