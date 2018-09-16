package org.spincast.testing.core.h2;

import com.google.inject.ImplementedBy;

@ImplementedBy(SpincastTestingH2ConfigDefault.class)
public interface SpincastTestingH2Config {

    /**
     * The host on which to start the server.
     * Should probably be "localhost".
     */
    public String getServerHost();

    /**
     * The name of the database. Defaults
     * to "test".
     */
    public String getDatabaseName();

    /**
     * The port on which to start the H2
     * TCP server. If <code>null</code>
     * a random free port will be used.
     * <p>
     * Defaults to 9092.
     */
    public Integer getServerPort();

    /**
     * Should a compatibility mode be used by H2?
     * Be default, "MODE=PostgreSQL" will be
     * used.
     * <p>
     * @see http://www.h2database.com/html/features.html#compatibility
     */
    public String getCompatibilityMode();

    /**
     * Defaults to <code>false</code>.
     * 
     * @see https://www.h2database.com/javadoc/org/h2/engine/DbSettings.html#DATABASE_TO_UPPER
     */
    public boolean isDatabaseToUpper();

}
