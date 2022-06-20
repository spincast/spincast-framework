package org.spincast.plugins.flywayutils;

import java.sql.SQLException;

import org.flywaydb.core.api.FlywayException;
import org.flywaydb.core.api.executor.Context;
import org.flywaydb.core.api.executor.MigrationExecutor;

public class SpincastFlywayJdbcMigrationExecutor implements MigrationExecutor {

    private final SpincastFlywayMigration spincastFlywayMigration;

    public SpincastFlywayJdbcMigrationExecutor(SpincastFlywayMigration spincastFlywayMigration) {
        this.spincastFlywayMigration = spincastFlywayMigration;
    }

    protected SpincastFlywayMigration getSpincastFlywayMigration() {
        return spincastFlywayMigration;
    }

    @Override
    public void execute(Context context) throws SQLException {
        try {
            getSpincastFlywayMigration().migrate(context.getConnection());
        } catch (SQLException e) {
            throw e;
        } catch (Exception e) {
            throw new FlywayException("Migration failed !", e);
        }
    }

    @Override
    public boolean canExecuteInTransaction() {
        return true;
    }

    @Override
    public boolean shouldExecute() {
        return true;
    }
}
