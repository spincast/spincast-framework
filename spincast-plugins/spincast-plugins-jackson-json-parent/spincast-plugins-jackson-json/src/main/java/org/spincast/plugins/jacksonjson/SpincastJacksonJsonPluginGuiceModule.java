package org.spincast.plugins.jacksonjson;

import java.lang.reflect.Type;

import org.spincast.core.guice.SpincastPluginGuiceModuleBase;
import org.spincast.core.json.JsonManager;
import org.spincast.core.json.JsonPathUtils;
import org.spincast.core.json.JsonPathPathUtilsDefault;

import com.google.inject.Scopes;
import com.google.inject.multibindings.Multibinder;

public class SpincastJacksonJsonPluginGuiceModule extends SpincastPluginGuiceModuleBase {

    /**
     * Constructor.
     */
    public SpincastJacksonJsonPluginGuiceModule(Type requestContextType, Type websocketContextType) {
        super(requestContextType, websocketContextType);
    }

    @Override
    protected void configure() {

        bindJsonManager();
        bindJsonPathUtils();
        bindJsonMixinsMultiBinder();
    }

    protected void bindJsonManager() {
        bind(JsonManager.class).to(getSpincastJsonManager()).in(Scopes.SINGLETON);
    }

    protected Class<? extends JsonManager> getSpincastJsonManager() {
        return SpincastJsonManager.class;
    }

    protected void bindJsonPathUtils() {
        bind(JsonPathUtils.class).to(getJsonPathUtilsImplClass()).in(Scopes.SINGLETON);
    }

    protected Class<? extends JsonPathUtils> getJsonPathUtilsImplClass() {
        return JsonPathPathUtilsDefault.class;
    }

    protected void bindJsonMixinsMultiBinder() {

        //==========================================
        // Create the Json mixin info multibinder so it's not null,
        // even if no mixin infos are added.
        //==========================================
        @SuppressWarnings("unused")
        Multibinder<JsonMixinInfo> uriBinder = Multibinder.newSetBinder(binder(), JsonMixinInfo.class);
    }

}
