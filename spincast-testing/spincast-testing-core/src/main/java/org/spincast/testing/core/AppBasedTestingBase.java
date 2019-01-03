package org.spincast.testing.core;

import static org.junit.Assert.assertNotNull;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.spincast.core.config.SpincastConfig;
import org.spincast.core.cookies.Cookie;
import org.spincast.core.cookies.CookieFactory;
import org.spincast.core.exchange.RequestContext;
import org.spincast.core.guice.GuiceModuleUtils;
import org.spincast.core.guice.GuiceTweaker;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.guice.SpincastPlugin;
import org.spincast.core.routing.Router;
import org.spincast.core.server.Server;
import org.spincast.core.websocket.WebsocketContext;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.plugins.httpclient.builders.ConnectRequestBuilder;
import org.spincast.plugins.httpclient.builders.DeleteRequestBuilder;
import org.spincast.plugins.httpclient.builders.GetRequestBuilder;
import org.spincast.plugins.httpclient.builders.HeadRequestBuilder;
import org.spincast.plugins.httpclient.builders.OptionsRequestBuilder;
import org.spincast.plugins.httpclient.builders.PatchRequestBuilder;
import org.spincast.plugins.httpclient.builders.PostRequestBuilder;
import org.spincast.plugins.httpclient.builders.PutRequestBuilder;
import org.spincast.plugins.httpclient.builders.TraceRequestBuilder;
import org.spincast.plugins.httpclient.websocket.HttpClient;
import org.spincast.plugins.httpclient.websocket.SpincastHttpClientWithWebsocketPlugin;
import org.spincast.plugins.httpclient.websocket.builders.WebsocketRequestBuilder;
import org.spincast.shaded.org.apache.commons.lang3.StringUtils;
import org.spincast.shaded.org.apache.http.client.utils.DateUtils;
import org.spincast.testing.core.utils.SpincastConfigTestingDefault;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Scopes;
import com.google.inject.util.Modules;

public abstract class AppBasedTestingBase<R extends RequestContext<?>, W extends WebsocketContext<?>>
                                         extends SpincastTestBase {

    private Set<Cookie> previousResponseCookies = new HashSet<Cookie>();

    @Inject
    private HttpClient httpClient;

    @Inject
    private Server server;

    @Inject
    private Router<R, W> router;

    @Inject
    private CookieFactory cookieFactory;

    @Override
    public void beforeClass() {
        super.beforeClass();
    }

    @Override
    protected final Injector createInjector() {

        //==========================================
        // Starts the app!
        //==========================================
        callAppMainMethod();

        //==========================================
        // The Guice injector should now have been added
        // to the SpincastPluginThreadLocal...
        //==========================================
        Injector injector = getGuiceTweakerFromThreadLocal().getInjector();
        assertNotNull(injector);

        return injector;
    }

    @Override
    protected final Class<? extends SpincastConfig> getTestingConfigImplementationClass() {
        Class<? extends SpincastConfig> impl = getAppTestingConfigs().getSpincastConfigTestingImplementationClass();
        return (impl != null ? impl : super.getTestingConfigImplementationClass());
    }

    @Override
    protected final boolean isDisableBindCurrentClass() {
        return !getAppTestingConfigs().isBindAppClass();
    }

    @Override
    protected Set<Key<?>> getExtraExactBindingsToRemoveBeforePlugins() {
        Set<Key<?>> extraExactBindingsToRemove = super.getExtraExactBindingsToRemoveBeforePlugins();

        //==========================================
        // We remove ALL bindings related to configurations
        // before the plugins are bound.
        //
        // Configurations bindings is a special case:
        // we allow a test file to bind a different
        // testing implementation for SpincastConfig and for
        // the AppConfig interfaces. This means that two
        // bound implementations can ultimatly implements
        // SpincastConfig (since the AppConfig can also
        // extend it!). The SpincastConfigPlugin
        // doesn't like that: it throws an exeption
        // if it founds more than one binding ultimately
        // implementing SpincastConfig.
        //
        // Anyway, all the "getExtraOverridingModule()"
        // are bound again *after* the plugins are applied,
        // so the configurations bindings will still be
        // overriden properly.
        //==========================================

        extraExactBindingsToRemove.add(Key.get(SpincastConfig.class));

        if (getAppTestingConfigs().getAppConfigTestingImplementationClass() != null) {
            extraExactBindingsToRemove.add(Key.get(getAppTestingConfigs().getAppConfigTestingImplementationClass()));
        }

        if (getAppTestingConfigs().getAppConfigInterface() != null) {
            extraExactBindingsToRemove.add(Key.get(getAppTestingConfigs().getAppConfigInterface()));
        }

        if (getAppTestingConfigs().getSpincastConfigTestingImplementationClass() != null) {
            extraExactBindingsToRemove.add(Key.get(getAppTestingConfigs().getSpincastConfigTestingImplementationClass()));
        }

        return extraExactBindingsToRemove;
    }


    @Override
    protected final Module getGuiceTweakerExtraOverridingModule() {

        Module extraModuleUserSpecified = getExtraOverridingModule();
        if (extraModuleUserSpecified == null) {
            extraModuleUserSpecified = new SpincastGuiceModuleBase() {

                @Override
                protected void configure() {
                    // nothing
                }
            };
        }

        Module localModule = super.getGuiceTweakerExtraOverridingModule();

        //==========================================
        // The SpincastConfig testing binding is done by
        // the parent class, using the implementation returned by
        // {@link #getTestingConfigImplementationClass}. Here,
        // we tweak the *app* testing configuration bindings, 
        // if required...
        //==========================================
        final AppTestingConfigs testingConfigs = getAppTestingConfigs();
        if (getAppTestingConfigs().getAppConfigInterface() != null ||
            getAppTestingConfigs().getAppConfigTestingImplementationClass() != null) {

            if (testingConfigs.getAppConfigTestingImplementationClass() == null) {
                throw new RuntimeException("The testing app configuration implementation " +
                                           "can't be null for the the specified interface " +
                                           testingConfigs.getAppConfigInterface().getName());
            }

            //==========================================
            // Validation
            //==========================================
            if (testingConfigs.getAppConfigInterface() != null &&
                !testingConfigs.getAppConfigInterface()
                               .isAssignableFrom(testingConfigs.getAppConfigTestingImplementationClass())) {

                throw new RuntimeException("The testing app configuration implementation \"" +
                                           testingConfigs.getAppConfigTestingImplementationClass() +
                                           "\" doesn't implement the specified interface " +
                                           testingConfigs.getAppConfigInterface().getName());
            }

            localModule = Modules.override(localModule).with(new SpincastGuiceModuleBase() {

                @SuppressWarnings({"unchecked", "rawtypes"})
                @Override
                protected void configure() {

                    bind(testingConfigs.getAppConfigTestingImplementationClass()).in(Scopes.SINGLETON);

                    if (testingConfigs.getAppConfigInterface() != null) {
                        bind(testingConfigs.getAppConfigInterface()).to((Class)testingConfigs.getAppConfigTestingImplementationClass())
                                                                    .in(Scopes.SINGLETON);
                    }

                }
            });
        }

        return Modules.override(localModule).with(extraModuleUserSpecified);
    }

    /**
     * Since an App is used, it will probably use it own
     * AppConfig interface for the config. Often, this interface will
     * extends {@link SpincastConfig} so all configs are available from
     * the same instance: those for the application and the default
     *  ones required by Spincast.
     * <p>
     * But, during tests, we also want to easily reuse the testing configs values defined
     * in {@link SpincastConfigTestingDefault} (for example for a free port
     * to be used to start the HTTPS server)... Without having to extends
     * AppConfig and add a binding that redefine all those testing values available in
     * {@link SpincastConfigTestingDefault}.
     * <p>
     * But that means that two bound interfaces must use those testing configs,
     * AppConfig and SpincastConfig. To be able to defined testing configs 
     * *once*, we add an AOP interceptor: all methods on AppConfig inherited from
     * SpincastConfig (such as "getHttpsServerPort()") will be intercepted
     * and the ones from the testing impl bound to SpincastConfig will be used
     * instead!
     */
    @Override
    protected void tweakConfigurations(GuiceTweaker guiceTweaker) {
        super.tweakConfigurations(guiceTweaker);

        AppTestingConfigs testingConfigs = getAppTestingConfigs();
        Class<?> appConfigInterface = testingConfigs.getAppConfigInterface();

        //==========================================
        // This interception only makes sense if the 
        // AppTestingConfigs ultimately extends SpincastConfig!
        //==========================================
        if (appConfigInterface != null && SpincastConfig.class.isAssignableFrom(appConfigInterface)) {
            Class<?> spincastConfigTestingImplementationClass =
                    testingConfigs.getSpincastConfigTestingImplementationClass();

            SpincastGuiceModuleBase interceptModule =
                    GuiceModuleUtils.createInterceptorModule(appConfigInterface,
                                                             spincastConfigTestingImplementationClass,
                                                             isIgnoreMethodsAnnotatedWithInjectDuringConfigurationsTweaking());
            guiceTweaker.overridingModule(interceptModule);
        }
    }

    protected boolean isIgnoreMethodsAnnotatedWithInjectDuringConfigurationsTweaking() {
        return true;
    }

    /**
     * Can be overriden with something like :
     * 
     * <pre>
     * return Modules.override(super.getExtraOverridingModule()).with(new SpincastGuiceModuleBase() {
     *     protected void configure() {
     *         // ...
     *     }
     * });
     * </pre>
     */
    protected Module getExtraOverridingModule() {
        //==========================================
        // Empty Module, so Modules.combine() and
        // Modules.override() can be used by the extending
        // classes.
        //==========================================
        return new SpincastGuiceModuleBase() {

            @Override
            protected void configure() {
                // nothing
            }
        };
    }

    /**
     * The extra required plugins.
     */
    @Override
    protected final List<SpincastPlugin> getGuiceTweakerExtraPlugins() {

        List<SpincastPlugin> plugins = super.getGuiceTweakerExtraPlugins();

        List<SpincastPlugin> pluginsUserDefined = getExtraPlugins();
        if (pluginsUserDefined != null) {
            plugins.addAll(pluginsUserDefined);
        }

        //==========================================
        // We need the Spincast HTTP Client plugin
        //==========================================
        plugins.add(new SpincastHttpClientWithWebsocketPlugin());

        return plugins;
    }

    /**
     * The extra required plugins.
     * Example:
     * <pre>
     * List&lt;SpincastPlugin&gt; extraPlugins = super.getExtraPlugins();
     * extraPlugins.add(new XXX());
     * return extraPlugins;
     * </pre>
     */
    protected List<SpincastPlugin> getExtraPlugins() {
        return Lists.newArrayList();
    }

    @Override
    protected void validateCreatedInjector(Injector guice) {
        super.validateCreatedInjector(guice);

        //==========================================
        // We validate that the required plugin have been
        // bound.
        //==========================================
        try {
            guice.getBinding(HttpClient.class);
        } catch (Exception ex) {

            String msg = "By extending the " + this.getClass().getName() + " base class, " +
                         "the " + HttpClient.class.getName() + " extra plugin must be bound " +
                         "in the Guice context.";

            throw new RuntimeException(msg, ex);
        }
    }

    @Override
    public void afterClass() {
        super.afterClass();
        stopServer();
    }

    protected void stopServer() {
        if (getServer() != null) {
            //==========================================
            // No "closing" message to the peers by default
            // for the tests.
            //==========================================
            getServer().stop(false);
        }
    }

    protected HttpClient getHttpClient() {
        return this.httpClient;
    }

    protected Router<R, W> getRouter() {
        return this.router;
    }

    protected CookieFactory getCookieFactory() {
        return this.cookieFactory;
    }

    protected Server getServer() {
        return this.server;
    }

    /**
     * Creates an URL to the started HTTPS server.
     * 
     * @param path the relative path to be appended to the
     * base test URL.
     */
    protected String createTestUrl(String path) {
        return createTestUrl(path, false, true);
    }

    /**
     * Creates an URL to the started HTTP(S) server.
     * 
     * @param path the relative path to be appended to the
     * base test URL.
     * 
     * @param isHttps if <code>true</code>, "https:" will be used
     * instead of "http:".
     */
    protected String createTestUrl(String path, boolean isHttps) {
        return createTestUrl(path, false, isHttps);
    }

    /**
     * Creates an URL to the started HTTP(S) server.
     * 
     * @param pathOrUrl a relative path OR a full URL.
     * 
     * @param isFullUrl if the 'pathOrUrl' parameter a full URL? If
     * so, it will be used as is. Otherwise it will be appended to the
     * base test URL.
     * 
     * @param isHttps if <code>true</code>, "https:" will be used
     * instead of "http:".
     */
    protected String createTestUrl(String pathOrUrl, boolean isFullUrl, boolean isHttps) {

        if (isFullUrl) {
            return pathOrUrl;
        }

        if (StringUtils.isBlank(pathOrUrl)) {
            pathOrUrl = "/";
        } else if (!pathOrUrl.startsWith("/")) {
            pathOrUrl = "/" + pathOrUrl;
        }

        String host = getSpincastConfig().getServerHost();
        if ("0.0.0.0".equals(host)) {
            host = "127.0.0.1";
        }

        return "http" + (isHttps ? "s" : "") + "://" + host + ":" +
               (isHttps ? getSpincastConfig().getHttpsServerPort() : getSpincastConfig().getHttpServerPort()) + pathOrUrl;
    }

    protected WebsocketRequestBuilder websocket(String path) {
        return websocket(path, false, true);
    }

    protected WebsocketRequestBuilder websocket(String pathOrUrl, boolean isFullUrl) {
        return websocket(pathOrUrl, isFullUrl, true);
    }

    protected WebsocketRequestBuilder websocket(String pathOrUrl, boolean isFullUrl, boolean isHttps) {

        WebsocketRequestBuilder builder = getHttpClient().websocket(createTestUrl(pathOrUrl, isFullUrl, isHttps));
        return builder;
    }

    /**
     * Starts an Https Client builder for a GET method.
     * 
     * A cookie store is automatically added.
     * 
     * @param path the relative path to be appended to the
     * base test URL.
     */
    protected GetRequestBuilder GET(String path) {
        return GET(path, false, true);
    }

    /**
     * Starts an Https Client builder for a GET method.
     * 
     * A cookie store is automatically added.
     * 
     * @param pathOrUrl a relative path or a full URL.
     * 
     * @param isFullUrl if the 'pathOrUrl' parameter a full URL? If
     * so, it will be used as is. Otherwise it will be appended to the
     * base test URL. 
     */
    protected GetRequestBuilder GET(String pathOrUrl, boolean isFullUrl) {
        return GET(pathOrUrl, isFullUrl, true);
    }

    /**
     * Starts an Http(s) Client builder for a GET method.
     * 
     * A cookie store is automatically added.
     * 
     * @param pathOrUrl a relative path or a full URL.
     * 
     * @param isFullUrl if the 'pathOrUrl' parameter a full URL? If
     * so, it will be used as is. Otherwise it will be appended to the
     * base test URL.
     * 
     * @param isHttps if <code>true</code>, "https:" will be used
     * instead of "http:".
     */
    protected GetRequestBuilder GET(String pathOrUrl, boolean isFullUrl, boolean isHttps) {

        GetRequestBuilder client = getHttpClient().GET(createTestUrl(pathOrUrl, isFullUrl, isHttps));
        if (isDisableSllCetificateErrors()) {
            client.disableSslCertificateErrors();
        }
        return client;
    }

    /**
     * By default, for the tests, by disable the
     * SSL certificate errors.
     */
    protected boolean isDisableSllCetificateErrors() {
        return true;
    }

    /**
     * Starts an Https Client builder for a POST method.
     * 
     * A cookie store is automatically added.
     * 
     * @param path the relative path to be appended to the
     * base test URL.
     */
    protected PostRequestBuilder POST(String path) {
        return POST(path, false, true);
    }

    /**
     * Starts an Https Client builder for a POST method.
     * 
     * A cookie store is automatically added.
     * 
     * @param pathOrUrl a relative path or a full URL.
     * 
     * @param isFullUrl if the 'pathOrUrl' parameter a full URL? If
     * so, it will be used as is. Otherwise it will be appended to the
     * base test URL. 
     */
    protected PostRequestBuilder POST(String pathOrUrl, boolean isFullUrl) {
        return POST(pathOrUrl, isFullUrl, true);
    }

    /**
     * Starts an Http(s) Client builder for a POST method.
     * 
     * A cookie store is automatically added.
     * 
     * @param pathOrUrl a relative path or a full URL.
     * 
     * @param isFullUrl if the 'pathOrUrl' parameter a full URL? If
     * so, it will be used as is. Otherwise it will be appended to the
     * base test URL.
     * 
     * @param isHttps if <code>true</code>, "https:" will be used
     * instead of "http:".
     */
    protected PostRequestBuilder POST(String pathOrUrl, boolean isFullUrl, boolean isHttps) {
        PostRequestBuilder client = getHttpClient().POST(createTestUrl(pathOrUrl, isFullUrl, isHttps));
        if (isDisableSllCetificateErrors()) {
            client.disableSslCertificateErrors();
        }
        return client;
    }

    /**
     * Starts an Https Client builder for a PUT method.
     * 
     * A cookie store is automatically added.
     * 
     * @param path the relative path to be appended to the
     * base test URL.
     */
    protected PutRequestBuilder PUT(String path) {
        return PUT(path, false, true);
    }

    /**
     * Starts an Https Client builder for a PUT method.
     * 
     * A cookie store is automatically added.
     * 
     * @param pathOrUrl a relative path or a full URL.
     * 
     * @param isFullUrl if the 'pathOrUrl' parameter a full URL? If
     * so, it will be used as is. Otherwise it will be appended to the
     * base test URL. 
     */
    protected PutRequestBuilder PUT(String pathOrUrl, boolean isFullUrl) {
        return PUT(pathOrUrl, isFullUrl, true);
    }

    /**
     * Starts an Http(s) Client builder for a PUT method.
     * 
     * A cookie store is automatically added.
     * 
     * @param pathOrUrl a relative path or a full URL.
     * 
     * @param isFullUrl if the 'pathOrUrl' parameter a full URL? If
     * so, it will be used as is. Otherwise it will be appended to the
     * base test URL.
     * 
     * @param isHttps if <code>true</code>, "https:" will be used
     * instead of "http:".
     */
    protected PutRequestBuilder PUT(String pathOrUrl, boolean isFullUrl, boolean isHttps) {
        PutRequestBuilder client = getHttpClient().PUT(createTestUrl(pathOrUrl, isFullUrl, isHttps));
        if (isDisableSllCetificateErrors()) {
            client.disableSslCertificateErrors();
        }
        return client;
    }

    /**
     * Starts an Https Client builder for a DELETE method.
     * 
     * A cookie store is automatically added.
     * 
     * @param path the relative path to be appended to the
     * base test URL.
     */
    protected DeleteRequestBuilder DELETE(String path) {
        return DELETE(path, false, true);
    }

    /**
     * Starts an Https Client builder for a DELETE method.
     * 
     * A cookie store is automatically added.
     * 
     * @param pathOrUrl a relative path or a full URL.
     * 
     * @param isFullUrl if the 'pathOrUrl' parameter a full URL? If
     * so, it will be used as is. Otherwise it will be appended to the
     * base test URL. 
     */
    protected DeleteRequestBuilder DELETE(String pathOrUrl, boolean isFullUrl) {
        return DELETE(pathOrUrl, isFullUrl, true);
    }

    /**
     * Starts an Http(s) Client builder for a DELETE method.
     * 
     * A cookie store is automatically added.
     * 
     * @param pathOrUrl a relative path or a full URL.
     * 
     * @param isFullUrl if the 'pathOrUrl' parameter a full URL? If
     * so, it will be used as is. Otherwise it will be appended to the
     * base test URL.
     * 
     * @param isHttps if <code>true</code>, "https:" will be used
     * instead of "http:".
     */
    protected DeleteRequestBuilder DELETE(String pathOrUrl, boolean isFullUrl, boolean isHttps) {
        DeleteRequestBuilder client = getHttpClient().DELETE(createTestUrl(pathOrUrl, isFullUrl, isHttps));
        if (isDisableSllCetificateErrors()) {
            client.disableSslCertificateErrors();
        }
        return client;
    }

    /**
     * Starts an Https Client builder for a OPTIONS method.
     * 
     * A cookie store is automatically added.
     * 
     * @param path the relative path to be appended to the
     * base test URL.
     */
    protected OptionsRequestBuilder OPTIONS(String path) {
        return OPTIONS(path, false, true);
    }

    /**
     * Starts an Https Client builder for a OPTIONS method.
     * 
     * A cookie store is automatically added.
     * 
     * @param pathOrUrl a relative path or a full URL.
     * 
     * @param isFullUrl if the 'pathOrUrl' parameter a full URL? If
     * so, it will be used as is. Otherwise it will be appended to the
     * base test URL. 
     */
    protected OptionsRequestBuilder OPTIONS(String pathOrUrl, boolean isFullUrl) {
        return OPTIONS(pathOrUrl, isFullUrl, true);
    }

    /**
     * Starts an Http(s) Client builder for a OPTIONS method.
     * 
     * A cookie store is automatically added.
     * 
     * @param pathOrUrl a relative path or a full URL.
     * 
     * @param isFullUrl if the 'pathOrUrl' parameter a full URL? If
     * so, it will be used as is. Otherwise it will be appended to the
     * base test URL.
     * 
     * @param isHttps if <code>true</code>, "https:" will be used
     * instead of "http:".
     */
    protected OptionsRequestBuilder OPTIONS(String pathOrUrl, boolean isFullUrl, boolean isHttps) {
        OptionsRequestBuilder client = getHttpClient().OPTIONS(createTestUrl(pathOrUrl, isFullUrl, isHttps));
        if (isDisableSllCetificateErrors()) {
            client.disableSslCertificateErrors();
        }
        return client;
    }

    /**
     * Starts an Https Client builder for a TRACE method.
     * 
     * A cookie store is automatically added.
     * 
     * @param path the relative path to be appended to the
     * base test URL.
     */
    protected TraceRequestBuilder TRACE(String path) {
        return TRACE(path, false, true);
    }

    /**
     * Starts an Https Client builder for a TRACE method.
     * 
     * A cookie store is automatically added.
     * 
     * @param pathOrUrl a relative path or a full URL.
     * 
     * @param isFullUrl if the 'pathOrUrl' parameter a full URL? If
     * so, it will be used as is. Otherwise it will be appended to the
     * base test URL. 
     */
    protected TraceRequestBuilder TRACE(String pathOrUrl, boolean isFullUrl) {
        return TRACE(pathOrUrl, isFullUrl, true);
    }

    /**
     * Starts an Http(s) Client builder for a TRACE method.
     * 
     * A cookie store is automatically added.
     * 
     * @param pathOrUrl a relative path or a full URL.
     * 
     * @param isFullUrl if the 'pathOrUrl' parameter a full URL? If
     * so, it will be used as is. Otherwise it will be appended to the
     * base test URL.
     * 
     * @param isHttps if <code>true</code>, "https:" will be used
     * instead of "http:".
     */
    protected TraceRequestBuilder TRACE(String pathOrUrl, boolean isFullUrl, boolean isHttps) {
        TraceRequestBuilder client = getHttpClient().TRACE(createTestUrl(pathOrUrl, isFullUrl, isHttps));
        if (isDisableSllCetificateErrors()) {
            client.disableSslCertificateErrors();
        }
        return client;
    }

    /**
     * Starts an Https Client builder for a CONNECT method.
     * 
     * A cookie store is automatically added.
     * 
     * @param path the relative path to be appended to the
     * base test URL.
     */
    protected ConnectRequestBuilder CONNECT(String path) {
        return CONNECT(path, false, true);
    }

    /**
     * Starts an Https Client builder for a CONNECT method.
     * 
     * A cookie store is automatically added.
     * 
     * @param pathOrUrl a relative path or a full URL.
     * 
     * @param isFullUrl if the 'pathOrUrl' parameter a full URL? If
     * so, it will be used as is. Otherwise it will be appended to the
     * base test URL. 
     */
    protected ConnectRequestBuilder CONNECT(String pathOrUrl, boolean isFullUrl) {
        return CONNECT(pathOrUrl, isFullUrl, true);
    }

    /**
     * Starts an Http(s) Client builder for a CONNECT method.
     * 
     * A cookie store is automatically added.
     * 
     * @param pathOrUrl a relative path or a full URL.
     * 
     * @param isFullUrl if the 'pathOrUrl' parameter a full URL? If
     * so, it will be used as is. Otherwise it will be appended to the
     * base test URL.
     * 
     * @param isHttps if <code>true</code>, "https:" will be used
     * instead of "http:".
     */
    protected ConnectRequestBuilder CONNECT(String pathOrUrl, boolean isFullUrl, boolean isHttps) {
        ConnectRequestBuilder client = getHttpClient().CONNECT(createTestUrl(pathOrUrl, isFullUrl, isHttps));
        if (isDisableSllCetificateErrors()) {
            client.disableSslCertificateErrors();
        }
        return client;
    }

    /**
     * Starts an Https Client builder for a PATCH method.
     * 
     * A cookie store is automatically added.
     * 
     * @param path the relative path to be appended to the
     * base test URL.
     */
    protected PatchRequestBuilder PATCH(String path) {
        return PATCH(path, false, true);
    }

    /**
     * Starts an Https Client builder for a PATCH method.
     * 
     * A cookie store is automatically added.
     * 
     * @param pathOrUrl a relative path or a full URL.
     * 
     * @param isFullUrl if the 'pathOrUrl' parameter a full URL? If
     * so, it will be used as is. Otherwise it will be appended to the
     * base test URL. 
     */
    protected PatchRequestBuilder PATCH(String pathOrUrl, boolean isFullUrl) {
        return PATCH(pathOrUrl, isFullUrl, true);
    }

    /**
     * Starts an Http(s) Client builder for a PATCH method.
     * 
     * A cookie store is automatically added.
     * 
     * @param pathOrUrl a relative path or a full URL.
     * 
     * @param isFullUrl if the 'pathOrUrl' parameter a full URL? If
     * so, it will be used as is. Otherwise it will be appended to the
     * base test URL.
     * 
     * @param isHttps if <code>true</code>, "https:" will be used
     * instead of "http:".
     */
    protected PatchRequestBuilder PATCH(String pathOrUrl, boolean isFullUrl, boolean isHttps) {
        PatchRequestBuilder client = getHttpClient().PATCH(createTestUrl(pathOrUrl, isFullUrl, isHttps));
        if (isDisableSllCetificateErrors()) {
            client.disableSslCertificateErrors();
        }
        return client;
    }

    /**
     * Starts an Https Client builder for a HEAD method.
     * 
     * A cookie store is automatically added.
     * 
     * @param path the relative path to be appended to the
     * base test URL.
     */
    protected HeadRequestBuilder HEAD(String path) {
        return HEAD(path, false, true);
    }

    /**
     * Starts an Https Client builder for a HEAD method.
     * 
     * A cookie store is automatically added.
     * 
     * @param pathOrUrl a relative path or a full URL.
     * 
     * @param isFullUrl if the 'pathOrUrl' parameter a full URL? If
     * so, it will be used as is. Otherwise it will be appended to the
     * base test URL. 
     */
    protected HeadRequestBuilder HEAD(String pathOrUrl, boolean isFullUrl) {
        return HEAD(pathOrUrl, isFullUrl, true);
    }

    /**
     * Starts an Http(s) Client builder for a HEAD method.
     * 
     * A cookie store is automatically added.
     * 
     * @param pathOrUrl a relative path or a full URL.
     * 
     * @param isFullUrl if the 'pathOrUrl' parameter a full URL? If
     * so, it will be used as is. Otherwise it will be appended to the
     * base test URL.
     * 
     * @param isHttps if <code>true</code>, "https:" will be used
     * instead of "http:".
     */
    protected HeadRequestBuilder HEAD(String pathOrUrl, boolean isFullUrl, boolean isHttps) {
        HeadRequestBuilder client = getHttpClient().HEAD(createTestUrl(pathOrUrl, isFullUrl, isHttps));
        if (isDisableSllCetificateErrors()) {
            client.disableSslCertificateErrors();
        }
        return client;
    }

    /**
     * Format a date so it can be used in a HTTP header.
     */
    protected String formatDate(Date date) {
        Objects.requireNonNull(date, "The date can't be NULL");
        return DateUtils.formatDate(date);
    }

    /**
     * Parse a date from a HTTP header to a Date object.
     */
    protected Date parseDate(String dateHeaderValue) {
        Objects.requireNonNull(dateHeaderValue, "The dateHeaderValue can't be NULL");
        return DateUtils.parseDate(dateHeaderValue);
    }

    /**
     * The {@link Cookie}s returned by the previous
     * {@link HttpResponse}. This allows you to simulate
     * a real browser which would automatically resend
     * cookies on a particular domain. Example:
     * <pre>
     * GET("/").addCookies(getPreviousResponseCookies())...
     * </pre>
     * <p>
     * Note that for this to work, <em>you have to manually 
     * save the cookies</em> using {@link #saveResponseCookies(HttpResponse)} 
     * when a response is received!
     */
    protected Set<Cookie> getPreviousResponseCookies() {
        if (this.previousResponseCookies == null) {
            this.previousResponseCookies = new HashSet<Cookie>();
        }
        return this.previousResponseCookies;
    }

    protected Cookie getPreviousResponseCookie(String cookieName) {
        if (cookieName == null) {
            return null;
        }

        for (Cookie cookie : getPreviousResponseCookies()) {
            if (cookie != null && cookieName.equals(cookie.getName())) {
                return cookie;
            }
        }
        return null;
    }

    /**
     * Removes all the cookies saved from a previous
     * response.
     */
    protected void clearPreviousResponseCookies() {
        getPreviousResponseCookies().clear();
    }

    /**
     * Saves the current response's cookies.<p>
     * You would then be able to resend them in a
     * request by using {@link #getPreviousResponseCookies()}.
     * Example: 
     * <pre>
     * GET("/").addCookies(getPreviousResponseCookies())...
     * </pre>
     */
    protected void saveResponseCookies(HttpResponse response) {
        clearPreviousResponseCookies();
        if (response == null) {
            return;
        }

        Map<String, Cookie> cookies = response.getCookies();
        if (cookies == null || cookies.size() == 0) {
            return;
        }

        for (Cookie cookie : cookies.values()) {
            if (isSetSecureFalseOnCookiesFromBag()) {
                cookie.setSecure(false);
                getPreviousResponseCookies().add(cookie);
            }
        }
    }

    /**
     * Will set {@link Cookie#setSecure(boolean)} to
     * <code>false</code> on cookies added to the 
     * {@link #getPreviousResponseCookies()} so their can be resend
     * in tests file using HTTP (not HTTPS).
     */
    protected boolean isSetSecureFalseOnCookiesFromBag() {
        return true;
    }

    /**
     * We force test classes to provide information about
     * the required testing configurations.
     * <p>
     * The bindings for those components will be automatically
     * created.
     * 
     * @return the testing configs informations or <code>null</code>
     * to disable this process (you will then have to add the
     * required config bindings by yourself).
     */
    protected abstract AppTestingConfigs getAppTestingConfigs();

    /**
     * Starts the application.
     * <p>
     * In this method, you should call your
     * application <code>main()</code> method. 
     * <p>
     * There is no need to start the 
     * {@link Server} here, since the target
     * application is supposed to do it by itself,
     * in general in an "{@literal @}Inject init()" 
     * method!
     */
    protected abstract void callAppMainMethod();

}
