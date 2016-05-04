package org.spincast.plugins.jacksonjson;

/**
 * An simple implementation of IJsonMixinInfo.
 */
public class JsonMixinInfo implements IJsonMixinInfo {

    private final Class<?> targetClass;
    private final Class<?> mixinClass;

    public JsonMixinInfo(Class<?> targetClass, Class<?> mixinClass) {
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
