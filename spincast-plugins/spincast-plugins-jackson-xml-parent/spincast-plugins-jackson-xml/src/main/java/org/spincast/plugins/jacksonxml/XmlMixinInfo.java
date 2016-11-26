package org.spincast.plugins.jacksonxml;

/**
 * Mixin for Jackson.
 */
public interface XmlMixinInfo {

    /**
     * The class to apply the mixin to.
     */
    public Class<?> getTargetClass();

    /**
     * The mixin class.
     */
    public Class<?> getMixinClass();

}
