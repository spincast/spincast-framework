package org.spincast.plugins.flywayutils.tests.migrations4;

import java.sql.Connection;

import javax.sql.DataSource;

import org.spincast.plugins.flywayutils.SpincastFlywayMigrationBase;
import org.spincast.plugins.jdbc.JdbcUtils;

import com.google.inject.Inject;

public class M_abc extends SpincastFlywayMigrationBase {

    @Inject
    public M_abc(DataSource dataSource, JdbcUtils jdbcUtils) {
        super(dataSource, jdbcUtils);
    }

    @Override
    protected void runMigration(Connection connection) {
        // ok
    }
}
