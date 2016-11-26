package org.spincast.plugins.routing;

import java.util.Set;

import org.spincast.core.routing.StaticResourceCorsConfig;

import com.google.inject.assistedinject.Assisted;

public interface StaticResourceCorsConfigFactory {

    public StaticResourceCorsConfig create(@Assisted("allowedOrigins") Set<String> allowedOrigins,
                                           @Assisted("extraHeadersAllowedToBeRead") Set<String> extraHeadersAllowedToBeRead,
                                           @Assisted("extraHeadersAllowedToBeSent") Set<String> extraHeadersAllowedToBeSent,
                                           @Assisted("allowCookies") boolean allowCookies,
                                           @Assisted("maxAgeInSeconds") int maxAgeInSeconds);
}
