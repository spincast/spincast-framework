package org.spincast.plugins.routing;

import java.util.Set;

import org.spincast.core.exchange.IRequestContext;
import org.spincast.core.routing.IHandler;
import org.spincast.core.routing.IRouter;
import org.spincast.core.routing.IStaticResource;
import org.spincast.core.routing.IStaticResourceBuilder;
import org.spincast.core.routing.IStaticResourceCorsConfig;
import org.spincast.core.routing.StaticResourceType;

import com.google.common.collect.Sets;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

public class StaticResourceBuilder<R extends IRequestContext<?>> implements IStaticResourceBuilder<R> {

    private final IRouter<R> router;
    private final boolean isDir;
    private String url = null;
    private String path = null;
    private boolean isClasspath = false;
    private IStaticResourceCorsConfig corsConfig;
    private IHandler<R> generator;
    private final IStaticResourceFactory<R> staticResourceFactory;
    private final IStaticResourceCorsConfigFactory staticResourceCorsConfigFactory;

    @AssistedInject
    public StaticResourceBuilder(@Assisted boolean isDir,
                                 IStaticResourceFactory<R> staticResourceFactory,
                                 IStaticResourceCorsConfigFactory staticResourceCorsConfigFactory) {
        this(null, isDir, staticResourceFactory, staticResourceCorsConfigFactory);
    }

    @AssistedInject
    public StaticResourceBuilder(@Assisted IRouter<R> router,
                                 @Assisted boolean isDir,
                                 IStaticResourceFactory<R> staticResourceFactory,
                                 IStaticResourceCorsConfigFactory staticResourceCorsConfigFactory) {
        this.router = router;
        this.isDir = isDir;
        this.staticResourceFactory = staticResourceFactory;
        this.staticResourceCorsConfigFactory = staticResourceCorsConfigFactory;
    }

    protected boolean isDir() {
        return this.isDir;
    }

    protected IRouter<R> getRouter() {
        return this.router;
    }

    protected IStaticResourceFactory<R> getStaticResourceFactory() {
        return this.staticResourceFactory;
    }

    protected IStaticResourceCorsConfigFactory getStaticResourceCorsConfigFactory() {
        return this.staticResourceCorsConfigFactory;
    }

    public String getUrl() {
        return this.url;
    }

    public String getPath() {
        return this.path;
    }

    public boolean isClasspath() {
        return this.isClasspath;
    }

    public IHandler<R> getGenerator() {
        return this.generator;
    }

    public IStaticResourceCorsConfig getCorsConfig() {
        return this.corsConfig;
    }

    @Override
    public IStaticResourceBuilder<R> url(String url) {
        this.url = url;
        return this;
    }

    @Override
    public IStaticResourceBuilder<R> classpath(String path) {
        this.path = path;
        this.isClasspath = true;
        return this;
    }

    @Override
    public IStaticResourceBuilder<R> fileSystem(String path) {
        this.path = path;
        this.isClasspath = false;
        return this;
    }

    @Override
    public IStaticResourceBuilder<R> cors() {
        return cors(Sets.newHashSet("*"), null, true);
    }

    @Override
    public IStaticResourceBuilder<R> cors(Set<String> allowedOrigins) {
        return cors(allowedOrigins, null, true);
    }

    @Override
    public IStaticResourceBuilder<R> cors(Set<String> allowedOrigins,
                                          Set<String> extraHeadersAllowedToBeRead) {
        return cors(allowedOrigins, extraHeadersAllowedToBeRead, true);
    }

    @Override
    public IStaticResourceBuilder<R> cors(Set<String> allowedOrigins,
                                          Set<String> extraHeadersAllowedToBeRead,
                                          boolean allowCookies) {

        this.corsConfig = getStaticResourceCorsConfigFactory().create(allowedOrigins,
                                                                      extraHeadersAllowedToBeRead,
                                                                      allowCookies);
        return this;
    }

    @Override
    public void save() {
        save(null);
    }

    @Override
    public void save(IHandler<R> generator) {

        if(getRouter() == null) {
            throw new RuntimeException("No router specified, can't save the static resource!");
        }

        if(isClasspath() && generator != null) {
            throw new RuntimeException("A resource generator can only be specified when a file system " +
                                       "path is used, not a classpath path.");
        }
        this.generator = generator;

        if(getUrl() == null) {
            throw new RuntimeException("The URL to the resource must be specified!");
        }

        if(getPath() == null) {
            throw new RuntimeException("A classpath or a file system path must be specified!");
        }

        IStaticResource<R> staticResource = create();
        getRouter().addStaticResource(staticResource);
    }

    @Override
    public IStaticResource<R> create() {

        StaticResourceType type;
        if(isDir()) {
            if(isClasspath()) {
                type = StaticResourceType.DIRECTORY_FROM_CLASSPATH;
            } else {
                type = StaticResourceType.DIRECTORY;
            }
        } else {
            if(isClasspath()) {
                type = StaticResourceType.FILE_FROM_CLASSPATH;
            } else {
                type = StaticResourceType.FILE;
            }
        }

        IStaticResource<R> staticResource = getStaticResourceFactory().create(type,
                                                                              getUrl(),
                                                                              getPath(),
                                                                              getGenerator(),
                                                                              getCorsConfig());
        return staticResource;
    }

}
