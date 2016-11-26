package org.spincast.core.websocket;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.guice.SpincastGuiceScopes;
import org.spincast.core.guice.SpincastRequestScoped;
import org.spincast.core.json.JsonManager;
import org.spincast.core.locale.LocaleResolver;
import org.spincast.core.templating.TemplatingEngine;
import org.spincast.core.xml.XmlManager;

import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Scopes;
import com.google.inject.Singleton;

/**
 * The base implementation for a WebSocket context object.
 */
public abstract class WebsocketContextBase<W extends WebsocketContext<?>> {

    protected final Logger logger = LoggerFactory.getLogger(WebsocketContextBase.class);

    private final String endpointId;
    private final String peerId;
    private final WebsocketPeerManager peerManager;
    private final LocaleResolver localeResolver;
    private final JsonManager jsonManager;
    private final XmlManager xmlManager;
    private final TemplatingEngine templatingEngine;
    private final Provider<Injector> injectorProvider;

    private Map<Key<?>, Object> instanceFromGuiceCache;

    /**
     * Constructor
     */
    public WebsocketContextBase(String endpointId,
                                String peerId,
                                WebsocketPeerManager peerManager,
                                WebsocketContextBaseDeps<W> deps) {
        this.endpointId = endpointId;
        this.peerId = peerId;
        this.peerManager = peerManager;
        this.localeResolver = deps.getLocaleResolver();
        this.jsonManager = deps.getJsonManager();
        this.xmlManager = deps.getXmlManager();
        this.templatingEngine = deps.getTemplatingEngine();
        this.injectorProvider = deps.getInjectorProvider();
    }

    public WebsocketPeerManager peerManager() {
        return this.peerManager;
    }

    public String getEndpointId() {
        return this.endpointId;
    }

    public String getPeerId() {
        return this.peerId;
    }

    public void sendMessageToCurrentPeer(String message) {
        peerManager().sendMessage(message);
    }

    public void sendMessageToCurrentPeer(byte[] bytes) {
        peerManager().sendMessage(bytes);
    }

    public void closeConnectionWithCurrentPeer() {
        peerManager().closeConnection();
    }

    public JsonManager json() {
        return this.jsonManager;
    }

    public XmlManager xml() {
        return this.xmlManager;
    }

    public TemplatingEngine templating() {
        return this.templatingEngine;
    }

    protected Map<Key<?>, Object> getInstanceFromGuiceCache() {
        if(this.instanceFromGuiceCache == null) {
            this.instanceFromGuiceCache = new HashMap<Key<?>, Object>();
        }
        return this.instanceFromGuiceCache;
    }

    public Injector guice() {
        return this.injectorProvider.get();
    }

    public <T> T get(Class<T> clazz) {
        Objects.requireNonNull(clazz, "clazz can't be NULL");
        return get(Key.get(clazz));
    }

    public <T> T get(Key<T> key) {
        Objects.requireNonNull(key, "key can't be NULL");

        T obj = null;
        Map<Key<?>, Object> cache = getInstanceFromGuiceCache();
        if(!cache.containsKey(key)) {

            obj = guice().getInstance(key);

            //==========================================
            // We only cache the request scoped objects, and the 
            // singletons!
            //==========================================
            Binding<T> binding = guice().getBinding(key);
            if(Scopes.isScoped(binding, SpincastGuiceScopes.REQUEST, SpincastRequestScoped.class) ||
               Scopes.isScoped(binding, Scopes.SINGLETON, Singleton.class)) {
                cache.put(key, obj);
            }
        } else {
            @SuppressWarnings("unchecked")
            T asT = (T)cache.get(key);
            obj = asT;
        }
        return obj;
    }

    protected LocaleResolver getLocaleResolver() {
        return this.localeResolver;
    }

    public Locale getLocaleToUse() {
        return getLocaleResolver().getLocaleToUse();
    }

}
