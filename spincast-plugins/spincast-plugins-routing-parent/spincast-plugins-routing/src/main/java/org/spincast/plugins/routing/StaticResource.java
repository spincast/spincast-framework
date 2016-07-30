package org.spincast.plugins.routing;

import javax.annotation.Nullable;

import org.spincast.core.exchange.IRequestContext;
import org.spincast.core.routing.IHandler;
import org.spincast.core.routing.IStaticResource;
import org.spincast.core.routing.IStaticResourceCacheConfig;
import org.spincast.core.routing.IStaticResourceCorsConfig;
import org.spincast.core.routing.StaticResourceType;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

/**
 * Represents a static resource.
 */
public class StaticResource<R extends IRequestContext<?>> implements IStaticResource<R> {

    private final StaticResourceType staticResourceType;
    private final String urlPath;
    private final String resourcePath;
    private final IHandler<R> generator;
    private final IStaticResourceCorsConfig corsConfig;
    private final IStaticResourceCacheConfig cacheConfig;

    @AssistedInject
    public StaticResource(@Assisted StaticResourceType staticResourceType,
                          @Assisted("url") String urlPath,
                          @Assisted("path") String resourcePath,
                          @Assisted @Nullable IHandler<R> generator,
                          @Assisted @Nullable IStaticResourceCorsConfig corsConfig,
                          @Assisted @Nullable IStaticResourceCacheConfig cacheConfig) {
        this.staticResourceType = staticResourceType;
        this.urlPath = urlPath;
        this.resourcePath = resourcePath;
        this.generator = generator;
        this.corsConfig = corsConfig;
        this.cacheConfig = cacheConfig;
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
    public IHandler<R> getGenerator() {
        return this.generator;
    }

    @Override
    public IStaticResourceCorsConfig getCorsConfig() {
        return this.corsConfig;
    }

    @Override
    public IStaticResourceCacheConfig getCacheConfig() {
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
    public String toString() {
        return getResourcePath();
    }

}
