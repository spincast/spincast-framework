package org.spincast.plugins.flywayutils;

import java.util.Objects;

import org.flywaydb.core.api.MigrationType;
import org.flywaydb.core.api.MigrationVersion;
import org.flywaydb.core.api.resolver.MigrationExecutor;
import org.flywaydb.core.api.resolver.ResolvedMigration;
import org.flywaydb.core.internal.util.ClassUtils;

public class SpincastFlywayResolvedMigration implements ResolvedMigration {

    private MigrationVersion version;
    private String description;
    private String script;
    private String physicalLocation;
    private MigrationExecutor executor;
    private final SpincastFlywayMigration spincastFlywayMigration;

    public SpincastFlywayResolvedMigration(SpincastFlywayMigration spincastFlywayMigration) {
        Objects.requireNonNull(spincastFlywayMigration, "The spincastFlywayMigration can't be NULL");
        this.spincastFlywayMigration = spincastFlywayMigration;
    }

    protected SpincastFlywayMigration getSpincastFlywayMigration() {
        return this.spincastFlywayMigration;
    }

    @Override
    public MigrationVersion getVersion() {
        if (this.version == null) {

            String temp = getSpincastFlywayMigration().getClass().getSimpleName();
            if (!temp.startsWith("M_")) {
                throw new RuntimeException("Invalid migration class name. Must start with 'M_' followed by the version. " +
                                           "For example: 'M_2018_09_18_00' or 'M_1_44_0__someDescription'.");
            }
            temp = temp.substring("M_".length());

            //==========================================
            // Suffixe to remove?
            //==========================================
            int pos = temp.indexOf("__");
            if (pos > -1) {
                temp = temp.substring(0, pos);
            }
            this.version = MigrationVersion.fromVersion(temp);
        }
        return this.version;
    }

    @Override
    public String getDescription() {
        if (this.description == null) {
            this.description = "Migration " + getSpincastFlywayMigration().getClass().getSimpleName();
        }
        return this.description;
    }

    @Override
    public String getScript() {
        if (this.script == null) {
            this.script = getSpincastFlywayMigration().getClass().getName();
        }
        return this.script;
    }

    @Override
    public Integer getChecksum() {
        return null;
    }

    @Override
    public MigrationType getType() {
        return MigrationType.JDBC;
    }

    @Override
    public String getPhysicalLocation() {
        if (this.physicalLocation == null) {
            this.physicalLocation = ClassUtils.getLocationOnDisk(getSpincastFlywayMigration().getClass());
        }
        return this.physicalLocation;
    }

    @Override
    public MigrationExecutor getExecutor() {
        if (this.executor == null) {
            this.executor = new SpincastFlywayJdbcMigrationExecutor(getSpincastFlywayMigration());
        }
        return this.executor;
    }

}
