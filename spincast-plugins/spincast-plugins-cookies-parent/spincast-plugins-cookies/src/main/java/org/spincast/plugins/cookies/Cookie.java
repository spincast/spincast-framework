package org.spincast.plugins.cookies;

import java.util.Date;

import javax.annotation.Nullable;

import org.spincast.core.cookies.ICookie;
import org.spincast.shaded.org.apache.commons.lang3.time.DateUtils;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

public class Cookie implements ICookie {

    private final String name;

    private String value;
    private String path;
    private String domain;
    private Date expires;
    private boolean secure;
    private boolean httpOnly;
    private boolean discard;
    private int version = 0;

    /**
     * Constructor
     */
    @AssistedInject
    public Cookie(@Assisted("name") String name) {
        this.name = name;
    }

    /**
     * Constructor
     */
    @AssistedInject
    public Cookie(@Assisted("name") String name,
                  @Assisted("value") String value) {
        this.name = name;
        this.value = value;
    }

    /**
     * Constructor
     */
    @AssistedInject
    public Cookie(@Assisted("name") String name,
                  @Assisted("value") String value,
                  @Assisted("path") @Nullable String path,
                  @Assisted("domain") @Nullable String domain,
                  @Assisted("expires") @Nullable Date expires,
                  @Assisted("secure") boolean secure,
                  @Assisted("httpOnly") boolean httpOnly,
                  @Assisted("discard") boolean discard,
                  @Assisted("version") int version) {

        this.name = name;
        this.value = value;
        this.path = path;
        this.domain = domain;
        this.expires = expires;
        this.secure = secure;
        this.httpOnly = httpOnly;
        this.discard = discard;
        this.version = version;
    }

    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String getPath() {
        return this.path;
    }

    @Override
    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String getDomain() {
        return this.domain;
    }

    @Override
    public void setDomain(String domain) {
        this.domain = domain;
    }

    @Override
    public Date getExpires() {
        return this.expires;
    }

    @Override
    public void setExpires(Date expires) {
        this.expires = expires;
    }

    @Override
    public void setExpiresUsingMaxAge(Integer maxAge) {

        if(maxAge == null || maxAge.equals(0)) {
            setExpires(null);
        } else if(maxAge < 0) {
            Date date = DateUtils.addYears(new Date(), -1);
            setExpires(date);
        } else {
            Date date = DateUtils.addSeconds(new Date(), maxAge.intValue());
            setExpires(date);
        }
    }

    @Override
    public boolean isExpired() {

        Date expire = getExpires();
        if(expire == null) {
            return false;
        }

        return expire.before(new Date());
    }

    @Override
    public boolean isSecure() {
        return this.secure;
    }

    @Override
    public void setSecure(boolean secure) {
        this.secure = secure;
    }

    @Override
    public boolean isHttpOnly() {
        return this.httpOnly;
    }

    @Override
    public void setHttpOnly(boolean httpOnly) {
        this.httpOnly = httpOnly;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public boolean isDiscard() {
        return this.discard;
    }

    @Override
    public int getVersion() {
        return this.version;
    }

    @Override
    public String toString() {
        return getName() + " - " + getValue();
    }

}
