package org.spincast.plugins.jacksonxml;

import java.lang.reflect.Type;

import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.xml.IXmlManager;

import com.google.inject.Scopes;
import com.google.inject.multibindings.Multibinder;

public class SpincastJacksonXmlPluginGuiceModule extends SpincastGuiceModuleBase {

    private final Type requestContextType;

    /**
     * Constructor.
     */
    public SpincastJacksonXmlPluginGuiceModule(Type requestContextType) {
        this.requestContextType = requestContextType;
    }

    @Override
    protected Type getRequestContextType() {
        return this.requestContextType;
    }

    @Override
    protected void configure() {

        bindXmlManager();
        bindXmlMixinsMultiBinder();
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

}
