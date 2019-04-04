package org.spincast.plugins.flywayutils;

import java.sql.Connection;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.plugins.jdbc.ConnectionScope;
import org.spincast.plugins.jdbc.JdbcScope;
import org.spincast.plugins.jdbc.JdbcUtils;

import com.google.inject.Inject;

public abstract class SpincastFlywayMigrationBase implements SpincastFlywayMigration {

    protected static final Logger logger = LoggerFactory.getLogger(SpincastFlywayMigrationBase.class);

    private final DataSource dataSource;
    private final JdbcUtils jdbcUtils;

    @Inject
    public SpincastFlywayMigrationBase(DataSource dataSource,
                                       JdbcUtils jdbcUtils) {
        this.dataSource = dataSource;
        this.jdbcUtils = jdbcUtils;
    }

    protected DataSource getDataSource() {
        return this.dataSource;
    }

    protected JdbcUtils getJdbcUtils() {
        return this.jdbcUtils;
    }

    /**
     * Override {{@link #runMigration(Connection)}} instead of this
     * method.
     */
    @Override
    public final void migrate(Connection connection) throws Exception {

        //==========================================
        // We run all requests of thi migration file
        // using the connection provided by Flyway!
        //
        // We do this migration file by migration file
        // because Flyway may create a new connection.
        // Also, some database don't support transactions
        // for DDL requests, but at least migration files
        // without DDK would be performed inside transactions.
        //==========================================
        getJdbcUtils().scopes().specificConnection(connection, getDataSource(), new ConnectionScope<Void>() {

            @Override
            public Void run(Connection connection) throws Exception {
                runMigration(connection);

                SpincastFlywayMigrationBase.logger.info("Migration \"" +
                                                             SpincastFlywayMigrationBase.this.getClass().getSimpleName() +
                                                             "\" applied.");
                return null;
            }
        });
    }

    /**
     * Runs the migrations inside a scope where the
     * proper connection will used, even by indirect
     * components (ex: a service is called that uses
     * a repository which gets a connection using 
     * {@link JdbcScope#transactional(org.spincast.plugins.jdbc.TransactionalScope)} transactional()).
     */
    protected abstract void runMigration(Connection connection);


}
