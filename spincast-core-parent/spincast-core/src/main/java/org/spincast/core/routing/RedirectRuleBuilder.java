package org.spincast.core.routing;

import org.spincast.core.exchange.RequestContext;
import org.spincast.core.websocket.WebsocketContext;

/**
 * Builder to create a redirection rule.
 */
public interface RedirectRuleBuilder<R extends RequestContext<?>, W extends WebsocketContext<?>> {

    /**
     * The redirection will be permanent (301).
     * <p>
     * This is the default.
     */
    public RedirectRuleBuilder<R, W> permanently();

    /**
     * The redirection will be temporarily (302).
     */
    public RedirectRuleBuilder<R, W> temporarily();

    /**
     * The new path or full URL to redirect to.
     * <p>
     * This ends the creation of the redirection rule and
     * save it to the router.
     */
    public void to(String newPathOrFullUrl);

    /**
     * Will called the specified {@link RedirectHandler}
     * to generate the path to redirect to.
     * <p>
     * This ends the creation of the redirection rule and
     * save it to the router.
     */
    public void to(RedirectHandler<R, W> handler);

    /**
     * The position where the filter will be run.
     * <p>
     * By using a position below <code>0</code>, the
     * redirection will occures even if a main handler would
     * be found.
     *
     * <p>
     * Defaults to -1000.
     */
    public RedirectRuleBuilder<R, W> pos(int position);


}
