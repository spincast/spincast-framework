package org.spincast.tests.validation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.spincast.core.exchange.DefaultRequestContext;
import org.spincast.core.json.JsonManager;
import org.spincast.core.request.Form;
import org.spincast.core.routing.Handler;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.core.validation.ValidationFactory;
import org.spincast.core.validation.ValidationMessage;
import org.spincast.core.validation.ValidationSet;
import org.spincast.defaults.testing.NoAppStartHttpServerTestingBase;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.shaded.org.apache.http.HttpStatus;

import com.google.inject.Inject;

public class FormValidationTest extends NoAppStartHttpServerTestingBase {

    @Inject
    protected JsonManager jsonManager;
    @Inject
    protected ValidationFactory validationFactory;

    protected JsonManager getJsonManager() {
        return this.jsonManager;
    }

    protected ValidationFactory getValidationFactory() {
        return this.validationFactory;
    }

    @Test
    public void simpleForm() throws Exception {

        getRouter().POST("/").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                Form form = context.request().getForm("myForm");
                context.response().addForm(form);

                assertEquals("Slomo", form.getString("name"));
                assertEquals("42", form.getString("age"));

                context.response().sendParseHtml("Hi {{myForm.name}}, {{myForm.age}}");
            }
        });

        HttpResponse response =
                POST("/").addEntityFormDataValue("myForm.name", "Slomo")
                         .addEntityFormDataValue("myForm.age", "42")
                         .send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.HTML.getMainVariationWithUtf8Charset(), response.getContentType());

        String content = response.getContentAsString();
        assertEquals("Hi Slomo, 42", content);
    }

    @Test
    public void validationHasNoError() throws Exception {

        getRouter().POST("/").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                Form form = context.request().getForm("myForm");
                context.response().addForm(form);

                context.response().sendParseHtml("{% if validation['myForm._'] | validationHasErrors() %}yes{% endif %}");
            }
        });

        HttpResponse response =
                POST("/").addEntityFormDataValue("myForm.name", "Slomo")
                         .addEntityFormDataValue("myForm.age", "42")
                         .send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.HTML.getMainVariationWithUtf8Charset(), response.getContentType());

        String content = response.getContentAsString();
        assertEquals("", content);
    }

    @Test
    public void validationHasError() throws Exception {

        getRouter().POST("/").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                Form form = context.request().getForm("myForm");
                context.response().addForm(form);

                form.addError("name", "name_invalid", "The name is invalid");

                context.response().sendParseHtml("{% if validation['myForm._'] | validationHasErrors() %}yes{% endif %}");
            }
        });

        HttpResponse response =
                POST("/").addEntityFormDataValue("myForm.name", "Slomo")
                         .addEntityFormDataValue("myForm.age", "42")
                         .send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.HTML.getMainVariationWithUtf8Charset(), response.getContentType());

        String content = response.getContentAsString();
        assertEquals("yes", content);
    }

    @Test
    public void validationMessages() throws Exception {

        getRouter().POST("/").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                Form form = context.request().getForm("myForm");
                context.response().addForm(form);

                form.addError("name", "name_invalid", "The name is invalid");

                context.response().sendParseHtml("{{validation['myForm.name'] | validationMessages()}}");
            }
        });

        HttpResponse response =
                POST("/").addEntityFormDataValue("myForm.name", "Slomo")
                         .addEntityFormDataValue("myForm.age", "42")
                         .send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.HTML.getMainVariationWithUtf8Charset(), response.getContentType());

        String content = response.getContentAsString();
        assertTrue(content.contains("msgError"));
        assertTrue(content.contains("validationMessages"));
        assertTrue(content.contains("The name is invalid"));
    }

    @Test
    public void customValidationElementName() throws Exception {

        getRouter().POST("/").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                Form form = context.request().getForm("myForm");
                context.response().addForm(form, "myFormValidation");

                form.addError("name", "name_invalid", "The name is invalid");

                context.response().sendParseHtml("{{myFormValidation['myForm.name'] | validationMessages()}}");
            }
        });

        HttpResponse response =
                POST("/").addEntityFormDataValue("myForm.name", "Slomo")
                         .addEntityFormDataValue("myForm.age", "42")
                         .send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.HTML.getMainVariationWithUtf8Charset(), response.getContentType());

        String content = response.getContentAsString();
        assertTrue(content.contains("msgError"));
        assertTrue(content.contains("validationMessages"));
        assertTrue(content.contains("The name is invalid"));
    }

    @Test
    public void twoFormsDefaultValidationElement() throws Exception {

        getRouter().POST("/").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                Form form = context.request().getForm("myForm");
                context.response().addForm(form);
                form.addError("name", "name_invalid", "The name is invalid");

                Form form2 = context.request().getForm("myForm2");
                context.response().addForm(form2);
                form2.addError("name2", "name2_invalid", "The name2 is invalid");

                context.response()
                       .sendParseHtml("{{validation['myForm.name'] | validationMessages()}} {{validation['myForm2.name2'] | validationMessages()}}");
            }
        });

        HttpResponse response =
                POST("/").addEntityFormDataValue("myForm.name", "Slomo")
                         .addEntityFormDataValue("myForm.age", "42")
                         .addEntityFormDataValue("myForm2.name", "Slomo2")
                         .addEntityFormDataValue("myForm2.age", "422")
                         .send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.HTML.getMainVariationWithUtf8Charset(), response.getContentType());

        String content = response.getContentAsString();
        assertTrue(content.contains("msgError"));
        assertTrue(content.contains("validationMessages"));
        assertTrue(content.contains("The name is invalid"));
        assertTrue(content.contains("The name2 is invalid"));
    }

    @Test
    public void twoFormsDifferentValidationElement() throws Exception {

        getRouter().POST("/").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                Form form = context.request().getForm("myForm");
                context.response().addForm(form, "myFormVal");
                form.addError("name", "name_invalid", "The name is invalid");

                Form form2 = context.request().getForm("myForm2");
                context.response().addForm(form2);
                form2.addError("name2", "name2_invalid", "The name2 is invalid");

                context.response()
                       .sendParseHtml("{{myFormVal['myForm.name'] | validationMessages()}} {{validation['myForm2.name2'] | validationMessages()}}");
            }
        });

        HttpResponse response =
                POST("/").addEntityFormDataValue("myForm.name", "Slomo")
                         .addEntityFormDataValue("myForm.age", "42")
                         .addEntityFormDataValue("myForm2.name", "Slomo2")
                         .addEntityFormDataValue("myForm2.age", "422")
                         .send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.HTML.getMainVariationWithUtf8Charset(), response.getContentType());

        String content = response.getContentAsString();
        assertTrue(content.contains("msgError"));
        assertTrue(content.contains("validationMessages"));
        assertTrue(content.contains("The name is invalid"));
        assertTrue(content.contains("The name2 is invalid"));
    }

    @Test
    public void twoFormsSameCustomValidationElement() throws Exception {

        getRouter().POST("/").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                Form form = context.request().getForm("myForm");
                context.response().addForm(form, "myFormVal");
                form.addError("name", "name_invalid", "The name is invalid");

                Form form2 = context.request().getForm("myForm2");
                context.response().addForm(form2, "myFormVal");
                form2.addError("name2", "name2_invalid", "The name2 is invalid");

                context.response()
                       .sendParseHtml("{{myFormVal['myForm.name'] | validationMessages()}} {{myFormVal['myForm2.name2'] | validationMessages()}}");
            }
        });

        HttpResponse response =
                POST("/").addEntityFormDataValue("myForm.name", "Slomo")
                         .addEntityFormDataValue("myForm.age", "42")
                         .addEntityFormDataValue("myForm2.name", "Slomo2")
                         .addEntityFormDataValue("myForm2.age", "422")
                         .send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.HTML.getMainVariationWithUtf8Charset(), response.getContentType());

        String content = response.getContentAsString();
        assertTrue(content.contains("msgError"));
        assertTrue(content.contains("validationMessages"));
        assertTrue(content.contains("The name is invalid"));
        assertTrue(content.contains("The name2 is invalid"));
    }

    @Test
    public void mergeValidationSets() throws Exception {

        ValidationSet set1 = getValidationFactory().createValidationSet();
        set1.addError("name", "name", "invalid name");
        assertEquals(1, set1.size());

        ValidationSet set2 = getValidationFactory().createValidationSet();
        set2.addError("name2", "name2", "invalid name2");
        assertEquals(1, set1.size());
        assertEquals(1, set2.size());

        set1.mergeValidationSet(set2);
        assertEquals(2, set1.size());
        assertEquals(1, set2.size());

        assertNotNull(set1.getMessages("name"));
        assertNotNull(set1.getMessages("name2"));
    }

    @Test
    public void mergeValidationSetMergedUsingEmptyValidationKeys() throws Exception {

        ValidationSet set1 = getValidationFactory().createValidationSet();
        set1.addError("name", "name", "invalid name");
        assertEquals(1, set1.size());

        ValidationSet set2 = getValidationFactory().createValidationSet();
        set2.addError("", "name2", "invalid name2");
        assertEquals(1, set1.size());
        assertEquals(1, set2.size());

        set1.mergeValidationSet("name", set2);
        assertEquals(1, set1.size());
        assertEquals(1, set2.size());

        List<ValidationMessage> messages = set1.getMessages("name");
        assertNotNull(messages);
        assertEquals(2, messages.size());


    }

}
