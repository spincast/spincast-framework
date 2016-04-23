package org.spincast.tests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.spincast.core.exchange.IDefaultRequestContext;
import org.spincast.defaults.tests.DefaultIntegrationTestingBase;
import org.spincast.defaults.tests.DefaultTestingModule;

import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.ProvisionException;
import com.google.inject.Scopes;

public class RequestScopeOutOfScopeTest extends DefaultIntegrationTestingBase {

    public static class ServiceClass {

        //==========================================
        // We can't inject requestContext here because the
        // service is of scope SINGLETON and the requestContext is
        // request scoped.
        //==========================================
        @Inject
        public ServiceClass(IDefaultRequestContext requestContext) {
            fail();
        }
    }

    @Override
    public Module getTestingModule() {
        return new DefaultTestingModule() {

            @Override
            protected void configure() {
                super.configure();
                bind(ServiceClass.class).in(Scopes.SINGLETON);
            }
        };
    }

    @Inject
    protected ServiceClass service;

    protected Exception exception;

    @Override
    public void beforeClass() {

        try {
            super.beforeClass();
            fail();
        } catch(Exception ex) {
            this.exception = ex;
        }
    }

    @Test
    public void outOfScope() throws Exception {
        assertNotNull(this.exception);
        assertTrue(this.exception instanceof ProvisionException);
        assertNull(this.service);
    }

}
