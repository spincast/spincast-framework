package org.spincast.plugins.flywayutils.tests.migrations7;

import java.sql.Connection;

import javax.sql.DataSource;

import org.spincast.plugins.flywayutils.SpincastFlywayMigrationBase;
import org.spincast.plugins.jdbc.JdbcUtils;

import com.google.inject.Inject;

public class M_22__toto extends SpincastFlywayMigrationBase {

    @Inject
    public M_22__toto(DataSource dataSource, JdbcUtils jdbcUtils) {
        super(dataSource, jdbcUtils);
    }

    @Override
    protected void runMigration(Connection connection) {
        // ok
    }
}
