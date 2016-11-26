package org.spincast.core.routing;

/**
 * Builder to create a redirection rule.
 */
public interface RedirectRuleBuilder {

    /**
     * The redirection will be permanent (301).
     * 
     * This is the default.
     */
    public RedirectRuleBuilder permanently();

    /**
     * The redirection will be temporarily (302).
     */
    public RedirectRuleBuilder temporarily();

    /**
     * The new path or full URL to redirect to.
     * 
     * This ends the creation of the redirection rule and
     * save it to the router.
     */
    public void to(String newPathOrFullUrl);

}
