package org.spincast.testing.core.postgres;

import java.sql.Connection;

import javax.sql.DataSource;

import org.spincast.core.utils.SpincastStatics;
import org.spincast.plugins.jdbc.JdbcQueries;
import org.spincast.plugins.jdbc.JdbcUtils;
import org.spincast.plugins.jdbc.SpincastDataSource;
import org.spincast.plugins.jdbc.SpincastDataSourceFactory;
import org.spincast.plugins.jdbc.statements.UpdateStatement;
import org.spincast.testing.core.utils.SpincastTestingUtils;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.opentable.db.postgres.embedded.EmbeddedPostgres;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Singleton
public class SpincastTestingPostgres implements Provider<SpincastDataSource> {

    private final SpincastTestingPostgresConfig spincastTestingPostgresConfig;
    private final JdbcUtils jdbcUtils;
    private final SpincastDataSourceFactory spincastDataSourceFactory;
    private EmbeddedPostgres pg = null;
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
            this.pg = EmbeddedPostgres.builder()
                                      .setPort(SpincastTestingUtils.findFreePort())
                                      .setDataDirectory(getSpincastTestingPostgresConfig().getDataTempDir())
                                      .setCleanDataDirectory(true)
                                      .start();

        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    @Override
    public SpincastDataSource get() {
        if (this.dataSource == null) {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(createConnectionString());
            config.setUsername("postgres");
            config.setPassword("postgres");
            config.setMaximumPoolSize(10);
            DataSource ds = new HikariDataSource(config);
            this.dataSource = getSpincastDataSourceFactory().create(ds);
        }
        return this.dataSource;
    }

    protected String createConnectionString() {
        StringBuilder b = new StringBuilder();
        b.append("jdbc:postgresql://localhost:").append(getPg().getPort()).append("/postgres");
        return b.toString();
    }

    /**
     * Stops Postgres.
     */
    public void stopPostgres() {
        if (this.pg != null) {
            try {
                this.pg.close();
            } catch (Exception ex) {
                System.err.println(ex);
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
                return null;
            }
        });
    }


}
