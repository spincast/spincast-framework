package org.spincast.testing.core.postgres;

import java.io.File;

import com.google.inject.ImplementedBy;

@ImplementedBy(SpincastTestingPostgresConfigDefault.class)
public interface SpincastTestingPostgresConfig {

    public File getDataTempDir();

}
