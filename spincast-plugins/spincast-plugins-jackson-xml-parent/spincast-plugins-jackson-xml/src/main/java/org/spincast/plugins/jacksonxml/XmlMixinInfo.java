package org.spincast.plugins.jacksonxml;

/**
 * An simple implementation of IXmlMixinInfo.
 */
public class XmlMixinInfo implements IXmlMixinInfo {

    private final Class<?> targetClass;
    private final Class<?> mixinClass;

    public XmlMixinInfo(Class<?> targetClass, Class<?> mixinClass) {
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
