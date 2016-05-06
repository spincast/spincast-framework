package org.spincast.plugins.configpropsfile;

/**
 * Interface allowing to retrieve configurations using
 * an untyped,plain text key.
 */
public interface IFreeKeyConfig {

    public String getConfig(String key);

    public String getConfig(String key, String defaultValue);

    public Boolean getConfigBoolean(String key);

    public Boolean getConfigBoolean(String key, Boolean defaultValue);

    public Integer getConfigInteger(String key);

    public Integer getConfigInteger(String key, Integer defaultValue);

}
