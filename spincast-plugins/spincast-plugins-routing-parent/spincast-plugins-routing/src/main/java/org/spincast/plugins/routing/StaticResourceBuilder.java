package org.spincast.plugins.routing;

import java.io.File;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.config.ISpincastConfig;
import org.spincast.core.exchange.IRequestContext;
import org.spincast.core.routing.IHandler;
import org.spincast.core.routing.IRouter;
import org.spincast.core.routing.IStaticResource;
import org.spincast.core.routing.IStaticResourceBuilder;
import org.spincast.core.routing.IStaticResourceCacheConfig;
import org.spincast.core.routing.IStaticResourceCorsConfig;
import org.spincast.core.routing.IStaticResourceFactory;
import org.spincast.core.routing.StaticResourceType;
import org.spincast.core.utils.ISpincastUtils;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.core.websocket.IWebsocketContext;

import com.google.common.collect.Sets;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

public class StaticResourceBuilder<R extends IRequestContext<?>, W extends IWebsocketContext<?>>
                                  implements IStaticResourceBuilder<R> {

    protected final Logger logger = LoggerFactory.getLogger(StaticResourceBuilder.class);

    private final ISpincastConfig spincastConfig;
    private final ISpincastUtils spincastUtils;
    private final IRouter<R, W> router;
    private final boolean isDir;
    private String url = null;
    private String path = null;
    private boolean isClasspath = false;
    private IStaticResourceCorsConfig corsConfig;
    private IStaticResourceCacheConfig cacheConfig;
    private IHandler<R> generator;
    private final IStaticResourceFactory<R> staticResourceFactory;
    private final IStaticResourceCorsConfigFactory staticResourceCorsConfigFactory;
    private final IStaticResourceCacheConfigFactory staticResourceCacheConfigFactory;
    private final ISpincastRouterConfig spincastRouterConfig;

    @AssistedInject
    public StaticResourceBuilder(@Assisted boolean isDir,
                                 IStaticResourceFactory<R> staticResourceFactory,
                                 IStaticResourceCorsConfigFactory staticResourceCorsConfigFactory,
                                 IStaticResourceCacheConfigFactory staticResourceCacheConfigFactory,
                                 ISpincastConfig spincastConfig,
                                 ISpincastUtils spincastUtils,
                                 ISpincastRouterConfig spincastRouterConfig) {
        this(null,
             isDir,
             staticResourceFactory,
             staticResourceCorsConfigFactory,
             staticResourceCacheConfigFactory,
             spincastConfig,
             spincastUtils,
             spincastRouterConfig);
    }

    @AssistedInject
    public StaticResourceBuilder(@Assisted IRouter<R, W> router,
                                 @Assisted boolean isDir,
                                 IStaticResourceFactory<R> staticResourceFactory,
                                 IStaticResourceCorsConfigFactory staticResourceCorsConfigFactory,
                                 IStaticResourceCacheConfigFactory staticResourceCacheConfigFactory,
                                 ISpincastConfig spincastConfig,
                                 ISpincastUtils spincastUtils,
                                 ISpincastRouterConfig spincastRouterConfig) {
        this.router = router;
        this.isDir = isDir;
        this.staticResourceFactory = staticResourceFactory;
        this.staticResourceCorsConfigFactory = staticResourceCorsConfigFactory;
        this.staticResourceCacheConfigFactory = staticResourceCacheConfigFactory;
        this.spincastConfig = spincastConfig;
        this.spincastUtils = spincastUtils;
        this.spincastRouterConfig = spincastRouterConfig;
    }

    protected boolean isDir() {
        return this.isDir;
    }

    protected IRouter<R, W> getRouter() {
        return this.router;
    }

    protected IStaticResourceFactory<R> getStaticResourceFactory() {
        return this.staticResourceFactory;
    }

    protected IStaticResourceCorsConfigFactory getStaticResourceCorsConfigFactory() {
        return this.staticResourceCorsConfigFactory;
    }

    protected IStaticResourceCacheConfigFactory getStaticResourceCacheConfigFactory() {
        return this.staticResourceCacheConfigFactory;
    }

    protected ISpincastConfig getSpincastConfig() {
        return this.spincastConfig;
    }

    protected ISpincastUtils getSpincastUtils() {
        return this.spincastUtils;
    }

    protected ISpincastRouterConfig getSpincastRouterConfig() {
        return this.spincastRouterConfig;
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

    public IStaticResourceCacheConfig getCacheConfig() {
        return this.cacheConfig;
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
    public IStaticResourceBuilder<R> pathAbsolute(String absolutePath) {
        this.path = absolutePath;
        this.isClasspath = false;
        return this;
    }

    @Override
    public IStaticResourceBuilder<R> pathRelative(String relativePath) {

        try {
            this.path = new File(getSpincastConfig().getSpincastWritableDir(), relativePath).getCanonicalPath();
            this.isClasspath = false;
            return this;
        } catch(Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    @Override
    public IStaticResourceBuilder<R> cors() {
        return cors(getCorsDefaultAllowedOrigins(),
                    getCorsDefaultExtraHeadersAllowedToBeRead(),
                    getCorsDefaultExtraHeadersAllowedToBeSent(),
                    getCorsDefaultIsCookiesAllowed(),
                    getCorsDefaultMaxAgeInSeconds());
    }

    @Override
    public IStaticResourceBuilder<R> cors(Set<String> allowedOrigins) {
        return cors(allowedOrigins,
                    getCorsDefaultExtraHeadersAllowedToBeRead(),
                    getCorsDefaultExtraHeadersAllowedToBeSent(),
                    getCorsDefaultIsCookiesAllowed(),
                    getCorsDefaultMaxAgeInSeconds());
    }

    @Override
    public IStaticResourceBuilder<R> cors(Set<String> allowedOrigins,
                                          Set<String> extraHeadersAllowedToBeRead) {
        return cors(allowedOrigins,
                    extraHeadersAllowedToBeRead,
                    getCorsDefaultExtraHeadersAllowedToBeSent(),
                    getCorsDefaultIsCookiesAllowed(),
                    getCorsDefaultMaxAgeInSeconds());
    }

    @Override
    public IStaticResourceBuilder<R> cors(Set<String> allowedOrigins,
                                          Set<String> extraHeadersAllowedToBeRead,
                                          Set<String> extraHeadersAllowedToBeSent) {
        return cors(allowedOrigins,
                    extraHeadersAllowedToBeRead,
                    extraHeadersAllowedToBeSent,
                    getCorsDefaultIsCookiesAllowed(),
                    getCorsDefaultMaxAgeInSeconds());
    }

    @Override
    public IStaticResourceBuilder<R> cors(Set<String> allowedOrigins,
                                          Set<String> extraHeadersAllowedToBeRead,
                                          Set<String> extraHeadersAllowedToBeSent,
                                          boolean allowCookies) {
        return cors(allowedOrigins,
                    extraHeadersAllowedToBeRead,
                    extraHeadersAllowedToBeSent,
                    allowCookies,
                    getCorsDefaultMaxAgeInSeconds());
    }

    @Override
    public IStaticResourceBuilder<R> cors(Set<String> allowedOrigins,
                                          Set<String> extraHeadersAllowedToBeRead,
                                          Set<String> extraHeadersAllowedToBeSent,
                                          boolean allowCookies,
                                          int maxAgeInSeconds) {

        this.corsConfig = getStaticResourceCorsConfigFactory().create(allowedOrigins,
                                                                      extraHeadersAllowedToBeRead,
                                                                      extraHeadersAllowedToBeSent,
                                                                      allowCookies,
                                                                      maxAgeInSeconds);
        return this;
    }

    /**
     * If &lt;= 0, the "Access-Control-Max-Age" header
     * won't be sent.
     */
    protected int getCorsDefaultMaxAgeInSeconds() {
        return 86400; // 24h
    }

    /**
     * The origins allowed, by default.
     */
    protected Set<String> getCorsDefaultAllowedOrigins() {

        //==========================================
        // All origins allowed.
        //==========================================
        return Sets.newHashSet("*");
    }

    /**
     * The extra headers allowed to be read, by default,
     */
    protected Set<String> getCorsDefaultExtraHeadersAllowedToBeRead() {

        //==========================================
        // No extra header allowed.
        //==========================================
        return null;
    }

    /**
     * The extra headers allowed to be sent, by default,
     */
    protected Set<String> getCorsDefaultExtraHeadersAllowedToBeSent() {

        //==========================================
        // All headers allowed.
        //==========================================
        return Sets.newHashSet("*");
    }

    /**
     * Are cookies allowed by default?
     */
    protected boolean getCorsDefaultIsCookiesAllowed() {

        //==========================================
        // Cookies allowed.
        //==========================================
        return true;
    }

    /**
     * Is the cache private by default?
     */
    protected boolean isCachePrivateDefault() {
        return false;
    }

    protected Integer getCacheCdnSecondsDefault() {
        return null;
    }

    @Override
    public IStaticResourceBuilder<R> cache(int seconds) {
        return cache(seconds, isCachePrivateDefault());
    }

    @Override
    public IStaticResourceBuilder<R> cache(int seconds, boolean isCachePrivate) {
        return cache(seconds, isCachePrivate, getCacheCdnSecondsDefault());
    }

    @Override
    public IStaticResourceBuilder<R> cache(int seconds, boolean isCachePrivate, Integer cdnSeconds) {
        this.cacheConfig = getStaticResourceCacheConfigFactory().create(seconds, isCachePrivate, cdnSeconds);
        return this;
    }

    @Override
    public void save() {
        save((IHandler<R>)null);
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

        IStaticResourceCacheConfig cacheConfig = getCacheConfig();
        if(cacheConfig == null) {
            cacheConfig = getDefaultCacheConfig();
        }

        IStaticResource<R> staticResource = getStaticResourceFactory().create(type,
                                                                              getUrl(),
                                                                              getPath(),
                                                                              getGenerator(),
                                                                              getCorsConfig(),
                                                                              cacheConfig);
        return staticResource;
    }

    /**
     * The default cache configurations to use if it is
     * not specified. It can still be <code>null</code> and,
     * in that case, no cache header will be used.
     */
    protected IStaticResourceCacheConfig getDefaultCacheConfig() {

        //==========================================
        // If a generator is specified, we use a
        // shorter cache period.
        //==========================================
        return getSpincastConfig().getDefaultStaticResourceCacheConfig(getGenerator() != null);
    }

}
