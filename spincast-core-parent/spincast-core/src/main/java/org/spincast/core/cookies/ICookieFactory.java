package org.spincast.core.cookies;

import java.util.Date;

import com.google.inject.assistedinject.Assisted;

/**
 * Factory to create cookies.
 */
public interface ICookieFactory {

    /**
     * Creates a cookie using the given name (<code>null</code> value).
     */
    public ICookie createCookie(@Assisted("name") String name);

    /**
     * Creates a cookie using the given name and value.
     */
    public ICookie createCookie(@Assisted("name") String name,
                                @Assisted("value") String value);

    /**
     * Creates a cookie using all possible configurations.
     */
    public ICookie createCookie(@Assisted("name") String name,
                                @Assisted("value") String value,
                                @Assisted("path") String path,
                                @Assisted("domain") String domain,
                                @Assisted("expires") Date expires,
                                @Assisted("secure") boolean secure,
                                @Assisted("httpOnly") boolean httpOnly,
                                @Assisted("discard") boolean discard,
                                @Assisted("version") int version);
}
