package org.spincast.plugins.jacksonxml;

/**
 * An simple implementation of XmlMixinInfo.
 */
public class XmlMixinInfoDefault implements XmlMixinInfo {

    private final Class<?> targetClass;
    private final Class<?> mixinClass;

    public XmlMixinInfoDefault(Class<?> targetClass, Class<?> mixinClass) {
        this.targetClass = targetClass;
        this.mixinClass = mixinClass;
    }

    @Override
    public Class<?> getTargetClass() {
        return this.targetClass;
    }

    @Override
    public Class<?> getMixinClass() {
        return this.mixinClass;
    }

}
