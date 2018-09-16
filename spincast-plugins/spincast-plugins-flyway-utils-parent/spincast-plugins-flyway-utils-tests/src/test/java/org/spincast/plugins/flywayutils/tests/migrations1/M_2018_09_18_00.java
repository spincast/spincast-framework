package org.spincast.plugins.flywayutils.tests.migrations1;

import java.sql.Connection;

import javax.sql.DataSource;

import org.spincast.plugins.flywayutils.SpincastFlywayMigrationBase;
import org.spincast.plugins.jdbc.JdbcUtils;
import org.spincast.plugins.jdbc.SpincastResultSet;
import org.spincast.plugins.jdbc.statements.ResultSetHandler;
import org.spincast.plugins.jdbc.statements.SelectStatement;

import com.google.inject.Inject;

public class M_2018_09_18_00 extends SpincastFlywayMigrationBase {

    @Inject
    public M_2018_09_18_00(DataSource dataSource, JdbcUtils jdbcUtils) {
        super(dataSource, jdbcUtils);
    }

    @Override
    protected void runMigration(Connection connection) {
        SelectStatement stm = getJdbcUtils().statements().createSelectStatement(connection);
        stm.sql("SELECT 1 AS nbr ");

        stm.selectOne(new ResultSetHandler<Integer>() {

            @Override
            public Integer handle(SpincastResultSet rs) throws Exception {
                return rs.getIntegerOrNull("nbr");
            }
        });
    }

}
