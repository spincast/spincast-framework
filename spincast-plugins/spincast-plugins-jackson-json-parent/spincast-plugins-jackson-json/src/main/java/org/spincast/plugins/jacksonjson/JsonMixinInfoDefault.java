package org.spincast.plugins.jacksonjson;

/**
 * An simple implementation of JsonMixinInfo.
 */
public class JsonMixinInfoDefault implements JsonMixinInfo {

    private final Class<?> targetClass;
    private final Class<?> mixinClass;

    public JsonMixinInfoDefault(Class<?> targetClass, Class<?> mixinClass) {
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
