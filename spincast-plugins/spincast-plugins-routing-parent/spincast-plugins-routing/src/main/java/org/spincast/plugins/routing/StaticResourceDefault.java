package org.spincast.plugins.routing;

import javax.annotation.Nullable;

import org.spincast.core.exchange.RequestContext;
import org.spincast.core.routing.Handler;
import org.spincast.core.routing.StaticResource;
import org.spincast.core.routing.StaticResourceCacheConfig;
import org.spincast.core.routing.StaticResourceCorsConfig;
import org.spincast.core.routing.StaticResourceType;
import org.spincast.core.routing.hotlinking.HotlinkingManager;
import org.spincast.core.routing.hotlinking.HotlinkingManagerDefault;
import org.spincast.core.utils.SpincastUtils;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

/**
 * Represents a static resource.
 */
public class StaticResourceDefault<R extends RequestContext<?>> implements StaticResource<R> {

    private final boolean isSpicastOrPluginAddedResource;
    private final StaticResourceType staticResourceType;
    private final String urlPath;
    private final String resourcePath;
    private final Handler<R> generator;
    private final StaticResourceCorsConfig corsConfig;
    private final StaticResourceCacheConfig cacheConfig;
    private final boolean ignoreQueryString;
    private boolean hotlinkingProtected = false;
    private HotlinkingManager hotlinkingManager;
    private final SpincastUtils spincastUtils;

    @AssistedInject
    public StaticResourceDefault(@Assisted("isSpicastOrPluginAddedResource") boolean isSpicastOrPluginAddedResource,
                                 @Assisted StaticResourceType staticResourceType,
                                 @Assisted("url") String urlPath,
                                 @Assisted("path") String resourcePath,
                                 @Assisted @Nullable Handler<R> generator,
                                 @Assisted @Nullable StaticResourceCorsConfig corsConfig,
                                 @Assisted @Nullable StaticResourceCacheConfig cacheConfig,
                                 @Assisted("ignoreQueryString") boolean ignoreQueryString,
                                 @Assisted("hotlinkingProtected") boolean hotlinkingProtected,
                                 @Assisted("hotlinkingManager") @Nullable HotlinkingManager hotlinkingManager,
                                 SpincastUtils spincastUtils,
                                 HotlinkingManagerDefault hotlinkingManagerDefault) {
        this.spincastUtils = spincastUtils;

        this.isSpicastOrPluginAddedResource = isSpicastOrPluginAddedResource;
        this.staticResourceType = staticResourceType;
        this.urlPath = urlPath;
        this.resourcePath = resourcePath;
        this.generator = generator;
        this.corsConfig = corsConfig;
        this.cacheConfig = cacheConfig;
        this.ignoreQueryString = ignoreQueryString;
        this.hotlinkingProtected = hotlinkingProtected;

        if (hotlinkingManager == null) {
            hotlinkingManager = hotlinkingManagerDefault;
        }
        this.hotlinkingManager = hotlinkingManager;
    }

    protected SpincastUtils getSpincastUtils() {
        return this.spincastUtils;
    }

    @Override
    public boolean isSpicastOrPluginAddedResource() {
        return this.isSpicastOrPluginAddedResource;
    }

    @Override
    public StaticResourceType getStaticResourceType() {
        return this.staticResourceType;
    }

    @Override
    public String getUrlPath() {
        return this.urlPath;
    }

    @Override
    public String getResourcePath() {
        return this.resourcePath;
    }

    @Override
    public boolean isCanBeGenerated() {
        return getGenerator() != null;
    }

    @Override
    public Handler<R> getGenerator() {
        return this.generator;
    }

    @Override
    public StaticResourceCorsConfig getCorsConfig() {
        return this.corsConfig;
    }

    @Override
    public StaticResourceCacheConfig getCacheConfig() {
        return this.cacheConfig;
    }

    @Override
    public boolean isFileResource() {
        return getStaticResourceType() == StaticResourceType.FILE ||
               getStaticResourceType() == StaticResourceType.FILE_FROM_CLASSPATH;
    }

    @Override
    public boolean isDirResource() {
        return !isFileResource();
    }

    @Override
    public boolean isClasspath() {
        return getStaticResourceType() == StaticResourceType.FILE_FROM_CLASSPATH ||
               getStaticResourceType() == StaticResourceType.DIRECTORY_FROM_CLASSPATH;
    }

    @Override
    public boolean isFileSytem() {
        return !isClasspath();
    }

    @Override
    public boolean isIgnoreQueryString() {
        return this.ignoreQueryString;
    }

    @Override
    public boolean isHotlinkingProtected() {
        return this.hotlinkingProtected;
    }

    @Override
    public HotlinkingManager getHotlinkingManager() {
        return this.hotlinkingManager;
    }

    @Override
    public String toString() {
        return getResourcePath();
    }

}
