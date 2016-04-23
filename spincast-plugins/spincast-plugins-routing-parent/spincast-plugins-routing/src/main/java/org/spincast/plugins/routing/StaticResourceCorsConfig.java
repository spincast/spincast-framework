package org.spincast.plugins.routing;

import java.util.Set;

import javax.annotation.Nullable;

import org.spincast.core.routing.IStaticResourceCorsConfig;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

public class StaticResourceCorsConfig implements IStaticResourceCorsConfig {

    private final Set<String> allowedOrigins;
    private final Set<String> extraHeadersAllowedToBeRead;
    private final boolean allowCookies;

    @AssistedInject
    public StaticResourceCorsConfig(@Assisted("allowedOrigins") Set<String> allowedOrigins,
                                    @Assisted("extraHeadersAllowedToBeRead") @Nullable Set<String> extraHeadersAllowedToBeRead,
                                    @Assisted boolean allowCookies) {
        this.allowedOrigins = allowedOrigins;
        this.extraHeadersAllowedToBeRead = extraHeadersAllowedToBeRead;
        this.allowCookies = allowCookies;
    }

    @Override
    public Set<String> getAllowedOrigins() {
        return this.allowedOrigins;
    }

    @Override
    public Set<String> getExtraHeadersAllowedToBeRead() {
        return this.extraHeadersAllowedToBeRead;
    }

    @Override
    public boolean isAllowCookies() {
        return this.allowCookies;
    }

}
