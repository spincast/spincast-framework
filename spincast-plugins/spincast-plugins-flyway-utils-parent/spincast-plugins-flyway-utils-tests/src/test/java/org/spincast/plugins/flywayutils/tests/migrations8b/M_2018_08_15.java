package org.spincast.plugins.flywayutils.tests.migrations8b;

import java.sql.Connection;

import javax.sql.DataSource;

import org.spincast.plugins.flywayutils.SpincastFlywayMigrationBase;
import org.spincast.plugins.jdbc.JdbcUtils;
import org.spincast.plugins.jdbc.statements.UpdateStatement;

import com.google.inject.Inject;

public class M_2018_08_15 extends SpincastFlywayMigrationBase {

    @Inject
    public M_2018_08_15(DataSource dataSource, JdbcUtils jdbcUtils) {
        super(dataSource, jdbcUtils);
    }

    @Override
    protected void runMigration(Connection connection) {
        UpdateStatement stm = getJdbcUtils().statements().createUpdateStatement(connection);
        stm.sql("UPDATE users " +
                "SET last_name = 'Alien' " +
                "WHERE first_name = 'Stromgol' ");
        stm.update();
    }
}
