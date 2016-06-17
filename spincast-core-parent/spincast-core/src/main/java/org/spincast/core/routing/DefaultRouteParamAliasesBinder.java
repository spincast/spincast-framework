package org.spincast.core.routing;

import org.spincast.core.config.ISpincastConfig;
import org.spincast.core.exchange.IRequestContext;
import org.spincast.core.websocket.IWebsocketContext;

import com.google.inject.Inject;

/**
 * Component that binds some default route parameter aliases.
 */
public class DefaultRouteParamAliasesBinder<R extends IRequestContext<?>, W extends IWebsocketContext<?>> {

    /**
     * Init : binds the aliases.
     */
    @Inject
    protected void init(ISpincastConfig spincastConfig,
                        IRouter<R, W> router) {

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

    protected void bindAlphaAlias(boolean insensitive, IRouter<R, W> router) {
        router.addRouteParamPatternAlias(getAlphaAliasKey(),
                                         (insensitive ? "(?i)" : "") + "[a-z]+");
    }

    /**
     * Numeric alias
     */
    public String geNumericAliasKey() {
        return "N";
    }

    protected void bindNumericAlias(boolean insensitive, IRouter<R, W> router) {
        router.addRouteParamPatternAlias(geNumericAliasKey(), "[0-9]+");
    }

    /**
     * Alpha + "_" and "-" alias
     */
    public String getAlphaPlusAliasKey() {
        return "A+";
    }

    protected void bindAlphaPlusAlias(boolean insensitive, IRouter<R, W> router) {
        router.addRouteParamPatternAlias(getAlphaPlusAliasKey(),
                                         (insensitive ? "(?i)" : "") + "[-_a-z]+");
    }

    /**
     * Numeric + "_" and "-" alias
     */
    public String geNumericPlusAliasKey() {
        return "N+";
    }

    protected void bindNumericPlusAlias(boolean insensitive, IRouter<R, W> router) {
        router.addRouteParamPatternAlias(geNumericPlusAliasKey(), "[-_0-9]+");
    }

    /**
     * Alphanumeric alias
     */
    public String getAlphaNumericAliasKey() {
        return "AN";
    }

    protected void bindAlphaNumericAlias(boolean insensitive, IRouter<R, W> router) {
        router.addRouteParamPatternAlias(getAlphaNumericAliasKey(),
                                         (insensitive ? "(?i)" : "") + "[a-z0-9]+");
    }

    /**
     * Alphanumeric + "_" and "-" alias
     */
    public String getAlphaNumericPlusAliasKey() {
        return "AN+";
    }

    protected void bindAlphaNumericPlusAlias(boolean insensitive, IRouter<R, W> router) {
        router.addRouteParamPatternAlias(getAlphaNumericPlusAliasKey(),
                                         (insensitive ? "(?i)" : "") + "[-_a-z0-9]+");
    }

}
