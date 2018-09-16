package org.spincast.plugins.flywayutils;

import org.flywaydb.core.api.resolver.MigrationResolver;

public interface SpincastFlywayMigrationContext extends MigrationResolver {

    public void migrate();
}
