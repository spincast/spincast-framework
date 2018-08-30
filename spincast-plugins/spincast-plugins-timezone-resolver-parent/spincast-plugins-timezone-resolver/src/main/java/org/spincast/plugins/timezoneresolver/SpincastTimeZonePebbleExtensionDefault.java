package org.spincast.plugins.timezoneresolver;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.spincast.core.config.SpincastConfig;
import org.spincast.core.exchange.RequestContext;
import org.spincast.core.timezone.TimeZoneResolver;
import org.spincast.core.utils.SpincastUtils;
import org.spincast.plugins.timezoneresolver.config.SpincastTimeZoneResolverConfig;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.OutOfScopeException;
import com.google.inject.Provider;
import com.google.inject.ProvisionException;
import com.mitchellbosecke.pebble.extension.AbstractExtension;
import com.mitchellbosecke.pebble.extension.Function;
import com.mitchellbosecke.pebble.extension.escaper.SafeString;


public class SpincastTimeZonePebbleExtensionDefault extends AbstractExtension
                                                    implements SpincastTimeZonePebbleExtension {

    public static final String FONCTION_NAME_TIMEZONE_COOKIE = "timeZoneCookie";

    private final TimeZoneResolver timeZoneResolver;
    private final SpincastConfig spincastConfig;
    private final SpincastUtils spincastUtils;
    private final Provider<RequestContext<?>> requestContextProvider;
    private final SpincastTimeZoneResolverConfig spincastTimeZoneResolverConfig;


    @Inject
    public SpincastTimeZonePebbleExtensionDefault(TimeZoneResolver timeZoneResolver,
                                                  SpincastConfig spincastConfig,
                                                  SpincastUtils spincastUtils,
                                                  Provider<RequestContext<?>> requestContextProvider,
                                                  SpincastTimeZoneResolverConfig spincastTimeZoneResolverConfig) {
        this.timeZoneResolver = timeZoneResolver;
        this.spincastConfig = spincastConfig;
        this.spincastUtils = spincastUtils;
        this.requestContextProvider = requestContextProvider;
        this.spincastTimeZoneResolverConfig = spincastTimeZoneResolverConfig;
    }

    protected TimeZoneResolver getTimeZoneResolver() {
        return this.timeZoneResolver;
    }

    protected SpincastConfig getSpincastConfig() {
        return this.spincastConfig;
    }

    protected SpincastTimeZoneResolverConfig getSpincastTimeZoneResolverConfig() {
        return this.spincastTimeZoneResolverConfig;
    }

    protected SpincastUtils getSpincastUtils() {
        return this.spincastUtils;
    }

    protected Provider<RequestContext<?>> getRequestContextProvider() {
        return this.requestContextProvider;
    }

    @Override
    public Map<String, Function> getFunctions() {

        Map<String, Function> functions = new HashMap<>();
        functions.put(FONCTION_NAME_TIMEZONE_COOKIE, getTimeZoneCookieFunction());

        return functions;
    }

    protected Function getTimeZoneCookieFunction() {

        String jstz = getSpincastUtils().readClasspathFile("spincast/spincast-plugins-timezone-resolver/jstz.2.0.0.min.js");

        //==========================================
        // Changes the exported obj name so it doesn't 
        // clash with anything
        //==========================================
        String jstzClean = jstz.replace(".jstz", ".spincast_jstz");

        return new Function() {

            @Override
            public List<String> getArgumentNames() {
                return Lists.newArrayList("addScriptTag", "reloadPageIfSet");
            }

            @Override
            public Object execute(Map<String, Object> args) {

                //==========================================
                // Cookie already set?
                //==========================================
                try {
                    RequestContext<?> context = getRequestContextProvider().get();
                    String cookieValue = context.request().getCookieValue(getSpincastConfig().getCookieNameTimeZoneId());
                    if (cookieValue != null) {
                        return "";
                    }
                } catch (OutOfScopeException | ProvisionException ex) {
                    // ok, output the script
                }

                boolean addScriptTag = true;
                Object addScriptTagObj = args.get("addScriptTag");
                if (addScriptTagObj != null && addScriptTagObj.toString().toLowerCase().equals("false")) {
                    addScriptTag = false;
                }

                OffsetDateTime oneHourFromNow =
                        OffsetDateTime.now(ZoneOffset.UTC)
                                      .plus(Duration.ofHours(getSpincastTimeZoneResolverConfig().getPebbleTimeZoneCookieExpiredHoursNbr()));
                String expiresDate = DateTimeFormatter.RFC_1123_DATE_TIME.format(oneHourFromNow);

                StringBuilder b = new StringBuilder();

                if (addScriptTag) {
                    b.append("<script>\n");
                }

                b.append(jstzClean).append("\n");
                b.append("(function(){\n");

                b.append("document.cookie = \"");
                b.append(getSpincastConfig().getCookieNameTimeZoneId());
                b.append("=\" + spincast_jstz.determine().name() + \"; ");
                b.append("domain=").append(getSpincastTimeZoneResolverConfig().getPebbleTimeZoneCookieDomain()).append("; path=")
                 .append(getSpincastTimeZoneResolverConfig().getPebbleTimeZoneCookiePath())
                 .append("; ");
                b.append("expires=").append(expiresDate).append(";\";\n");
                if (getSpincastTimeZoneResolverConfig().isRefreshPageAfterAddingPebbleTimeZoneCookie()) {

                    b.append("var url = window.location.href;\n");
                    b.append("var part = '").append(getSpincastTimeZoneResolverConfig().getPebbleTimeZoneCookieReloadingQsParamName())
                     .append("=1';\n");

                    //==========================================
                    // We only reload the page if the cookie can
                    // now be read and the page has not already
                    // been reloaded!
                    //==========================================
                    b.append("if (document.cookie.includes('")
                     .append(getSpincastConfig().getCookieNameTimeZoneId()).append("=")
                     .append("') && !url.includes(part)) {\n");

                    //==========================================
                    // Thanks : https://stackoverflow.com/questions/486896/adding-a-parameter-to-the-url-with-javascript#comment11988623_4811158
                    //==========================================
                    b.append("url = (url.indexOf(\"?\") != -1 ? url.split(\"?\")[0]+\"?\"+part+\"&\"+url.split(\"?\")[1] : (url.indexOf(\"#\") != -1 ? url.split(\"#\")[0]+\"?\"+part+\"#\"+ url.split(\"#\")[1] : url+'?'+part));\n");
                    b.append("window.location.href = url;\n");

                    b.append("}\n");
                }

                b.append("})()\n");

                if (addScriptTag) {
                    b.append("</script>\n");
                }

                String script = b.toString();
                return new SafeString(script);
            }
        };
    }
}
