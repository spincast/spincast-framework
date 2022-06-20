package org.spincast.plugins.flywayutils.tests.migrations8b;

import java.sql.Connection;

import javax.sql.DataSource;

import org.spincast.plugins.flywayutils.SpincastFlywayMigrationBase;
import org.spincast.plugins.jdbc.JdbcUtils;
import org.spincast.plugins.jdbc.statements.UpdateStatement;

import com.google.inject.Inject;

/**
 * This class must be identical to the one in package
 * "org.spincast.plugins.flywayutils.tests.migrations8".
 */
public class M_2018_08_14 extends SpincastFlywayMigrationBase {

    @Inject
    public M_2018_08_14(DataSource dataSource, JdbcUtils jdbcUtils) {
        super(dataSource, jdbcUtils);
    }

    @Override
    protected void runMigration(Connection connection) {
        UpdateStatement stm = getJdbcUtils().statements().createUpdateStatement(connection);
        stm.sql("ALTER TABLE users ADD last_name VARCHAR(255)");
        stm.update();
    }
}
