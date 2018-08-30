package org.spincast.core.guice;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.spincast.core.exchange.DefaultRequestContextDefault;
import org.spincast.core.exchange.RequestContext;
import org.spincast.core.websocket.DefaultWebsocketContextDefault;
import org.spincast.core.websocket.WebsocketContext;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.util.Types;

/**
 * Base class for a Spincast Guice module.
 */
public abstract class SpincastGuiceModuleBase extends AbstractModule implements SpincastContextTypesInterested {

    private Class<? extends RequestContext<?>> requestContextInterface;
    private Class<? extends WebsocketContext<?>> websocketContextInterface;

    private Class<? extends RequestContext<?>> requestContextImplementationClass;
    private Class<? extends WebsocketContext<?>> websocketContextImplementationClass;

    public SpincastGuiceModuleBase() {
        this(DefaultRequestContextDefault.class, DefaultWebsocketContextDefault.class);
    }

    public SpincastGuiceModuleBase(Class<? extends RequestContext<?>> requestContextImplementationClass,
                                   Class<? extends WebsocketContext<?>> websocketContextImplementationClass) {
        this.requestContextImplementationClass = requestContextImplementationClass;
        this.websocketContextImplementationClass = websocketContextImplementationClass;
    }

    protected Class<? extends RequestContext<?>> getRequestContextImplementationClass() {
        return this.requestContextImplementationClass;
    }

    protected Class<? extends WebsocketContext<?>> getWebsocketContextImplementationClass() {
        return this.websocketContextImplementationClass;
    }

    @Override
    public void setRequestContextImplementationClass(Class<? extends RequestContext<?>> requestContextImplementationClass) {
        this.requestContextImplementationClass = requestContextImplementationClass;
    }

    @Override
    public void setWebsocketContextImplementationClass(Class<? extends WebsocketContext<?>> websocketContextImplementationClass) {
        this.websocketContextImplementationClass = websocketContextImplementationClass;
    }

    protected Class<? extends RequestContext<?>> getRequestContextInterface() {
        if (this.requestContextInterface == null) {

            //==========================================
            // We extract the Request Context interface
            // from the implementation class
            //==========================================
            Key<? extends RequestContext<?>> key = Key.get(getRequestContextImplementationClass());

            TypeLiteral<?> requestContextTypeLiteral = key.getTypeLiteral().getSupertype(RequestContext.class);
            @SuppressWarnings("unchecked")
            Class<? extends RequestContext<?>> temp =
                    (Class<? extends RequestContext<?>>)((ParameterizedType)requestContextTypeLiteral.getType()).getActualTypeArguments()[0];
            this.requestContextInterface = temp;
        }
        return this.requestContextInterface;
    }

    protected Type getWebsocketContextInterface() {
        if (this.websocketContextInterface == null) {

            //==========================================
            // We extract the Websocket Context interface
            // from the implementation class
            //==========================================
            Key<? extends WebsocketContext<?>> key = Key.get(getWebsocketContextImplementationClass());

            TypeLiteral<?> websocketContextTypeLiteral = key.getTypeLiteral().getSupertype(WebsocketContext.class);
            @SuppressWarnings("unchecked")
            Class<? extends WebsocketContext<?>> temp =
                    (Class<? extends WebsocketContext<?>>)((ParameterizedType)websocketContextTypeLiteral.getType()).getActualTypeArguments()[0];
            this.websocketContextInterface = temp;
        }
        return this.websocketContextInterface;
    }

    /**
     * Get a version of the specified class parameterized with the
     * request context type.
     */
    @SuppressWarnings("unchecked")
    protected <T> Key<T> parameterizeWithRequestContext(Class<T> clazz) {
        return (Key<T>)Key.get(Types.newParameterizedType(clazz, getRequestContextInterface()));
    }

    /**
     * Get a version of the specified class parameterized with the
     * Websocket context type.
     */
    @SuppressWarnings("unchecked")
    protected <T> Key<T> parameterizeWithWebsocketContext(Class<T> clazz) {
        return (Key<T>)Key.get(Types.newParameterizedType(clazz, getWebsocketContextInterface()));
    }

    /**
     * Get a version of the specified class parameterized with the
     * request and Websocket context types.
     */
    @SuppressWarnings("unchecked")
    protected <T> Key<T> parameterizeWithContextInterfaces(Class<T> clazz) {
        return (Key<T>)Key.get(Types.newParameterizedType(clazz, getRequestContextInterface(), getWebsocketContextInterface()));
    }

    @Override
    protected void install(Module module) {

        //==========================================
        // Set the Request Context type and the Websocket Context type
        // on SpincastContextTypesInterested modules...
        //==========================================
        if (module instanceof SpincastContextTypesInterested) {
            ((SpincastContextTypesInterested)module).setRequestContextImplementationClass(getRequestContextImplementationClass());
            ((SpincastContextTypesInterested)module).setWebsocketContextImplementationClass(getWebsocketContextImplementationClass());
        }

        super.install(module);
    }

    @Override
    protected abstract void configure();

}
