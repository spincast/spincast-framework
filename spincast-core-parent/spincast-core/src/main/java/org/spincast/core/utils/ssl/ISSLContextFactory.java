package org.spincast.core.utils.ssl;

import javax.net.ssl.SSLContext;

public interface ISSLContextFactory {

    public SSLContext createSSLContext(String keyStorePath,
                                       String keyStoreType,
                                       String keyStoreStorePass,
                                       String keyStoreKeypass);
}
