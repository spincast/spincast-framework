package org.spincast.core.cookies;

import java.util.Date;

/**
 * An HTTP cookie.
 */
public interface Cookie {

    /**
     * Gets the cookie name.
     */
    public String getName();

    /**
     * Gets the cookie value.
     */
    public String getValue();

    /**
     * Sets the cookie value.
     */
    public void setValue(String value);

    /**
     * Gets the cookie path.
     */
    public String getPath();

    /**
     * Sets the cookie path.
     */
    public void setPath(String path);

    /**
     * Gets the cookie domain.
     */
    public String getDomain();

    /**
     * Sets the cookie domain.
     */
    public void setDomain(String domain);

    /**
     * Gets the date the cookie will expire at.
     * 
     * If the date is in the past, the cookie will be deleted.
     *    
     * If <code>null</code> the cookie will live for the current session 
     * (this is the default).
     */
    public Date getExpires();

    /**
     * Sets the date the cookie will expire.
     * 
     * If the date is in the past, the cookie will be deleted.
     *    
     * If <code>null</code> the cookie will live for the current session 
     * (this is the default).
     */
    public void setExpires(Date expires);

    /**
     * Sets the number of seconds for a cookie to live.
     * 
     * If <code>maxAge</code> &lt; 0 : The "Expires date" will be in the past and the cookie will
     * therefore be deleted.
     *    
     * If <code>maxAge</code> == 0 : The "Expires date" will be <code>null</code> and the cookie will live
     * for the session only.
     * 
     * If <code>maxAge</code> &gt; 0 :  The "Expires date" will be the current date + <code>'maxAge'</code> seconds.
     */
    public void setExpiresUsingMaxAge(int maxAge);

    /**
     * Is this cookie expired?
     */
    public boolean isExpired();

    /**
     * Is the "secure" feature on?
     */
    public boolean isSecure();

    /**
     * Sets the "secure" feature on or off.
     */
    public void setSecure(boolean secure);

    /**
     * Is the cookie availableto the server
     * and not to javascript?
     */
    public boolean isHttpOnly();

    /**
     * Sets if the cookie is available only for to the server
     * anbd not to javascript.
     */
    public void setHttpOnly(boolean httpOnly);

    /**
     * The "sameSite" attribute. May be 
     * <code>null</code> to not add it at all.
     */
    public CookieSameSite getSameSite();

    /**
     * The "sameSite" attribute to add.
     */
    public void setSameSite(CookieSameSite sameSite);

    /**
     * Is this cookie to be discarded?
     */
    public boolean isDiscard();

    /**
     * Gets the cookie version.
     */
    public int getVersion();

}
