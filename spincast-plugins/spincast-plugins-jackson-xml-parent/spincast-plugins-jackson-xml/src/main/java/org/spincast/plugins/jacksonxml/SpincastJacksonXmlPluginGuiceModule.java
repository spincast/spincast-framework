package org.spincast.plugins.jacksonxml;

import java.lang.reflect.Type;

import org.spincast.core.guice.SpincastPluginGuiceModuleBase;
import org.spincast.core.xml.IXmlManager;

import com.fasterxml.jackson.dataformat.xml.XmlPrettyPrinter;
import com.fasterxml.jackson.dataformat.xml.util.DefaultXmlPrettyPrinter.Indenter;
import com.google.inject.Scopes;
import com.google.inject.multibindings.Multibinder;

public class SpincastJacksonXmlPluginGuiceModule extends SpincastPluginGuiceModuleBase {

    /**
     * Constructor.
     */
    public SpincastJacksonXmlPluginGuiceModule(Type requestContextType,
                                               Type websocketContextType) {
        super(requestContextType, websocketContextType);
    }

    @Override
    protected void configure() {

        bindXmlManager();
        bindXmlMixinsMultiBinder();

        binxCustomXmlPrettyPrinter();
        bindCustomXmlIndenter();
    }

    protected void bindXmlManager() {
        bind(IXmlManager.class).to(getSpincastXmlManagerClass()).in(Scopes.SINGLETON);
    }

    protected Class<? extends IXmlManager> getSpincastXmlManagerClass() {
        return SpincastXmlManager.class;
    }

    protected void bindXmlMixinsMultiBinder() {

        //==========================================
        // Create the XML mixin info multibinder so it's not null,
        // even if no mixin infos are added.
        //==========================================
        @SuppressWarnings("unused")
        Multibinder<IXmlMixinInfo> uriBinder = Multibinder.newSetBinder(binder(), IXmlMixinInfo.class);
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
