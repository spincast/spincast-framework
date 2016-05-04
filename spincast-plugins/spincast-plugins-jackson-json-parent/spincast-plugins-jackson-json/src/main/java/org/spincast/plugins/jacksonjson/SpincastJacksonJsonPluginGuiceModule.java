package org.spincast.plugins.jacksonjson;

import java.lang.reflect.Type;

import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.json.IJsonManager;

import com.google.inject.Scopes;
import com.google.inject.multibindings.Multibinder;

public class SpincastJacksonJsonPluginGuiceModule extends SpincastGuiceModuleBase {

    private final Type requestContextType;

    /**
     * Constructor.
     */
    public SpincastJacksonJsonPluginGuiceModule(Type requestContextType) {
        this.requestContextType = requestContextType;
    }

    @Override
    protected Type getRequestContextType() {
        return this.requestContextType;
    }

    @Override
    protected void configure() {

        bindJsonManager();
        bindJsonMixinsMultiBinder();

    }

    protected void bindJsonManager() {
        bind(IJsonManager.class).to(getSpincastJsonManager()).in(Scopes.SINGLETON);
    }

    protected Class<? extends IJsonManager> getSpincastJsonManager() {
        return SpincastJsonManager.class;
    }

    protected void bindJsonMixinsMultiBinder() {

        //==========================================
        // Create the Json mixin info multibinder so it's not null,
        // even if no mixin infos are added.
        //==========================================
        @SuppressWarnings("unused")
        Multibinder<IJsonMixinInfo> uriBinder = Multibinder.newSetBinder(binder(), IJsonMixinInfo.class);
    }

}
