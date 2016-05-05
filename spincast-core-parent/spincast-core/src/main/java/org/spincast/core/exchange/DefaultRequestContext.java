package org.spincast.core.exchange;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

/**
 * The default implementation of the request context.
 * 
 * We leave this implementation in the "spincast-core" artifact instead
 * of the "spincast-default" artifact because we want to be able to
 * use the SpincastCoreGuiceModule <i>as is</i> when creating a
 * Guice context. For example :
 * 
 * Injector guice = Guice.createInjector(new SpincastCoreGuiceModule(args),
 *                                       new AnotherModule())
 *                                       
 * So SpincastCoreGuiceModule can't be <code>abstract</code> and
 * therefore we have to provide this method implementation :
 * 
 *  protected Key<?> getRequestContextImplementationClass() {
 *      return Key.get(DefaultRequestContext.class);
 *  }
 *  
 *  Some may want to start their application fron the  "spincast-core" artifact
 *  but with the default request context.
 */
public class DefaultRequestContext extends RequestContextBase<IDefaultRequestContext>
                                   implements IDefaultRequestContext {

    protected final Logger logger = LoggerFactory.getLogger(DefaultRequestContext.class);

    @AssistedInject
    public DefaultRequestContext(@Assisted Object exchange) {
        super(exchange);
    }

}
