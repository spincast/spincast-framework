package org.spincast.plugins.flywayutils.tests.migrations8;

import java.sql.Connection;

import javax.sql.DataSource;

import org.spincast.plugins.flywayutils.SpincastFlywayMigrationBase;
import org.spincast.plugins.jdbc.JdbcUtils;
import org.spincast.plugins.jdbc.statements.UpdateStatement;

import com.google.inject.Inject;

/**
 * This class must be identical to the one in package
 * "org.spincast.plugins.flywayutils.tests.migrations8b".
 */
public class M_2018_08_13__toto extends SpincastFlywayMigrationBase {

    @Inject
    public M_2018_08_13__toto(DataSource dataSource, JdbcUtils jdbcUtils) {
        super(dataSource, jdbcUtils);
    }

    @Override
    protected void runMigration(Connection connection) {

        UpdateStatement stm = getJdbcUtils().statements().createUpdateStatement(connection);
        stm.sql("CREATE TABLE users (" +
                "   id SERIAL PRIMARY KEY, " +
                "   first_name VARCHAR(255) UNIQUE NOT NULL " +
                ") ");
        stm.update();
    }
}
