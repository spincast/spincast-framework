package org.spincast.core.routing;

import org.spincast.core.config.SpincastConfig;
import org.spincast.core.exchange.RequestContext;
import org.spincast.core.websocket.WebsocketContext;

import com.google.inject.Inject;

/**
 * Component that binds some default route parameter aliases.
 */
public class DefaultRouteParamAliasesBinder<R extends RequestContext<?>, W extends WebsocketContext<?>> {

    /**
     * Init : binds the aliases.
     */
    @Inject
    protected void init(SpincastConfig spincastConfig,
                        Router<R, W> router) {

        boolean insensitive = !spincastConfig.isRoutesCaseSensitive();

        bindAlphaAlias(insensitive, router);
        bindNumericAlias(insensitive, router);
        bindAlphaNumericAlias(insensitive, router);
        bindAlphaPlusAlias(insensitive, router);
        bindNumericPlusAlias(insensitive, router);
        bindAlphaNumericPlusAlias(insensitive, router);

    }

    /**
     * Alpha alias
     */
    public String getAlphaAliasKey() {
        return "A";
    }

    protected void bindAlphaAlias(boolean insensitive, Router<R, W> router) {
        router.addRouteParamPatternAlias(getAlphaAliasKey(),
                                         (insensitive ? "(?i)" : "") + "[a-z]+");
    }

    /**
     * Numeric alias
     */
    public String geNumericAliasKey() {
        return "N";
    }

    protected void bindNumericAlias(boolean insensitive, Router<R, W> router) {
        router.addRouteParamPatternAlias(geNumericAliasKey(), "[0-9]+");
    }

    /**
     * Alpha + "_" and "-" alias
     */
    public String getAlphaPlusAliasKey() {
        return "A+";
    }

    protected void bindAlphaPlusAlias(boolean insensitive, Router<R, W> router) {
        router.addRouteParamPatternAlias(getAlphaPlusAliasKey(),
                                         (insensitive ? "(?i)" : "") + "[-_a-z]+");
    }

    /**
     * Numeric + "_" and "-" alias
     */
    public String geNumericPlusAliasKey() {
        return "N+";
    }

    protected void bindNumericPlusAlias(boolean insensitive, Router<R, W> router) {
        router.addRouteParamPatternAlias(geNumericPlusAliasKey(), "[-_0-9]+");
    }

    /**
     * Alphanumeric alias
     */
    public String getAlphaNumericAliasKey() {
        return "AN";
    }

    protected void bindAlphaNumericAlias(boolean insensitive, Router<R, W> router) {
        router.addRouteParamPatternAlias(getAlphaNumericAliasKey(),
                                         (insensitive ? "(?i)" : "") + "[a-z0-9]+");
    }

    /**
     * Alphanumeric + "_" and "-" alias
     */
    public String getAlphaNumericPlusAliasKey() {
        return "AN+";
    }

    protected void bindAlphaNumericPlusAlias(boolean insensitive, Router<R, W> router) {
        router.addRouteParamPatternAlias(getAlphaNumericPlusAliasKey(),
                                         (insensitive ? "(?i)" : "") + "[-_a-z0-9]+");
    }

}
