package org.spincast.testing.core.postgres;

import java.io.File;

import javax.annotation.Nullable;

import org.spincast.core.utils.SpincastUtils;
import org.spincast.testing.core.utils.SpincastTestingUtils;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class SpincastTestingPostgresConfigDefault implements SpincastTestingPostgresConfig {

    private final SpincastUtils spincastUtils;
    private File dataDir = null;
    private int pgPortToUse = -1;

    @Inject
    public SpincastTestingPostgresConfigDefault(@Nullable @PostgresDataDir File dataDir,
                                                SpincastUtils spincastUtils) {
        this.dataDir = dataDir;
        this.spincastUtils = spincastUtils;
    }

    protected SpincastUtils getSpincastUtils() {
        return this.spincastUtils;
    }

    @Override
    public File getDataTempDir() {
        if (this.dataDir == null) {
            String dirPath = getSpincastUtils().createTempFilePath();
            this.dataDir = new File(dirPath);
            boolean result = this.dataDir.mkdirs();
            if (!result) {
                throw new RuntimeException("Unable to create a temp dir at: " + dirPath);

            }
        }
        return this.dataDir;
    }

    @Override
    public int getPortToUse() {
        if (this.pgPortToUse < 0) {
            this.pgPortToUse = SpincastTestingUtils.findFreePort();
        }
        return this.pgPortToUse;
    }

    @Override
    public boolean isResetSchemaOnInit() {
        return true;
    }

}
