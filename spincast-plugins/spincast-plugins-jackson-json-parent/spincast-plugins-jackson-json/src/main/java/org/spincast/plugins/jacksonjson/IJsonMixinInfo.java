package org.spincast.plugins.jacksonjson;

/**
 * Links a mixin to a class to (de)serialize using
 * Jackson.
 */
public interface IJsonMixinInfo {

    /**
     * The class to apply the mixin to.
     */
    public Class<?> getTargetClass();

    /**
     * The mixin class.
     */
    public Class<?> getMixinClass();

}
