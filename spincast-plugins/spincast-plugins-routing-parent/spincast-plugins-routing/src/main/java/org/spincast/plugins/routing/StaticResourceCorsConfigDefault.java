package org.spincast.plugins.routing;

import java.util.Set;

import javax.annotation.Nullable;

import org.spincast.core.routing.StaticResourceCorsConfig;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

public class StaticResourceCorsConfigDefault implements StaticResourceCorsConfig {

    private final Set<String> allowedOrigins;
    private final Set<String> extraHeadersAllowedToBeRead;
    private final Set<String> extraHeadersAllowedToBeSent;
    private final boolean allowCookies;
    private final int maxAgeInSeconds;

    @AssistedInject
    public StaticResourceCorsConfigDefault(@Assisted("allowedOrigins") Set<String> allowedOrigins,
                                           @Assisted("extraHeadersAllowedToBeRead") @Nullable Set<String> extraHeadersAllowedToBeRead,
                                           @Assisted("extraHeadersAllowedToBeSent") @Nullable Set<String> extraHeadersAllowedToBeSent,
                                           @Assisted("allowCookies") boolean allowCookies,
                                           @Assisted("maxAgeInSeconds") int maxAgeInSeconds) {
        this.allowedOrigins = allowedOrigins;
        this.extraHeadersAllowedToBeRead = extraHeadersAllowedToBeRead;
        this.extraHeadersAllowedToBeSent = extraHeadersAllowedToBeSent;
        this.allowCookies = allowCookies;
        this.maxAgeInSeconds = maxAgeInSeconds;
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
    public Set<String> getExtraHeadersAllowedToBeSent() {
        return this.extraHeadersAllowedToBeSent;
    }

    @Override
    public boolean isAllowCookies() {
        return this.allowCookies;
    }

    @Override
    public int getMaxAgeInSeconds() {
        return this.maxAgeInSeconds;
    }

}
