package org.spincast.testing.core.h2;

import com.google.inject.Singleton;

@Singleton
public class SpincastTestingH2ConfigDefault implements SpincastTestingH2Config {

    @Override
    public String getServerHost() {
        return "localhost";
    }

    @Override
    public String getDatabaseName() {
        return "test";
    }

    @Override
    public Integer getServerPort() {
        return 9092;
    }

    @Override
    public String getCompatibilityMode() {
        return "PostgreSQL";
    }

    @Override
    public boolean isDatabaseToUpper() {
        return false;
    }

}
