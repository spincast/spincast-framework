package org.spincast.plugins.flywayutils.tests;

import static org.junit.Assert.fail;

import org.junit.Test;
import org.spincast.plugins.flywayutils.SpincastFlywayMigrationContext;
import org.spincast.plugins.flywayutils.tests.migrations3.M_1__InvalidQuery;

public class OtherTest extends TestBase {

    @Test
    public void testRollback() throws Exception {

        try {
            SpincastFlywayMigrationContext migrationContext =
                    getSpincastFlywayFactory().createMigrationContext(getTestDataSource(),
                                                                      M_1__InvalidQuery.class.getPackage().getName());
            migrationContext.migrate();
            fail();
        } catch (Exception ex) {
        }
    }

}
