package org.spincast.plugins.routing;

import javax.annotation.Nullable;

import org.spincast.core.exchange.RequestContext;
import org.spincast.core.routing.Handler;
import org.spincast.core.routing.StaticResource;
import org.spincast.core.routing.StaticResourceCacheConfig;
import org.spincast.core.routing.StaticResourceCorsConfig;
import org.spincast.core.routing.StaticResourceType;

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

    @AssistedInject
    public StaticResourceDefault(@Assisted("isSpicastOrPluginAddedResource") boolean isSpicastOrPluginAddedResource,
                                 @Assisted StaticResourceType staticResourceType,
                                 @Assisted("url") String urlPath,
                                 @Assisted("path") String resourcePath,
                                 @Assisted @Nullable Handler<R> generator,
                                 @Assisted @Nullable StaticResourceCorsConfig corsConfig,
                                 @Assisted @Nullable StaticResourceCacheConfig cacheConfig,
                                 @Assisted("ignoreQueryString") boolean ignoreQueryString) {
        this.isSpicastOrPluginAddedResource = isSpicastOrPluginAddedResource;
        this.staticResourceType = staticResourceType;
        this.urlPath = urlPath;
        this.resourcePath = resourcePath;
        this.generator = generator;
        this.corsConfig = corsConfig;
        this.cacheConfig = cacheConfig;
        this.ignoreQueryString = ignoreQueryString;
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
    public String toString() {
        return getResourcePath();
    }

}
