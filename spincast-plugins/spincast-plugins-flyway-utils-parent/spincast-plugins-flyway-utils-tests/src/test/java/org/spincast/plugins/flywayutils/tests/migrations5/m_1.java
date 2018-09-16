package org.spincast.plugins.flywayutils.tests.migrations5;

import java.sql.Connection;

import javax.sql.DataSource;

import org.spincast.plugins.flywayutils.SpincastFlywayMigrationBase;
import org.spincast.plugins.jdbc.JdbcUtils;

import com.google.inject.Inject;

public class m_1 extends SpincastFlywayMigrationBase {

    @Inject
    public m_1(DataSource dataSource, JdbcUtils jdbcUtils) {
        super(dataSource, jdbcUtils);
    }

    @Override
    protected void runMigration(Connection connection) {
        // ok
    }
}
