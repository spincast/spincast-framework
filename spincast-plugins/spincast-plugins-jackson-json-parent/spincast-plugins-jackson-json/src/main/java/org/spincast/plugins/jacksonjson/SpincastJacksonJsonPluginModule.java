package org.spincast.plugins.jacksonjson;

import org.spincast.core.exchange.RequestContext;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.json.JsonManager;
import org.spincast.core.json.JsonPathPathUtilsDefault;
import org.spincast.core.json.JsonPathUtils;
import org.spincast.core.websocket.WebsocketContext;

import com.google.inject.Scopes;
import com.google.inject.multibindings.Multibinder;

public class SpincastJacksonJsonPluginModule extends SpincastGuiceModuleBase {

    public SpincastJacksonJsonPluginModule() {
        super();
    }

    public SpincastJacksonJsonPluginModule(Class<? extends RequestContext<?>> requestContextImplementationClass,
                                           Class<? extends WebsocketContext<?>> websocketContextImplementationClass) {
        super(requestContextImplementationClass, websocketContextImplementationClass);
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
