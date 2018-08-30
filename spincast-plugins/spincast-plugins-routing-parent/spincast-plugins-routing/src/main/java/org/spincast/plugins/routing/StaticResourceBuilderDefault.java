package org.spincast.plugins.routing;

import java.io.File;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.config.SpincastConfig;
import org.spincast.core.exchange.RequestContext;
import org.spincast.core.routing.Handler;
import org.spincast.core.routing.Router;
import org.spincast.core.routing.StaticResource;
import org.spincast.core.routing.StaticResourceBuilder;
import org.spincast.core.routing.StaticResourceCacheConfig;
import org.spincast.core.routing.StaticResourceCorsConfig;
import org.spincast.core.routing.StaticResourceFactory;
import org.spincast.core.routing.StaticResourceType;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.core.utils.SpincastUtils;
import org.spincast.core.websocket.WebsocketContext;

import com.google.common.collect.Sets;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

public class StaticResourceBuilderDefault<R extends RequestContext<?>, W extends WebsocketContext<?>>
                                         implements StaticResourceBuilder<R> {

    protected final Logger logger = LoggerFactory.getLogger(StaticResourceBuilderDefault.class);

    private final SpincastConfig spincastConfig;
    private final SpincastUtils spincastUtils;
    private final Router<R, W> router;
    private boolean isSpicastOrPluginAddedResource = false;
    private final boolean isDir;
    private String url = null;
    private String path = null;
    private boolean isClasspath = false;
    private StaticResourceCorsConfig corsConfig;
    private StaticResourceCacheConfig cacheConfig;
    private Handler<R> generator;
    private boolean ignoreQueryString;
    private final StaticResourceFactory<R> staticResourceFactory;
    private final StaticResourceCorsConfigFactory staticResourceCorsConfigFactory;
    private final StaticResourceCacheConfigFactory staticResourceCacheConfigFactory;
    private final SpincastRouterConfig spincastRouterConfig;


    @AssistedInject
    public StaticResourceBuilderDefault(@Assisted("isDir") boolean isDir,
                                        StaticResourceFactory<R> staticResourceFactory,
                                        StaticResourceCorsConfigFactory staticResourceCorsConfigFactory,
                                        StaticResourceCacheConfigFactory staticResourceCacheConfigFactory,
                                        SpincastConfig spincastConfig,
                                        SpincastUtils spincastUtils,
                                        SpincastRouterConfig spincastRouterConfig) {
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
    public StaticResourceBuilderDefault(@Assisted Router<R, W> router,
                                        @Assisted("isDir") boolean isDir,
                                        StaticResourceFactory<R> staticResourceFactory,
                                        StaticResourceCorsConfigFactory staticResourceCorsConfigFactory,
                                        StaticResourceCacheConfigFactory staticResourceCacheConfigFactory,
                                        SpincastConfig spincastConfig,
                                        SpincastUtils spincastUtils,
                                        SpincastRouterConfig spincastRouterConfig) {
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

    protected boolean isSpicastOrPluginAddedResource() {
        return this.isSpicastOrPluginAddedResource;
    }

    protected Router<R, W> getRouter() {
        return this.router;
    }

    protected StaticResourceFactory<R> getStaticResourceFactory() {
        return this.staticResourceFactory;
    }

    protected StaticResourceCorsConfigFactory getStaticResourceCorsConfigFactory() {
        return this.staticResourceCorsConfigFactory;
    }

    protected StaticResourceCacheConfigFactory getStaticResourceCacheConfigFactory() {
        return this.staticResourceCacheConfigFactory;
    }

    protected SpincastConfig getSpincastConfig() {
        return this.spincastConfig;
    }

    protected SpincastUtils getSpincastUtils() {
        return this.spincastUtils;
    }

    protected SpincastRouterConfig getSpincastRouterConfig() {
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

    public Handler<R> getGenerator() {
        return this.generator;
    }

    public StaticResourceCorsConfig getCorsConfig() {
        return this.corsConfig;
    }

    public StaticResourceCacheConfig getCacheConfig() {
        return this.cacheConfig;
    }

    public boolean isIgnoreQueryString() {
        return this.ignoreQueryString;
    }

    @Override
    public StaticResourceBuilder<R> spicastOrPluginAddedResource() {
        this.isSpicastOrPluginAddedResource = true;
        return this;
    }

    @Override
    public StaticResourceBuilder<R> url(String url) {
        this.url = url;
        return this;
    }

    @Override
    public StaticResourceBuilder<R> classpath(String path) {
        this.path = path;
        this.isClasspath = true;
        return this;
    }

    @Override
    public StaticResourceBuilder<R> pathAbsolute(String absolutePath) {
        this.path = absolutePath;
        this.isClasspath = false;
        return this;
    }

    @Override
    public StaticResourceBuilder<R> pathRelative(String relativePath) {

        try {
            this.path = new File(getSpincastConfig().getTempDir(), relativePath).getCanonicalPath();
            this.isClasspath = false;
            return this;
        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    @Override
    public StaticResourceBuilder<R> cors() {
        return cors(getCorsDefaultAllowedOrigins(),
                    getCorsDefaultExtraHeadersAllowedToBeRead(),
                    getCorsDefaultExtraHeadersAllowedToBeSent(),
                    getCorsDefaultIsCookiesAllowed(),
                    getCorsDefaultMaxAgeInSeconds());
    }

    @Override
    public StaticResourceBuilder<R> cors(Set<String> allowedOrigins) {
        return cors(allowedOrigins,
                    getCorsDefaultExtraHeadersAllowedToBeRead(),
                    getCorsDefaultExtraHeadersAllowedToBeSent(),
                    getCorsDefaultIsCookiesAllowed(),
                    getCorsDefaultMaxAgeInSeconds());
    }

    @Override
    public StaticResourceBuilder<R> cors(Set<String> allowedOrigins,
                                         Set<String> extraHeadersAllowedToBeRead) {
        return cors(allowedOrigins,
                    extraHeadersAllowedToBeRead,
                    getCorsDefaultExtraHeadersAllowedToBeSent(),
                    getCorsDefaultIsCookiesAllowed(),
                    getCorsDefaultMaxAgeInSeconds());
    }

    @Override
    public StaticResourceBuilder<R> cors(Set<String> allowedOrigins,
                                         Set<String> extraHeadersAllowedToBeRead,
                                         Set<String> extraHeadersAllowedToBeSent) {
        return cors(allowedOrigins,
                    extraHeadersAllowedToBeRead,
                    extraHeadersAllowedToBeSent,
                    getCorsDefaultIsCookiesAllowed(),
                    getCorsDefaultMaxAgeInSeconds());
    }

    @Override
    public StaticResourceBuilder<R> cors(Set<String> allowedOrigins,
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
    public StaticResourceBuilder<R> cors(Set<String> allowedOrigins,
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
    public StaticResourceBuilder<R> cache(int seconds) {
        return cache(seconds, isCachePrivateDefault());
    }

    @Override
    public StaticResourceBuilder<R> cache(int seconds, boolean isCachePrivate) {
        return cache(seconds, isCachePrivate, getCacheCdnSecondsDefault());
    }

    @Override
    public StaticResourceBuilder<R> cache(int seconds, boolean isCachePrivate, Integer cdnSeconds) {
        this.cacheConfig = getStaticResourceCacheConfigFactory().create(seconds, isCachePrivate, cdnSeconds);
        return this;
    }

    @Override
    public void handle() {
        handle((Handler<R>)null);
    }

    @Override
    public void handle(Handler<R> generator) {
        handle(generator, false);
    }

    @Override
    public void handle(Handler<R> generator, boolean ignoreQueryString) {

        if (getRouter() == null) {
            throw new RuntimeException("No router specified, can't save the static resource!");
        }

        if (isClasspath() && generator != null) {
            throw new RuntimeException("A resource generator can only be specified when a file system " +
                                       "path is used, not a classpath path.");
        }
        this.generator = generator;

        if (getUrl() == null) {
            throw new RuntimeException("The URL to the resource must be specified!");
        }

        if (getPath() == null) {
            throw new RuntimeException("A classpath or a file system path must be specified!");
        }

        this.ignoreQueryString = ignoreQueryString;

        StaticResource<R> staticResource = create();
        getRouter().addStaticResource(staticResource);
    }

    @Override
    public StaticResource<R> create() {

        StaticResourceType type;
        if (isDir()) {
            if (isClasspath()) {
                type = StaticResourceType.DIRECTORY_FROM_CLASSPATH;
            } else {
                type = StaticResourceType.DIRECTORY;
            }
        } else {
            if (isClasspath()) {
                type = StaticResourceType.FILE_FROM_CLASSPATH;
            } else {
                type = StaticResourceType.FILE;
            }
        }

        StaticResourceCacheConfig cacheConfig = getCacheConfig();
        if (cacheConfig == null) {
            cacheConfig = getDefaultCacheConfig();
        }

        StaticResource<R> staticResource = getStaticResourceFactory().create(isSpicastOrPluginAddedResource(),
                                                                             type,
                                                                             getUrl(),
                                                                             getPath(),
                                                                             getGenerator(),
                                                                             getCorsConfig(),
                                                                             cacheConfig,
                                                                             isIgnoreQueryString());
        return staticResource;
    }

    /**
     * The default cache configurations to use if it is
     * not specified. It can still be <code>null</code> and,
     * in that case, no cache header will be used.
     */
    protected StaticResourceCacheConfig getDefaultCacheConfig() {

        //==========================================
        // If a generator is specified, we use a
        // shorter cache period.
        //==========================================
        return getSpincastConfig().getDefaultStaticResourceCacheConfig(getGenerator() != null);
    }

}
