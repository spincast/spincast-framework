package org.spincast.plugins.routing;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.spincast.core.routing.ETag;
import org.spincast.core.routing.ETagFactory;
import org.spincast.shaded.org.apache.commons.lang3.StringUtils;

import com.google.inject.Inject;

/**
 * Default ETag factory.
 */
public class ETagFactoryDefault implements ETagFactory {

    protected static final Pattern ETAG_HEADER_VALUE_PATTERN = Pattern.compile("^(\\*)$|^([wW]/)?\"([^\"]*)\"$");

    /**
     * Constructor
     */
    @Inject
    public ETagFactoryDefault() {
        //...
    }

    @Override
    public ETag create(String tag) {
        return create(tag, false);
    }

    @Override
    public ETag create(String tag, boolean isWeak) {
        return new ETagDefault(tag, isWeak);
    }

    @Override
    public ETag create(String tag, boolean isWeak, boolean isWildcard) {
        return new ETagDefault(tag, isWeak, isWildcard);
    }

    @Override
    public ETag deserializeHeaderValue(String etagHeaderValue) {

        if(StringUtils.isBlank(etagHeaderValue)) {
            throw new RuntimeException("The ETag value can't be empty.");
        }

        etagHeaderValue = etagHeaderValue.trim();

        Matcher matcher = ETAG_HEADER_VALUE_PATTERN.matcher(etagHeaderValue);
        if(matcher.find()) {

            boolean isWeak = false;
            if(matcher.group(2) != null) {
                isWeak = true;
            }

            String tag = "";
            boolean isWildcard = false;
            if(matcher.group(1) != null) {
                isWildcard = true;
            } else {
                tag = matcher.group(3);
            }

            return new ETagDefault(tag, isWeak, isWildcard);

        } else {
            throw new RuntimeException("Invalid ETag header value format: " + etagHeaderValue);
        }
    }

}
