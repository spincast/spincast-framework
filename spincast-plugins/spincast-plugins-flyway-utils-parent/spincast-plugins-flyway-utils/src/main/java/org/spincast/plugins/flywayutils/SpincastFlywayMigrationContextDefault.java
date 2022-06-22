package org.spincast.plugins.flywayutils;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;
import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.flywaydb.core.api.resolver.Context;
import org.flywaydb.core.api.resolver.ResolvedMigration;
import org.flywaydb.core.internal.resolver.ResolvedMigrationComparator;
import org.reflections.Reflections;
import org.spincast.core.config.SpincastConfig;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.plugins.jdbc.JdbcUtils;
import org.spincast.plugins.jdbc.SpincastDataSourceFactory;

import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

public class SpincastFlywayMigrationContextDefault implements SpincastFlywayMigrationContext {

    private final DataSource dataSource;
    private final String migrationsPackage;
    private final Provider<Injector> injectorProvider;
    private final JdbcUtils jdbcUtils;
    private final SpincastConfig spincastConfig;
    private final SpincastDataSourceFactory spincastDataSourceFactory;
    private final String schema;

    @AssistedInject
    public SpincastFlywayMigrationContextDefault(@Assisted DataSource dataSource,
                                                 @Assisted("migrationsPackage") String migrationsPackage,
                                                 Provider<Injector> injectorProvider,
                                                 JdbcUtils jdbcUtils,
                                                 SpincastConfig spincastConfig,
                                                 SpincastDataSourceFactory spincastDataSourceFactory) {
        this(dataSource, null, migrationsPackage, injectorProvider, jdbcUtils, spincastConfig, spincastDataSourceFactory);
    }

    @AssistedInject
    public SpincastFlywayMigrationContextDefault(@Assisted DataSource dataSource,
                                                 @Assisted("schema") @Nullable String schema,
                                                 @Assisted("migrationsPackage") String migrationsPackage,
                                                 Provider<Injector> injectorProvider,
                                                 JdbcUtils jdbcUtils,
                                                 SpincastConfig spincastConfig,
                                                 SpincastDataSourceFactory spincastDataSourceFactory) {
        this.dataSource = dataSource;
        this.migrationsPackage = migrationsPackage;
        this.injectorProvider = injectorProvider;
        this.jdbcUtils = jdbcUtils;
        this.spincastConfig = spincastConfig;
        this.spincastDataSourceFactory = spincastDataSourceFactory;
        this.schema = schema;
    }

    protected DataSource getDataSource() {
        return this.dataSource;
    }

    protected String getMigrationsPackage() {
        return this.migrationsPackage;
    }

    protected Injector getInjector() {
        return this.injectorProvider.get();
    }

    protected JdbcUtils getJdbcUtils() {
        return this.jdbcUtils;
    }

    protected String getSchema() {
        return this.schema;
    }

    protected SpincastConfig getSpincastConfig() {
        return this.spincastConfig;
    }

    protected SpincastDataSourceFactory getSpincastDataSourceFactory() {
        return this.spincastDataSourceFactory;
    }

    @Override
    public void migrate() {

        Flyway flyway = createFlyway();
        flyway.migrate();
    }

    protected boolean isValidateOnMigrate() {
        //==========================================
        // We default to false since that was the
        // default in Flyway before version 3,
        // Also, this feature doesn't seem to work well.
        // The migration files are always seen as
        // "modified" by Flyway for me.
        //==========================================
        return false;
    }

    protected Flyway createFlyway() {

        FluentConfiguration builder = Flyway.configure().dataSource(getDataSource())
                                            .resolvers(SpincastFlywayMigrationContextDefault.this)
                                            .skipDefaultResolvers(true)
                                            .validateOnMigrate(isValidateOnMigrate())
                                            .skipDefaultCallbacks(true);

        String schema = getSchema();
        if (schema != null) {
            builder = builder.schemas(schema);
        }

        Flyway flyway = new Flyway(builder);
        return flyway;
    }

    @Override
    public Collection<ResolvedMigration> resolveMigrations(Context context) {
        try {

            List<ResolvedMigration> migrations = new ArrayList<ResolvedMigration>();

            //==========================================
            // Finds all classes implemeting JdbcMigration
            // under the specified package.
            //==========================================
            Reflections reflections = new Reflections(getMigrationsPackage());

            Set<Class<? extends SpincastFlywayMigration>> classes = reflections.getSubTypesOf(SpincastFlywayMigration.class);
            for (Class<?> clazz : classes) {

                if (Modifier.isAbstract(clazz.getModifiers())) {
                    continue;
                }

                //==========================================
                // Because by default the package name is used as
                // a *prefix* ("a.b.c" will match "a.b.c123").
                //==========================================
                String classPackage = clazz.getPackage().getName();
                if (!classPackage.equals(getMigrationsPackage()) && !classPackage.startsWith(getMigrationsPackage() + ".")) {
                    continue;
                }

                //==========================================
                // We use the Guice injector to get the instance
                //==========================================
                SpincastFlywayMigration spincastFlywayMigration = (SpincastFlywayMigration)getInjector().getInstance(clazz);
                ResolvedMigration resolvedMigration = convertToResolvedMigration(spincastFlywayMigration);

                migrations.add(resolvedMigration);
            }

            Collections.sort(migrations, new ResolvedMigrationComparator());
            return migrations;

        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    protected ResolvedMigration convertToResolvedMigration(SpincastFlywayMigration spincastFlywayMigration) {
        return new SpincastFlywayResolvedMigration(spincastFlywayMigration);
    }

}
