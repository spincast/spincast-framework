package org.spincast.plugins.routing;

import org.spincast.core.routing.IETag;
import org.spincast.shaded.org.apache.commons.lang3.StringUtils;
import org.spincast.shaded.org.apache.commons.lang3.builder.EqualsBuilder;
import org.spincast.shaded.org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * ETag default implementation.
 */
public class ETag implements IETag {

    private final String tag;
    private final boolean isWeak;
    private final boolean isWildcard;
    private String headerValue = null;

    /**
     * Constructor
     */
    public ETag(String tag) {
        this(tag, false, false);
    }

    /**
     * Constructor
     */
    public ETag(String tag, boolean isWeak) {
        this(tag, isWeak, false);
    }

    /**
     * Constructor
     */
    public ETag(String tag, boolean isWeak, boolean isWildcard) {

        if(StringUtils.isBlank(tag)) {
            if(!isWildcard) {
                throw new RuntimeException("The tag can't be empty for an ETag which is not a wildcard.");
            } else {
                tag = "*";
            }
        }

        if(isWildcard && isWeak) {
            throw new RuntimeException("A wildcard ETag can't be weak!");
        }

        if(isWildcard && !StringUtils.isBlank(tag) && !"*".equals(tag)) {
            throw new RuntimeException("A wildcard ETag must have an empty tag ('*' is also accepted).");
        }

        if(tag.indexOf('"') > -1) {
            throw new RuntimeException("The tag can't contain '\"'.");
        }

        if(isWildcard) {
            tag = "*";
        }

        this.tag = tag;
        this.isWeak = isWeak;
        this.isWildcard = isWildcard;
    }

    @Override
    public String getTag() {
        return this.tag;
    }

    @Override
    public boolean isWeak() {
        return this.isWeak;
    }

    @Override
    public boolean isWildcard() {
        return this.isWildcard;
    }

    @Override
    public boolean equals(Object obj) {

        if(this == obj) {
            return true;
        }

        if(obj == null) {
            return false;
        }

        if(!(obj instanceof IETag)) {
            return false;
        }

        IETag other = (IETag)obj;

        return new EqualsBuilder().append(getTag(), other.getTag())
                                  .append(isWeak(), other.isWeak())
                                  .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(getTag())
                                    .append(isWeak())
                                    .toHashCode();
    }

    @Override
    public String getHeaderValue() {

        if(this.headerValue == null) {

            if(isWildcard()) {
                this.headerValue = "*";
            } else {
                StringBuilder builder = new StringBuilder();
                if(isWeak()) {
                    builder.append("W/");
                }
                builder.append('"').append(getTag()).append('"');
                this.headerValue = builder.toString();
            }
        }

        return this.headerValue;
    }

    @Override
    public boolean matches(IETag other) {
        return matches(other, false);
    }

    @Override
    public boolean matches(IETag other, boolean weakComparison) {

        if(isWildcard() || (other != null && other.isWildcard())) {
            return true;
        }

        if(other == null) {
            return false;
        }

        if(!getTag().equals(other.getTag())) {
            return false;
        }

        if(!weakComparison && (isWeak() || other.isWeak())) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return getHeaderValue();
    }

}
