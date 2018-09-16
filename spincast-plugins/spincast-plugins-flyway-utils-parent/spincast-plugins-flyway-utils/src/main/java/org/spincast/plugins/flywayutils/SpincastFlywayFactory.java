package org.spincast.plugins.flywayutils;

import javax.sql.DataSource;

import com.google.inject.assistedinject.Assisted;

/**
 * Entry point for running a Spincast Flyway migration.
 */
public interface SpincastFlywayFactory {

    /**
     * Creates a context for the migrations of a 
     * specific {@link DataSource}.
     * You have to specify the package where the migration
     * classes are located for that DataSource. You can use one 
     * of the class to get this package name in a safe way:
     * <pre>
     * createMigrationContext(maintaSource, M_2018_09_18_00.class.getPackage().getName());
     * </pre>
     */
    public SpincastFlywayMigrationContext createMigrationContext(DataSource dataSource,
                                                                 @Assisted("migrationsPackage") String migrationsPackage);

    /**
     * Creates a context for the migrations of a 
     * specific {@link DataSource} and schema.
     * You have to specify the package where the migration
     * classes are located for that DataSource. You can use one 
     * of the class to get this package name in a safe way:
     * <pre>
     * createMigrationContext(maintaSource, "public", M_2018_09_18_00.class.getPackage().getName());
     * </pre>
     */
    public SpincastFlywayMigrationContext createMigrationContext(DataSource dataSource,
                                                                 @Assisted("schema") String schema,
                                                                 @Assisted("migrationsPackage") String migrationsPackage);

}
