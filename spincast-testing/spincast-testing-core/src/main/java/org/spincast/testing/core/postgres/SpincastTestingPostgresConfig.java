package org.spincast.testing.core.postgres;

import java.io.File;

import com.google.inject.ImplementedBy;

@ImplementedBy(SpincastTestingPostgresConfigDefault.class)
public interface SpincastTestingPostgresConfig {

    public File getDataTempDir();

    /**
     * The port to use.
     * <p>
     * By default, a random free port is picked.
     */
    public int getPortToUse();

    /**
     * Will reset the "public" schema if it exists
     * at startup.
     */
    public boolean isResetSchemaOnInit();

}
