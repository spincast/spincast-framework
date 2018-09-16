package org.spincast.plugins.flywayutils.tests;

import static org.junit.Assert.fail;

import org.junit.Test;
import org.spincast.plugins.flywayutils.SpincastFlywayMigrationContext;
import org.spincast.plugins.flywayutils.tests.migrations1.M_2018_09_18_00;
import org.spincast.plugins.flywayutils.tests.migrations2._Nope;
import org.spincast.plugins.flywayutils.tests.migrations4.M_abc;
import org.spincast.plugins.flywayutils.tests.migrations5.m_1;
import org.spincast.plugins.flywayutils.tests.migrations6.M_1_22_3;
import org.spincast.plugins.flywayutils.tests.migrations7.M_22__toto;

public class ClassNamesTest extends TestBase {

    @Test
    public void validDate() throws Exception {

        SpincastFlywayMigrationContext migrationContext =
                getSpincastFlywayFactory().createMigrationContext(getTestDataSource(),
                                                                  M_2018_09_18_00.class.getPackage().getName());
        migrationContext.migrate();
    }

    @Test
    public void validVersion() throws Exception {

        SpincastFlywayMigrationContext migrationContext =
                getSpincastFlywayFactory().createMigrationContext(getTestDataSource(),
                                                                  M_1_22_3.class.getPackage().getName());
        migrationContext.migrate();
    }

    @Test
    public void validNumberAndDescription() throws Exception {

        SpincastFlywayMigrationContext migrationContext =
                getSpincastFlywayFactory().createMigrationContext(getTestDataSource(),
                                                                  M_22__toto.class.getPackage().getName());
        migrationContext.migrate();
    }

    @Test
    public void noMAndUnderscore() throws Exception {

        try {
            SpincastFlywayMigrationContext migrationContext =
                    getSpincastFlywayFactory().createMigrationContext(getTestDataSource(),
                                                                      _Nope.class.getPackage().getName());
            migrationContext.migrate();
            fail();
        } catch (Exception ex) {
        }
    }

    @Test
    public void invaldVersion() throws Exception {

        try {
            SpincastFlywayMigrationContext migrationContext =
                    getSpincastFlywayFactory().createMigrationContext(getTestDataSource(),
                                                                      M_abc.class.getPackage().getName());
            migrationContext.migrate();
            fail();
        } catch (Exception ex) {
        }
    }

    @Test
    public void mLower() throws Exception {

        try {
            SpincastFlywayMigrationContext migrationContext =
                    getSpincastFlywayFactory().createMigrationContext(getTestDataSource(),
                                                                      m_1.class.getPackage().getName());
            migrationContext.migrate();
            fail();
        } catch (Exception ex) {
        }
    }


}
