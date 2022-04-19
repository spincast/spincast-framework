package org.spincast.testing.core.postgres;

import java.sql.Connection;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.plugins.jdbc.JdbcQueries;
import org.spincast.plugins.jdbc.JdbcUtils;
import org.spincast.plugins.jdbc.SpincastDataSource;
import org.spincast.plugins.jdbc.SpincastDataSourceFactory;
import org.spincast.plugins.jdbc.statements.UpdateStatement;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.opentable.db.postgres.embedded.EmbeddedPostgres;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Singleton
public class SpincastTestingPostgres implements Provider<SpincastDataSource> {

    protected final static Logger logger = LoggerFactory.getLogger(SpincastTestingPostgres.class);

    private final SpincastTestingPostgresConfig spincastTestingPostgresConfig;
    private final JdbcUtils jdbcUtils;
    private final SpincastDataSourceFactory spincastDataSourceFactory;
    private EmbeddedPostgres pg = null;
    private int pgPort;
    private SpincastDataSource dataSource;

    @Inject
    public SpincastTestingPostgres(SpincastTestingPostgresConfig spincastTestingPostgresConfig,
                                   JdbcUtils jdbcUtils,
                                   SpincastDataSourceFactory spincastDataSourceFactory) {
        this.spincastTestingPostgresConfig = spincastTestingPostgresConfig;
        this.jdbcUtils = jdbcUtils;
        this.spincastDataSourceFactory = spincastDataSourceFactory;
    }

    protected EmbeddedPostgres getPg() {
        return this.pg;
    }

    protected SpincastTestingPostgresConfig getSpincastTestingPostgresConfig() {
        return this.spincastTestingPostgresConfig;
    }

    protected JdbcUtils getJdbcUtils() {
        return this.jdbcUtils;
    }

    protected SpincastDataSourceFactory getSpincastDataSourceFactory() {
        return this.spincastDataSourceFactory;
    }

    @Inject
    public void init() {
        try {
            this.pgPort = getPortToUse();

            logger.info("Starting embedded PostgreSQL on port " + this.pgPort + ". Please wait...");

            this.pg = EmbeddedPostgres.builder()
                                      .setPort(this.pgPort)
                                      .setDataDirectory(getSpincastTestingPostgresConfig().getDataTempDir())
                                      .setCleanDataDirectory(true)
                                      .start();

            logger.info("Embedded PostgreSQL started.");
            logger.info(getDbConnectionString());
            if (isLogCredentials()) {
                logger.info("Credentials: " + getDbUsername() + "/" + getDbPassword());
            }

            if (getSpincastTestingPostgresConfig().isResetSchemaOnInit()) {
                try {
                    clearDatabase();
                } catch (Exception ex) {
                    logger.warn("Error clearing the database at startup", ex);
                }
            }
        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    protected boolean isLogCredentials() {
        return true;
    }

    protected int getPortToUse() {
        return getSpincastTestingPostgresConfig().getPortToUse();
    }

    @Override
    public SpincastDataSource get() {
        if (this.dataSource == null) {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(getDbConnectionString());
            config.setUsername(getDbUsername());
            config.setPassword(getDbPassword());
            config.setMaximumPoolSize(getDbMaxPoolSize());
            DataSource ds = new HikariDataSource(config);
            this.dataSource = getSpincastDataSourceFactory().create(ds);
        }
        return this.dataSource;
    }

    public String getDbConnectionString() {
        StringBuilder b = new StringBuilder();
        b.append("jdbc:postgresql://localhost:").append(getPg().getPort()).append("/").append(getDbName());
        return b.toString();
    }

    public int getDbPort() {
        return this.pgPort;
    }

    public String getDbName() {
        return "postgres";
    }

    public String getDbUsername() {
        return "postgres";
    }

    public String getDbPassword() {
        return "postgres";
    }

    public int getDbMaxPoolSize() {
        return 10;
    }

    /**
     * Stops Postgres.
     */
    public void stopPostgres() {
        if (this.pg != null) {
            try {
                this.pg.close();
            } catch (Exception ex) {
                logger.warn("Error stopping the embedded PostgreSQL instance", ex);
            }
        }
    }

    /**
     * Clears the whole database.
     */
    public void clearDatabase() {
        getJdbcUtils().scopes().autoCommit(get(), new JdbcQueries<Void>() {

            @Override
            public Void run(Connection connection) {

                UpdateStatement stm = getJdbcUtils().statements().createUpdateStatement(connection);
                stm.sql("DROP SCHEMA public CASCADE ");
                stm.update();

                stm = getJdbcUtils().statements().createUpdateStatement(connection);
                stm.sql("CREATE SCHEMA public ");
                stm.update();
                return null;
            }
        });
    }


}
