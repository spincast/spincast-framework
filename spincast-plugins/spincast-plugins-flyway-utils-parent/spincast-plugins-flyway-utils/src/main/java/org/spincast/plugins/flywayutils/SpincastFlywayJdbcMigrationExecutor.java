package org.spincast.plugins.flywayutils;

import java.sql.Connection;
import java.sql.SQLException;

import org.flywaydb.core.api.FlywayException;
import org.flywaydb.core.api.resolver.MigrationExecutor;


public class SpincastFlywayJdbcMigrationExecutor implements MigrationExecutor {

    private final SpincastFlywayMigration spincastFlywayMigration;

    public SpincastFlywayJdbcMigrationExecutor(SpincastFlywayMigration spincastFlywayMigration) {
        this.spincastFlywayMigration = spincastFlywayMigration;
    }

    protected SpincastFlywayMigration getSpincastFlywayMigration() {
        return this.spincastFlywayMigration;
    }

    @Override
    public void execute(Connection connection) throws SQLException {
        try {
            getSpincastFlywayMigration().migrate(connection);
        } catch (SQLException e) {
            throw e;
        } catch (Exception e) {
            throw new FlywayException("Migration failed !", e);
        }
    }

    @Override
    public boolean executeInTransaction() {
        return true;
    }
}
