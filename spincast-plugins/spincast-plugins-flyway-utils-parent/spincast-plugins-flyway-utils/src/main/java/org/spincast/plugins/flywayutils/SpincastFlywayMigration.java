package org.spincast.plugins.flywayutils;

import java.sql.Connection;

public interface SpincastFlywayMigration {

    public void migrate(Connection connection) throws Exception;

}
