package org.spincast.plugins.formsprotection.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.spincast.core.exchange.DefaultRequestContext;
import org.spincast.core.routing.Handler;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.shaded.org.apache.http.HttpStatus;

public class FormsDoubleSubmitProtectionTest extends FormsProtectionTestBase {

    @Override
    public void beforeClass() {
        super.beforeClass();
    }

    @Override
    protected boolean isAddDoubleSubmitProtectionFilter() {
        return true;
    }

    @Test
    public void get() throws Exception {

        getRouter().GET("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("ok");
            }
        });

        HttpResponse response = GET("/").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void doubleSubmit() throws Exception {

        String protectionId = getSpincastFormsDoubleSubmitProtectionFilter().createNewFormDoubleSubmitProtectionId();

        getRouter().POST("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String val1 = context.request().getFormBodyAsJsonObject().getString("k1");
                assertEquals("v1", val1);

                context.response().sendPlainText("ok");
            }
        });

        HttpResponse response = POST("/").addFormBodyFieldValue("k1", "v1")
                                         .addFormBodyFieldValue("k2", "v2")
                                         .addFormBodyFieldValue(getSpincastFormsProtectionConfig().getFormDoubleSubmitProtectionIdFieldName(),
                                                                protectionId)
                                         .send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());

        response = POST("/").addFormBodyFieldValue("k1", "v1")
                            .addFormBodyFieldValue("k2", "v2")
                            .addFormBodyFieldValue(getSpincastFormsProtectionConfig().getFormDoubleSubmitProtectionIdFieldName(),
                                                   protectionId)
                            .send();
        assertEquals(HttpStatus.SC_BAD_REQUEST, response.getStatus());

        //==========================================
        // Disable protection!
        //==========================================
        response = POST("/").addFormBodyFieldValue("k1", "v1")
                            .addFormBodyFieldValue("k2", "v2")
                            .addFormBodyFieldValue(getSpincastFormsProtectionConfig().getFormDoubleSubmitProtectionIdFieldName(),
                                                   protectionId)
                            .addFormBodyFieldValue(getSpincastFormsProtectionConfig().getFormDoubleSubmitDisableProtectionIdFieldName(),
                                                   "1")
                            .send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());

        response = POST("/").addFormBodyFieldValue("k1", "v1")
                            .addFormBodyFieldValue("k2", "v2")
                            .addFormBodyFieldValue(getSpincastFormsProtectionConfig().getFormDoubleSubmitProtectionIdFieldName(),
                                                   protectionId)
                            .send();
        assertEquals(HttpStatus.SC_BAD_REQUEST, response.getStatus());
    }

    @Test
    public void deleteOldFormsProtectionIdsCron() throws Exception {
        Thread.sleep(1000);
        assertEquals(1, deleteOldFormsProtectionIdsCalled[0]);
    }

}
