package org.spincast.plugins.pebble.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.spincast.core.config.SpincastConfig;
import org.spincast.core.exchange.DefaultRequestContext;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.json.JsonArray;
import org.spincast.core.json.JsonManager;
import org.spincast.core.json.JsonObject;
import org.spincast.core.request.Form;
import org.spincast.core.request.FormFactory;
import org.spincast.core.routing.Handler;
import org.spincast.core.templating.TemplatingEngine;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.plugins.pebble.SpincastPebbleTemplatingEngineConfig;
import org.spincast.plugins.pebble.SpincastPebbleTemplatingEngineConfigDefault;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.testing.defaults.NoAppStartHttpServerTestingBase;

import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.Scopes;

public class SpincastFunctionsAndFiltersTest extends NoAppStartHttpServerTestingBase {

    @Inject
    private FormFactory formFactory;

    protected FormFactory getFormFactory() {
        return this.formFactory;
    }

    @Override
    protected Module getExtraOverridingModule() {
        return new SpincastGuiceModuleBase() {

            @Override
            protected void configure() {
                bind(SpincastPebbleTemplatingEngineConfig.class).to(SpincastPebbleTemplatingEngineConfigDefaultTest.class)
                                                                .in(Scopes.SINGLETON);
            }
        };
    }

    /**
     * We enable strict variables
     */
    public static class SpincastPebbleTemplatingEngineConfigDefaultTest extends SpincastPebbleTemplatingEngineConfigDefault {

        @Inject
        public SpincastPebbleTemplatingEngineConfigDefaultTest(SpincastConfig spincastConfig) {
            super(spincastConfig);
        }

        @Override
        public boolean isStrictVariablesEnabled() {
            return true;
        }
    }

    @Inject
    protected TemplatingEngine templatingEngine;

    @Inject
    protected JsonManager jsonManager;

    protected TemplatingEngine getTemplatingEngine() {
        return this.templatingEngine;
    }

    protected JsonManager getJsonManager() {
        return this.jsonManager;
    }

    @Test
    public void invalidFunction() throws Exception {

        JsonObject model = getJsonManager().create();

        try {
            getTemplatingEngine().evaluate("{{nope('user.validation')}}",
                                           model);
            fail();
        } catch (Exception ex) {
        }
    }

    @Test
    public void invalidFilter() throws Exception {

        JsonObject model = getJsonManager().create();
        model.set("name", "Stromgol");
        try {
            getTemplatingEngine().evaluate("{{name | nope()}}",
                                           model);
            fail();
        } catch (Exception ex) {
        }
    }

    @Test
    public void valueNotSpecified() throws Exception {

        JsonObject model = getJsonManager().create();

        JsonObject user = getJsonManager().create();

        model.set("user", user);

        String html = getTemplatingEngine().evaluate("{{user.name | default('')}}", model);
        assertEquals("", html);
    }

    @Test
    public void valueNull() throws Exception {

        JsonObject model = getJsonManager().create();

        JsonObject user = getJsonManager().create();
        user.set("name", null);

        model.set("user", user);

        String html = getTemplatingEngine().evaluate("{{user.name | default('')}}", model);
        assertEquals("", html);
    }

    @Test
    public void valueEmpty() throws Exception {

        JsonObject model = getJsonManager().create();

        JsonObject user = getJsonManager().create();
        user.set("name", "");

        model.set("user", user);

        String html = getTemplatingEngine().evaluate("{{user.name | default('')}}", model);
        assertEquals("", html);
    }

    @Test
    public void value() throws Exception {

        JsonObject model = getJsonManager().create();

        JsonObject user = getJsonManager().create();
        user.set("name", "Stromgol");

        model.set("user", user);

        String html = getTemplatingEngine().evaluate("{{user.name | default('')}}", model);
        assertEquals("Stromgol", html);
    }

    @Test
    public void valueWithArray() throws Exception {

        JsonObject model = getJsonManager().create();

        JsonObject user = getJsonManager().create();
        model.set("user", user);

        user.set("name", "Stromgol");

        JsonArray books = getJsonManager().createArray();
        user.set("books", books);

        JsonObject book = getJsonManager().create();
        book.set("title", "book1");
        books.add(book);

        book = getJsonManager().create();
        book.set("title", "book2");
        books.add(book);

        String html = getTemplatingEngine().evaluate("{{user.books[0].title | default('')}}", model);
        assertEquals("book1", html);

        html = getTemplatingEngine().evaluate("{{user.books[1].title | default('')}}", model);
        assertEquals("book2", html);

        html = getTemplatingEngine().evaluate("{{user.books[2].title | default('')}}", model);
        assertEquals("", html);
    }

    @Test
    public void checkedFilterWithValue() throws Exception {

        JsonObject model = getJsonManager().create();

        JsonObject user = getJsonManager().create();
        user.set("color", "red");

        model.set("user", user);

        String html =
                getTemplatingEngine().evaluate("{{user.color | checked('blue')}}-{{user.color | checked('red')}}-{{user.color | checked('green')}}",
                                               model);
        assertEquals("-checked-", html);
    }

    @Test
    public void checkedFilterWithBoolean() throws Exception {

        JsonObject model = getJsonManager().create();

        JsonArray colors = getJsonManager().createArray();
        model.set("colors", colors);

        JsonObject color = getJsonManager().create();
        color.set("name", "blue");
        color.set("selected", false);
        colors.add(color);

        color = getJsonManager().create();
        color.set("name", "red");
        color.set("selected", true);
        colors.add(color);

        color = getJsonManager().create();
        color.set("name", "green");
        color.set("selected", false);
        colors.add(color);

        String html =
                getTemplatingEngine().evaluate("{{colors[0].selected | checked(true)}}-{{colors[1].selected | checked(true)}}-{{colors[2].selected | checked(true)}}",
                                               model);
        assertEquals("-checked-", html);
    }

    @Test
    public void checkedFilterWithListMatches() throws Exception {

        JsonObject model = getJsonManager().create();

        JsonObject user = getJsonManager().create();
        model.set("user", user);
        user.set("color", "red");

        JsonArray validColors = getJsonManager().createArray();
        model.set("validColors", validColors);
        validColors.add("blue");
        validColors.add("red");
        validColors.add("yellow");

        String html = getTemplatingEngine().evaluate("{{user.color | checked(validColors)}}",
                                                     model);
        assertEquals("checked", html);
    }

    @Test
    public void checkedFilterWithListMatchesInline() throws Exception {

        JsonObject model = getJsonManager().create();

        JsonObject user = getJsonManager().create();
        model.set("user", user);
        user.set("color", "red");

        String html = getTemplatingEngine().evaluate("{{user.color | checked(['blue', 'red', 'yellow'])}}",
                                                     model);
        assertEquals("checked", html);
    }

    @Test
    public void checkedFilterWithListNoMatch() throws Exception {

        JsonObject model = getJsonManager().create();

        JsonObject user = getJsonManager().create();
        model.set("user", user);
        user.set("color", "red");

        JsonArray validColors = getJsonManager().createArray();
        model.set("validColors", validColors);
        validColors.add("blue");
        validColors.add("green");
        validColors.add("yellow");

        String html = getTemplatingEngine().evaluate("{{user.color | checked(validColors)}}",
                                                     model);
        assertEquals("", html);
    }

    @Test
    public void checkedFilterWithListNoMatchInline() throws Exception {

        JsonObject model = getJsonManager().create();

        JsonObject user = getJsonManager().create();
        model.set("user", user);
        user.set("color", "red");

        String html = getTemplatingEngine().evaluate("{{user.color | checked(['blue', 'green', 'yellow'])}}",
                                                     model);
        assertEquals("", html);
    }

    @Test
    public void checkedFilterEquivalenceTrue() throws Exception {

        JsonObject model = getJsonManager().create();
        model.set("key", "true");

        String html =
                getTemplatingEngine().evaluate("{{key | checked('true')}}-{{key | checked(true)}}-{{key | checked('1')}}",
                                               model);
        assertEquals("checked-checked-", html);
    }

    @Test
    public void checkedFilterEquivalenceFalse() throws Exception {

        JsonObject model = getJsonManager().create();
        model.set("key", "false");

        String html =
                getTemplatingEngine().evaluate("{{key | checked('false')}}-{{key | checked(false)}}-{{key | checked('0')}}",
                                               model);
        assertEquals("checked-checked-", html);
    }

    @Test
    public void checkedFilterEquivalenceNumber() throws Exception {

        JsonObject model = getJsonManager().create();
        model.set("key", 123);

        String html =
                getTemplatingEngine().evaluate("{{key | checked('123')}}-{{key | checked(123.00)}}-{{key | checked('123.2')}}",
                                               model);
        assertEquals("checked-checked-", html);
    }

    @Test
    public void checkedFilterEquivalenceTrueInverse() throws Exception {

        JsonObject model = getJsonManager().create();
        model.set("key", true);

        String html =
                getTemplatingEngine().evaluate("{{key | checked('true')}}-{{key | checked(true)}}-{{key | checked('1')}}",
                                               model);
        assertEquals("checked-checked-", html);
    }

    @Test
    public void checkedFilterEquivalenceFalseInverse() throws Exception {

        JsonObject model = getJsonManager().create();
        model.set("key", false);

        String html =
                getTemplatingEngine().evaluate("{{key | checked('false')}}-{{key | checked(false)}}-{{key | checked('0')}}",
                                               model);
        assertEquals("checked-checked-", html);
    }

    @Test
    public void checkedFilterEquivalenceNumberInverse() throws Exception {

        JsonObject model = getJsonManager().create();
        model.set("key", "123.0");

        String html =
                getTemplatingEngine().evaluate("{{key | checked(123)}}-{{key | checked(123.00)}}-{{key | checked('123.2')}}",
                                               model);
        assertEquals("checked-checked-", html);
    }

    @Test
    public void selectedFilterWithValue() throws Exception {

        JsonObject model = getJsonManager().create();

        JsonObject user = getJsonManager().create();
        user.set("color", "red");

        model.set("user", user);

        String html =
                getTemplatingEngine().evaluate("{{user.color | selected('blue')}}-{{user.color | selected('red')}}-{{user.color | selected('green')}}",
                                               model);
        assertEquals("-selected-", html);
    }

    @Test
    public void selectedFilterWithBoolean() throws Exception {

        JsonObject model = getJsonManager().create();

        JsonArray colors = getJsonManager().createArray();
        model.set("colors", colors);

        JsonObject color = getJsonManager().create();
        color.set("name", "blue");
        color.set("selected", false);
        colors.add(color);

        color = getJsonManager().create();
        color.set("name", "red");
        color.set("selected", true);
        colors.add(color);

        color = getJsonManager().create();
        color.set("name", "green");
        color.set("selected", false);
        colors.add(color);

        String html =
                getTemplatingEngine().evaluate("{{colors[0].selected | selected(true)}}-{{colors[1].selected | selected(true)}}-{{colors[2].selected | selected(true)}}",
                                               model);
        assertEquals("-selected-", html);
    }

    @Test
    public void selectedFilterWithListMatches() throws Exception {

        JsonObject model = getJsonManager().create();

        JsonObject user = getJsonManager().create();
        model.set("user", user);
        user.set("color", "red");

        JsonArray validColors = getJsonManager().createArray();
        model.set("validColors", validColors);
        validColors.add("blue");
        validColors.add("red");
        validColors.add("yellow");

        String html = getTemplatingEngine().evaluate("{{user.color | selected(validColors)}}",
                                                     model);
        assertEquals("selected", html);
    }

    @Test
    public void selectedFilterWithListMatchesInline() throws Exception {

        JsonObject model = getJsonManager().create();

        JsonObject user = getJsonManager().create();
        model.set("user", user);
        user.set("color", "red");

        String html = getTemplatingEngine().evaluate("{{user.color | selected(['blue', 'red', 'yellow'])}}",
                                                     model);
        assertEquals("selected", html);
    }

    @Test
    public void selectedFilterWithListNoMatch() throws Exception {

        JsonObject model = getJsonManager().create();

        JsonObject user = getJsonManager().create();
        model.set("user", user);
        user.set("color", "red");

        JsonArray validColors = getJsonManager().createArray();
        model.set("validColors", validColors);
        validColors.add("blue");
        validColors.add("green");
        validColors.add("yellow");

        String html = getTemplatingEngine().evaluate("{{user.color | selected(validColors)}}",
                                                     model);
        assertEquals("", html);
    }

    @Test
    public void selectedFilterWithListNoMatchInline() throws Exception {

        JsonObject model = getJsonManager().create();

        JsonObject user = getJsonManager().create();
        model.set("user", user);
        user.set("color", "red");

        String html = getTemplatingEngine().evaluate("{{user.color | selected(['blue', 'green', 'yellow'])}}",
                                                     model);
        assertEquals("", html);
    }

    @Test
    public void selectedFilterEquivalenceTrue() throws Exception {

        JsonObject model = getJsonManager().create();
        model.set("key", "true");

        String html =
                getTemplatingEngine().evaluate("{{key | selected('true')}}-{{key | selected(true)}}-{{key | selected('1')}}",
                                               model);
        assertEquals("selected-selected-", html);
    }

    @Test
    public void selectedFilterEquivalenceFalse() throws Exception {

        JsonObject model = getJsonManager().create();
        model.set("key", "false");

        String html =
                getTemplatingEngine().evaluate("{{key | selected('false')}}-{{key | selected(false)}}-{{key | selected('0')}}",
                                               model);
        assertEquals("selected-selected-", html);
    }

    @Test
    public void selectedFilterEquivalenceNumber() throws Exception {

        JsonObject model = getJsonManager().create();
        model.set("key", 123);

        String html =
                getTemplatingEngine().evaluate("{{key | selected('123')}}-{{key | selected(123.00)}}-{{key | selected('123.2')}}",
                                               model);
        assertEquals("selected-selected-", html);
    }

    @Test
    public void selectedFilterEquivalenceTrueInverse() throws Exception {

        JsonObject model = getJsonManager().create();
        model.set("key", true);

        String html =
                getTemplatingEngine().evaluate("{{key | selected('true')}}-{{key | selected(true)}}-{{key | selected('1')}}",
                                               model);
        assertEquals("selected-selected-", html);
    }

    @Test
    public void selectedFilterEquivalenceFalseInverse() throws Exception {

        JsonObject model = getJsonManager().create();
        model.set("key", false);

        String html =
                getTemplatingEngine().evaluate("{{key | selected('false')}}-{{key | selected(false)}}-{{key | selected('0')}}",
                                               model);
        assertEquals("selected-selected-", html);
    }

    @Test
    public void selectedFilterEquivalenceNumberInverse() throws Exception {

        JsonObject model = getJsonManager().create();
        model.set("key", "123.0");

        String html =
                getTemplatingEngine().evaluate("{{key | selected(123)}}-{{key | selected(123.00)}}-{{key | selected('123.2')}}",
                                               model);
        assertEquals("selected-selected-", html);
    }

    @Test
    public void filterValidationMessagesValid() throws Exception {

        Form form = getFormFactory().createForm("myFormName", null);
        JsonObject model = getJsonManager().create();
        JsonObject validationElement = getJsonManager().create();
        model.set(getSpincastConfig().getValidationElementDefaultName(), validationElement);
        form.setValidationObject(validationElement);

        String html = getTemplatingEngine().evaluate("{{validation['myFormName.name'] | validationMessages()}}",
                                                     model);
        assertEquals("", html);

        html = getTemplatingEngine().evaluate("{{user.nope | validationMessages()}}",
                                              model);
        assertEquals("", html);
    }

    @Test
    public void filterValidationMessagesInvalid() throws Exception {

        Form form = getFormFactory().createForm("myFormName", null);
        JsonObject model = getJsonManager().create();
        JsonObject validationElement = getJsonManager().create();
        model.set(getSpincastConfig().getValidationElementDefaultName(), validationElement);
        form.setValidationObject(validationElement);

        form.addError("name", "name", "message");

        String html =
                getTemplatingEngine().evaluate("{{validation['myFormName.name'] | validationMessages()}}",
                                               model);
        assertTrue(html.contains("msgError"));
        assertTrue(html.contains("validationMessages"));

        html = getTemplatingEngine().evaluate("{{validation['myFormName.age'] | validationMessages()}}",
                                              model);
        assertEquals("", html);
    }

    @Test
    public void filterValidationGroupMessagesValid() throws Exception {

        Form form = getFormFactory().createForm("myFormName", null);
        JsonObject model = getJsonManager().create();
        JsonObject validationElement = getJsonManager().create();
        model.set(getSpincastConfig().getValidationElementDefaultName(), validationElement);
        form.setValidationObject(validationElement);

        String html = getTemplatingEngine().evaluate("{{validation['myFormName.myGroup'] | validationGroupMessages()}}",
                                                     model);
        assertEquals("", html);

        html = getTemplatingEngine().evaluate("{{validation['myFormName.age'] | validationGroupMessages()}}",
                                              model);
        assertEquals("", html);
    }

    @Test
    public void filterValidationGroupMessagesInvalid() throws Exception {

        Form form = getFormFactory().createForm("myFormName", null);
        JsonObject model = getJsonManager().create();
        JsonObject validationElement = getJsonManager().create();
        model.set(getSpincastConfig().getValidationElementDefaultName(), validationElement);
        form.setValidationObject(validationElement);

        form.addError("myGroup", "myGroup", "message");

        String html =
                getTemplatingEngine().evaluate("{{validation['myFormName.myGroup'] | validationGroupMessages()}}",
                                               model);
        assertTrue(html.contains("msgError"));
        assertTrue(html.contains("validationGroupMessages"));

        html = getTemplatingEngine().evaluate("{{validation['myFormName.age'] | validationGroupMessages()}}",
                                              model);
        assertEquals("", html);
    }

    @Test
    public void filterValidationClassValid() throws Exception {

        Form form = getFormFactory().createForm("myFormName", null);
        JsonObject model = getJsonManager().create();
        JsonObject validationElement = getJsonManager().create();
        model.set(getSpincastConfig().getValidationElementDefaultName(), validationElement);
        form.setValidationObject(validationElement);

        String html = getTemplatingEngine().evaluate("{{validation['myFormName.name'] | validationClass()}}",
                                                     model);
        assertEquals("has-no-message", html);

        html = getTemplatingEngine().evaluate("{{validation['myFormName.age'] | validationClass()}}",
                                              model);
        assertEquals("has-no-message", html);
    }

    @Test
    public void filterValidationClassInvalid() throws Exception {

        Form form = getFormFactory().createForm("myFormName", null);
        JsonObject model = getJsonManager().create();
        JsonObject validationElement = getJsonManager().create();
        model.set(getSpincastConfig().getValidationElementDefaultName(), validationElement);
        form.setValidationObject(validationElement);

        form.addError("name", "name", "message");
        form.addSuccess("age", "age", "message");
        form.addWarning("kiki", "kiki", "message");

        String html =
                getTemplatingEngine().evaluate("{{validation['myFormName.name'] | validationClass()}}",
                                               model);
        assertEquals("has-error", html);

        html = getTemplatingEngine().evaluate("{{validation['myFormName.age'] | validationClass()}}",
                                              model);
        assertEquals("has-success", html);

        html = getTemplatingEngine().evaluate("{{validation['myFormName.kiki'] | validationClass()}}",
                                              model);
        assertEquals("has-warning", html);
    }

    @Test
    public void filterFreshYes() throws Exception {

        JsonObject model = getJsonManager().create();
        JsonObject validationElement = getJsonManager().create();
        model.set(getSpincastConfig().getValidationElementDefaultName(), validationElement);

        String html = getTemplatingEngine().evaluate(" {% if validation['_'] | validationFresh() %}fresh{% endif %}",
                                                     model)
                                           .trim();
        assertEquals("fresh", html);
    }

    @Test
    public void filterFreshNot() throws Exception {

        Form form = getFormFactory().createForm("myFormName", null);
        JsonObject model = getJsonManager().create();
        JsonObject validationElement = getJsonManager().create();
        model.set(getSpincastConfig().getValidationElementDefaultName(), validationElement);
        form.setValidationObject(validationElement);

        String html = getTemplatingEngine().evaluate(" {% if validation['myFormName._'] | validationFresh() %}fresh{% endif %}",
                                                     model)
                                           .trim();
        assertEquals("", html);
    }

    @Test
    public void filterFreshNot2() throws Exception {

        Form form = getFormFactory().createForm("myFormName", null);
        JsonObject model = getJsonManager().create();
        JsonObject validationElement = getJsonManager().create();
        model.set(getSpincastConfig().getValidationElementDefaultName(), validationElement);
        form.setValidationObject(validationElement);

        form.addError("myGroup", "myGroup", "message");

        String html = getTemplatingEngine().evaluate(" {% if validation['myFormName._'] | validationFresh() %}fresh{% endif %}",
                                                     model)
                                           .trim();
        assertEquals("", html);
    }

    @Test
    public void filterSubmittedYes() throws Exception {

        Form form = getFormFactory().createForm("myFormName", null);
        JsonObject model = getJsonManager().create();
        JsonObject validationElement = getJsonManager().create();
        model.set(getSpincastConfig().getValidationElementDefaultName(), validationElement);
        form.setValidationObject(validationElement);

        String html =
                getTemplatingEngine().evaluate(" {% if validation['myFormName._'] | validationSubmitted() %}submitted{% endif %}",
                                               model)
                                     .trim();
        assertEquals("submitted", html);
    }

    @Test
    public void filterSubmittedNot() throws Exception {

        JsonObject model = getJsonManager().create();
        JsonObject validationElement = getJsonManager().create();
        model.set(getSpincastConfig().getValidationElementDefaultName(), validationElement);

        String html =
                getTemplatingEngine().evaluate(" {% if validation['myFormName._'] | validationSubmitted() %}fresh{% endif %}",
                                               model)
                                     .trim();
        assertEquals("", html);
    }

    @Test
    public void filterHasSuccessesFieldFalse() throws Exception {

        Form form = getFormFactory().createForm("myFormName", null);
        JsonObject model = getJsonManager().create();
        JsonObject validationElement = getJsonManager().create();
        model.set(getSpincastConfig().getValidationElementDefaultName(), validationElement);
        form.setValidationObject(validationElement);

        form.addError("name", "name", "message");
        form.addSuccess("age", "age", "message");

        String html =
                getTemplatingEngine().evaluate(" {% if validation['myFormName.name'] | validationHasSuccesses() %}yes{% endif %}",
                                               model)
                                     .trim();
        assertEquals("", html);
    }

    @Test
    public void filterHasSuccessesFieldTrue() throws Exception {

        Form form = getFormFactory().createForm("myFormName", null);
        JsonObject model = getJsonManager().create();
        JsonObject validationElement = getJsonManager().create();
        model.set(getSpincastConfig().getValidationElementDefaultName(), validationElement);
        form.setValidationObject(validationElement);

        form.addSuccess("name", "name", "message");
        form.addError("age", "age", "message");

        String html =
                getTemplatingEngine().evaluate(" {% if validation['myFormName.name'] | validationHasSuccesses() %}yes{% endif %}",
                                               model)
                                     .trim();
        assertEquals("yes", html);
    }

    @Test
    public void filterHasSuccessesFormFalse() throws Exception {

        Form form = getFormFactory().createForm("myFormName", null);
        JsonObject model = getJsonManager().create();
        JsonObject validationElement = getJsonManager().create();
        model.set(getSpincastConfig().getValidationElementDefaultName(), validationElement);
        form.setValidationObject(validationElement);

        form.addError("name", "name", "message");
        form.addError("age", "age", "message");

        String html =
                getTemplatingEngine().evaluate(" {% if validation['myFormName._'] | validationHasSuccesses() %}yes{% endif %}",
                                               model)
                                     .trim();
        assertEquals("", html);
    }

    @Test
    public void filterHasSuccessesFormTrue() throws Exception {

        Form form = getFormFactory().createForm("myFormName", null);
        JsonObject model = getJsonManager().create();
        JsonObject validationElement = getJsonManager().create();
        model.set(getSpincastConfig().getValidationElementDefaultName(), validationElement);
        form.setValidationObject(validationElement);

        form.addError("name", "name", "message");
        form.addSuccess("age", "age", "message");

        String html =
                getTemplatingEngine().evaluate(" {% if validation['myFormName._'] | validationHasSuccesses() %}yes{% endif %}",
                                               model)
                                     .trim();
        assertEquals("yes", html);
    }

    @Test
    public void filterHasErrorsFieldFalse() throws Exception {

        Form form = getFormFactory().createForm("myFormName", null);
        JsonObject model = getJsonManager().create();
        JsonObject validationElement = getJsonManager().create();
        model.set(getSpincastConfig().getValidationElementDefaultName(), validationElement);
        form.setValidationObject(validationElement);

        form.addSuccess("name", "name", "message");
        form.addWarning("age", "age", "message");

        String html =
                getTemplatingEngine().evaluate(" {% if validation['myFormName.age'] | validationHasErrors() %}yes{% endif %}",
                                               model)
                                     .trim();
        assertEquals("", html);
    }

    @Test
    public void filterHasErrorsFieldTrue() throws Exception {

        Form form = getFormFactory().createForm("myFormName", null);
        JsonObject model = getJsonManager().create();
        JsonObject validationElement = getJsonManager().create();
        model.set(getSpincastConfig().getValidationElementDefaultName(), validationElement);
        form.setValidationObject(validationElement);

        form.addSuccess("name", "name", "message");
        form.addError("age", "age", "message");

        String html =
                getTemplatingEngine().evaluate(" {% if validation['myFormName.age'] | validationHasErrors() %}yes{% endif %}",
                                               model)
                                     .trim();
        assertEquals("yes", html);
    }

    @Test
    public void filterHasErrorsFormFalse() throws Exception {

        Form form = getFormFactory().createForm("myFormName", null);
        JsonObject model = getJsonManager().create();
        JsonObject validationElement = getJsonManager().create();
        model.set(getSpincastConfig().getValidationElementDefaultName(), validationElement);
        form.setValidationObject(validationElement);

        form.addSuccess("name", "name", "message");
        form.addSuccess("age", "age", "message");

        String html = getTemplatingEngine().evaluate(" {% if validation['myFormName._'] | validationHasErrors() %}yes{% endif %}",
                                                     model)
                                           .trim();
        assertEquals("", html);
    }

    @Test
    public void filterHasErrorsFormTrue() throws Exception {

        Form form = getFormFactory().createForm("myFormName", null);
        JsonObject model = getJsonManager().create();
        JsonObject validationElement = getJsonManager().create();
        model.set(getSpincastConfig().getValidationElementDefaultName(), validationElement);
        form.setValidationObject(validationElement);

        form.addError("name", "name", "message");
        form.addSuccess("age", "age", "message");

        String html = getTemplatingEngine().evaluate(" {% if validation['myFormName._'] | validationHasErrors() %}yes{% endif %}",
                                                     model)
                                           .trim();
        assertEquals("yes", html);
    }

    @Test
    public void filterHasWarningsFieldFalse() throws Exception {

        Form form = getFormFactory().createForm("myFormName", null);
        JsonObject model = getJsonManager().create();
        JsonObject validationElement = getJsonManager().create();
        model.set(getSpincastConfig().getValidationElementDefaultName(), validationElement);
        form.setValidationObject(validationElement);

        form.addSuccess("age", "age1", "message");
        form.addError("age", "age2", "message");

        String html =
                getTemplatingEngine().evaluate(" {% if validation['myFormName.age'] | validationHasWarnings() %}yes{% endif %}",
                                               model)
                                     .trim();
        assertEquals("", html);
    }

    @Test
    public void filterHasWarningsFieldTrue() throws Exception {

        Form form = getFormFactory().createForm("myFormName", null);
        JsonObject model = getJsonManager().create();
        JsonObject validationElement = getJsonManager().create();
        model.set(getSpincastConfig().getValidationElementDefaultName(), validationElement);
        form.setValidationObject(validationElement);

        form.addWarning("age", "age1", "message");
        form.addError("age", "age2", "message");

        String html =
                getTemplatingEngine().evaluate(" {% if validation['myFormName.age'] | validationHasWarnings() %}yes{% endif %}",
                                               model)
                                     .trim();
        assertEquals("yes", html);
    }

    @Test
    public void filterHasWarningsFormFalse() throws Exception {

        Form form = getFormFactory().createForm("myFormName", null);
        JsonObject model = getJsonManager().create();
        JsonObject validationElement = getJsonManager().create();
        model.set(getSpincastConfig().getValidationElementDefaultName(), validationElement);
        form.setValidationObject(validationElement);

        form.addSuccess("age", "age1", "message");
        form.addError("age", "age2", "message");

        String html =
                getTemplatingEngine().evaluate(" {% if validation['myFormName._'] | validationHasWarnings() %}yes{% endif %}",
                                               model)
                                     .trim();
        assertEquals("", html);
    }


    @Test
    public void filterHasWarningsFormTrue() throws Exception {

        Form form = getFormFactory().createForm("myFormName", null);
        JsonObject model = getJsonManager().create();
        JsonObject validationElement = getJsonManager().create();
        model.set(getSpincastConfig().getValidationElementDefaultName(), validationElement);
        form.setValidationObject(validationElement);

        form.addSuccess("age", "age1", "message");
        form.addWarning("age", "age2", "message");

        String html =
                getTemplatingEngine().evaluate(" {% if validation['myFormName._'] | validationHasWarnings() %}yes{% endif %}",
                                               model)
                                     .trim();
        assertEquals("yes", html);
    }

    @Test
    public void filterIsValidFieldTrue() throws Exception {

        Form form = getFormFactory().createForm("myFormName", null);
        JsonObject model = getJsonManager().create();
        JsonObject validationElement = getJsonManager().create();
        model.set(getSpincastConfig().getValidationElementDefaultName(), validationElement);
        form.setValidationObject(validationElement);

        form.addSuccess("age", "age1", "message");
        form.addWarning("age", "age2", "message");

        String html = getTemplatingEngine().evaluate(" {% if validation['myFormName.age'] | validationIsValid() %}yes{% endif %}",
                                                     model)
                                           .trim();
        assertEquals("yes", html);
    }


    @Test
    public void filterIsValidFieldFalse() throws Exception {

        Form form = getFormFactory().createForm("myFormName", null);
        JsonObject model = getJsonManager().create();
        JsonObject validationElement = getJsonManager().create();
        model.set(getSpincastConfig().getValidationElementDefaultName(), validationElement);
        form.setValidationObject(validationElement);

        form.addSuccess("age", "age1", "message");
        form.addError("age", "age2", "message");

        String html = getTemplatingEngine().evaluate(" {% if validation['myFormName.age'] | validationIsValid() %}yes{% endif %}",
                                                     model)
                                           .trim();
        assertEquals("", html);
    }

    @Test
    public void filterIsValidFormTrue() throws Exception {

        Form form = getFormFactory().createForm("myFormName", null);
        JsonObject model = getJsonManager().create();
        JsonObject validationElement = getJsonManager().create();
        model.set(getSpincastConfig().getValidationElementDefaultName(), validationElement);
        form.setValidationObject(validationElement);

        form.addSuccess("age", "age1", "message");
        form.addWarning("age", "age2", "message");

        String html = getTemplatingEngine().evaluate(" {% if validation['myFormName._'] | validationIsValid() %}yes{% endif %}",
                                                     model)
                                           .trim();
        assertEquals("yes", html);
    }

    @Test
    public void filterIsValidFormFalse() throws Exception {

        Form form = getFormFactory().createForm("myFormName", null);
        JsonObject model = getJsonManager().create();
        JsonObject validationElement = getJsonManager().create();
        model.set(getSpincastConfig().getValidationElementDefaultName(), validationElement);
        form.setValidationObject(validationElement);

        form.addSuccess("age", "age1", "message");
        form.addError("name", "name", "message");

        String html = getTemplatingEngine().evaluate(" {% if validation['myFormName._'] | validationIsValid() %}yes{% endif %}",
                                                     model)
                                           .trim();
        assertEquals("", html);
    }

    @Test
    public void filterGet() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.set("name", "Stromgol");

        JsonObject model = getJsonManager().create();
        model.set("toto.titi", obj);

        String html = getTemplatingEngine().evaluate("{{('toto.ti' + 'ti.na' + 'me') | get()}}", model).trim();
        assertEquals("Stromgol", html);
    }

    @Test
    public void functionGet() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.set("name", "Stromgol");

        JsonObject model = getJsonManager().create();
        model.set("toto.titi", obj);

        String html = getTemplatingEngine().evaluate("{{get('toto.ti' + 'ti.na' + 'me')}}", model).trim();
        assertEquals("Stromgol", html);
    }

    @Test
    public void functionGetVariable() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.set("name", "Stromgol");

        JsonObject model = getJsonManager().create();
        model.set("toto.titi", obj);

        String html = getTemplatingEngine().evaluate("{% set myKey = 'toto.ti' + 'ti.na' + 'me' %} {{get(myKey)}}", model).trim();
        assertEquals("Stromgol", html);
    }

    @Test
    public void jsOneLineDefault() throws Exception {

        JsonObject model = getJsonManager().create();

        model.set("code",
                  "let a=1;\n" +
                          "let b=2;\r" +
                          "let c='3';\r\n" +
                          "let d=\"4\";\n");

        String html = getTemplatingEngine().evaluate("let js=\"{{jsOneLine(code)}}\"", model);
        assertEquals("let js=\"let a=1;\\nlet b=2;let c='3';\\nlet d=\\\"4\\\";\\n\"", html);
    }

    @Test
    public void jsOneLineForceDoubleQuotesEscape() throws Exception {

        JsonObject model = getJsonManager().create();

        model.set("code",
                  "let a=1;\n" +
                          "let b=2;\r" +
                          "let c='3';\r\n" +
                          "let d=\"4\";\n");

        String html = getTemplatingEngine().evaluate("let js=\"{{jsOneLine(code, false)}}\"", model);
        assertEquals("let js=\"let a=1;\\nlet b=2;let c='3';\\nlet d=\\\"4\\\";\\n\"", html);
    }

    @Test
    public void jsOneLineSingleQuotesEscape() throws Exception {

        JsonObject model = getJsonManager().create();

        model.set("code",
                  "let a=1;\n" +
                          "let b=2;\r" +
                          "let c='3';\r\n" +
                          "let d=\"4\";\n");

        String html = getTemplatingEngine().evaluate("let js='{{jsOneLine(code, true)}}';", model);
        assertEquals("let js='let a=1;\\nlet b=2;let c=\\'3\\';\\nlet d=\"4\";\\n';", html);
    }

    @Test
    public void twoFormsDefaultValidationElement() throws Exception {

        JsonObject model = getJsonManager().create();
        JsonObject validationElement = getJsonManager().create();
        model.set(getSpincastConfig().getValidationElementDefaultName(), validationElement);

        Form myForm = getFormFactory().createForm("myForm", null);
        model.set("myForm", myForm);
        myForm.setValidationObject(validationElement);
        myForm.set("name1", "toto");
        myForm.addError("name1", "name1", "message1");

        Form myForm2 = getFormFactory().createForm("myForm2", null);
        model.set("myForm2", myForm2);
        myForm2.addSuccess("name2", "name2", "message2");
        myForm2.set("name2", "titi");
        myForm2.setValidationObject(validationElement);

        String html = getTemplatingEngine().evaluate(" {% if validation['myForm._'] | validationHasSuccesses() %}yes{% endif %}",
                                                     model)
                                           .trim();
        assertEquals("", html);

        html = getTemplatingEngine().evaluate(" {% if validation['myForm._'] | validationHasErrors() %}yes{% endif %}",
                                              model)
                                    .trim();
        assertEquals("yes", html);

        html = getTemplatingEngine().evaluate("{{validation['myForm.name1'] | validationClass()}}",
                                              model);
        assertEquals("has-error", html);

        html = getTemplatingEngine().evaluate("{{myForm.name1}}", model);
        assertEquals("toto", html);

        //---------

        html = getTemplatingEngine().evaluate(" {% if validation['myForm2._'] | validationHasSuccesses() %}yes{% endif %}",
                                              model)
                                    .trim();
        assertEquals("yes", html);

        html = getTemplatingEngine().evaluate(" {% if validation['myForm2._'] | validationHasErrors() %}yes{% endif %}",
                                              model)
                                    .trim();
        assertEquals("", html);

        html = getTemplatingEngine().evaluate("{{validation['myForm2.name2'] | validationClass()}}",
                                              model);
        assertEquals("has-success", html);

        html = getTemplatingEngine().evaluate("{{myForm2.name2}}", model);
        assertEquals("titi", html);
    }

    @Test
    public void newline2br() throws Exception {

        JsonObject model = getJsonManager().create();
        model.set("val", "<em>aaa\nbbb\r\nccc</em>");

        // No newline2br
        String html = getTemplatingEngine().evaluate("{{ val }}';", model);
        assertEquals("&lt;em&gt;aaa\nbbb\r\nccc&lt;/em&gt;';", html);

        // Default newline2br
        html = getTemplatingEngine().evaluate("{{ val | newline2br }}';", model);
        assertEquals("&lt;em&gt;aaa<br />\nbbb<br />\nccc&lt;/em&gt;';", html);

        // newline2br + escape
        html = getTemplatingEngine().evaluate("{{ val | newline2br(true) }}';", model);
        assertEquals("&lt;em&gt;aaa<br />\nbbb<br />\nccc&lt;/em&gt;';", html);

        // newline2br + no escape
        html = getTemplatingEngine().evaluate("{{ val | newline2br(false) }}';", model);
        assertEquals("<em>aaa<br />\nbbb<br />\nccc</em>';", html);
    }

    @Test
    public void querystringNew() throws Exception {

        getRouter().GET("/one").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String content = "<a href=\"{{ querystring('?a=1') }}\">link</a>";
                String result = context.templating().evaluate(content);
                assertEquals(result, "<a href=\"?a=1\">link</a>");

                context.response().sendPlainText("ok");
            }
        });

        HttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void querystringAppend() throws Exception {

        getRouter().GET("/one").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String content = "<a href=\"{{ querystring('?a=1') }}\">link</a>";
                String result = context.templating().evaluate(content);
                assertEquals(result, "<a href=\"?titi=123&a=1\">link</a>");

                context.response().sendPlainText("ok");
            }
        });

        HttpResponse response = GET("/one?titi=123").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void querystringAppendWithParam() throws Exception {

        getRouter().GET("/one").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                JsonObject model = getJsonManager().create();
                model.set("name", "Stromgol");

                String content = "<a href=\"{{ querystring('?a=' + name) }}\">link</a>";
                String result = context.templating().evaluate(content, model);
                assertEquals(result, "<a href=\"?titi=123&a=Stromgol\">link</a>");

                context.response().sendPlainText("ok");
            }
        });

        HttpResponse response = GET("/one?titi=123").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void querystringReplace() throws Exception {

        getRouter().GET("/one").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String content = "<a href=\"{{ querystring('?titi=456') }}\">link</a>";
                String result = context.templating().evaluate(content);
                assertEquals(result, "<a href=\"?titi=456\">link</a>");

                context.response().sendPlainText("ok");
            }
        });

        HttpResponse response = GET("/one?titi=123").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void querystringNotInRequestContext() throws Exception {

        String content = "<a href=\"{{ querystring('?titi=456') }}\">link</a>";
        String result = getTemplatingEngine().evaluate(content);
        assertEquals(result, "<a href=\"?titi=456\">link</a>");
    }

    @Test
    public void querystringNoInterogationMark() throws Exception {

        getRouter().GET("/one").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                JsonObject model = getJsonManager().create();
                model.set("name", "Stromgol");

                String content = "<a href=\"{{ querystring('a=' + name) }}\">link</a>";
                String result = context.templating().evaluate(content, model);
                assertEquals(result, "<a href=\"?titi=123&a=Stromgol\">link</a>");

                context.response().sendPlainText("ok");
            }
        });

        HttpResponse response = GET("/one?titi=123").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void querystringAnd() throws Exception {

        getRouter().GET("/one").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                JsonObject model = getJsonManager().create();
                model.set("name", "Stromgol");

                String content = "<a href=\"{{ querystring('&a=' + name) }}\">link</a>";
                String result = context.templating().evaluate(content, model);
                assertEquals(result, "<a href=\"?titi=123&a=Stromgol\">link</a>");

                context.response().sendPlainText("ok");
            }
        });

        HttpResponse response = GET("/one?titi=123&").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void booleanWithout() throws Exception {

        JsonObject model = getJsonManager().create();
        model.set("val", "true");

        String content = "{% if val %}ok{% else %}not ok{% endif %}";

        try {
            getTemplatingEngine().evaluate(content, model);
            fail();
        } catch (Exception ex) {
        }
    }

    @Test
    public void booleanAsString() throws Exception {

        JsonObject model = getJsonManager().create();
        model.set("val", "true");

        String content = "{% if val | boolean %}ok{% else %}not ok{% endif %}";
        String result = getTemplatingEngine().evaluate(content, model);
        assertEquals(result, "ok");
    }

    @Test
    public void booleanAsStringFalse() throws Exception {

        JsonObject model = getJsonManager().create();
        model.set("val", "FALSE");

        String content = "{% if val | boolean %}ok{% else %}not ok{% endif %}";
        String result = getTemplatingEngine().evaluate(content, model);
        assertEquals(result, "not ok");
    }

    @Test
    public void booleanAsStringNot() throws Exception {

        JsonObject model = getJsonManager().create();
        model.set("val", "true");

        String content = "{% if not val | boolean %}ok{% else %}not ok{% endif %}";
        String result = getTemplatingEngine().evaluate(content, model);
        assertEquals(result, "not ok");
    }

    @Test
    public void booleanAsBoolean() throws Exception {

        JsonObject model = getJsonManager().create();
        model.set("val", true);

        String content = "{% if val | boolean %}ok{% else %}not ok{% endif %}";
        String result = getTemplatingEngine().evaluate(content, model);
        assertEquals(result, "ok");
    }

    @Test
    public void booleanAsBooleanNot() throws Exception {

        JsonObject model = getJsonManager().create();
        model.set("val", true);

        String content = "{% if not val | boolean %}ok{% else %}not ok{% endif %}";
        String result = getTemplatingEngine().evaluate(content, model);
        assertEquals(result, "not ok");
    }

    @Test
    public void routeExact1() throws Exception {

        getRouter().GET("/one").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String content = "{% if isRoute('/one') %}yes{% else %}no{% endif %}";
                String result = context.templating().evaluate(content);
                assertEquals(result, "yes");

                context.response().sendPlainText("ok");
            }
        });

        HttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void routeExact2() throws Exception {

        getRouter().GET("/one").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String content = "{% if isRoute('/one/') %}yes{% else %}no{% endif %}";
                String result = context.templating().evaluate(content);
                assertEquals(result, "yes");

                context.response().sendPlainText("ok");
            }
        });

        HttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void routeExact3() throws Exception {

        getRouter().GET("/one").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String content = "{% if isRoute('one/') %}yes{% else %}no{% endif %}";
                String result = context.templating().evaluate(content);
                assertEquals(result, "yes");

                context.response().sendPlainText("ok");
            }
        });

        HttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void routeExact4() throws Exception {

        getRouter().GET("/one").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String content = "{% if isRoute('one') %}yes{% else %}no{% endif %}";
                String result = context.templating().evaluate(content);
                assertEquals(result, "yes");

                context.response().sendPlainText("ok");
            }
        });

        HttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void routeExact5() throws Exception {

        getRouter().GET("/one/two").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String content = "{% if isRoute('/one/two') %}yes{% else %}no{% endif %}";
                String result = context.templating().evaluate(content);
                assertEquals(result, "yes");

                context.response().sendPlainText("ok");
            }
        });

        HttpResponse response = GET("/one/two").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void routeExact6() throws Exception {

        getRouter().GET("/one/two").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String content = "{% if isRoute('/one/two/') %}yes{% else %}no{% endif %}";
                String result = context.templating().evaluate(content);
                assertEquals(result, "yes");

                context.response().sendPlainText("ok");
            }
        });

        HttpResponse response = GET("/one/two").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void routeNotInRequestContext() throws Exception {

        String content = "{% if isRoute('/one') %}yes{% else %}no{% endif %}";
        String result = getTemplatingEngine().evaluate(content);
        assertEquals(result, "no");
    }

    @Test
    public void routeNoMatch1() throws Exception {

        getRouter().GET("/one").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String content = "{% if isRoute('/two') %}yes{% else %}no{% endif %}";
                String result = context.templating().evaluate(content);
                assertEquals(result, "no");

                context.response().sendPlainText("ok");
            }
        });

        HttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void routeNoMatch2() throws Exception {

        getRouter().GET("/one").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String content = "{% if isRoute('/') %}yes{% else %}no{% endif %}";
                String result = context.templating().evaluate(content);
                assertEquals(result, "no");

                context.response().sendPlainText("ok");
            }
        });

        HttpResponse response = GET("/one/").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void routeNoMatch3() throws Exception {

        getRouter().GET("/one").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String content = "{% if isRoute('') %}yes{% else %}no{% endif %}";
                String result = context.templating().evaluate(content);
                assertEquals(result, "no");

                context.response().sendPlainText("ok");
            }
        });

        HttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void routeNoMatch4() throws Exception {

        getRouter().GET("/one").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String content = "{% if isRoute() %}yes{% else %}no{% endif %}";
                String result = context.templating().evaluate(content);
                assertEquals(result, "no");

                context.response().sendPlainText("ok");
            }
        });

        HttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void routeNoMatch5() throws Exception {

        getRouter().GET("/one").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String content = "{% if isRoute('/one/two') %}yes{% else %}no{% endif %}";
                String result = context.templating().evaluate(content);
                assertEquals(result, "no");

                context.response().sendPlainText("ok");
            }
        });

        HttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void routeDynamicMatch() throws Exception {

        getRouter().GET("/${targetType:<A+>}").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String content = "{% if isRoute('/one') %}yes{% else %}no{% endif %}";
                String result = context.templating().evaluate(content);
                assertEquals(result, "yes");

                context.response().sendPlainText("ok");
            }
        });

        HttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void routeHome1() throws Exception {

        getRouter().GET("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String content = "{% if isRoute('/') %}yes{% else %}no{% endif %}";
                String result = context.templating().evaluate(content);
                assertEquals(result, "yes");

                context.response().sendPlainText("ok");
            }
        });

        HttpResponse response = GET("/").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void routeHome2() throws Exception {

        getRouter().GET("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String content = "{% if isRoute('') %}yes{% else %}no{% endif %}";
                String result = context.templating().evaluate(content);
                assertEquals(result, "yes");

                context.response().sendPlainText("ok");
            }
        });

        HttpResponse response = GET("/").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void routeHome3() throws Exception {

        getRouter().GET("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String content = "{% if isRoute() %}yes{% else %}no{% endif %}";
                String result = context.templating().evaluate(content);
                assertEquals(result, "yes");

                context.response().sendPlainText("ok");
            }
        });

        HttpResponse response = GET("/").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }


    @Test
    public void routeDynamicMatches() throws Exception {

        getRouter().GET("/*{anything}").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String content = "{% if isRoute('/one/two') %}yes{% else %}no{% endif %}";
                String result = context.templating().evaluate(content);
                assertEquals(result, "yes");

                context.response().sendPlainText("ok");
            }
        });

        HttpResponse response = GET("/one/two").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void routeDynamicMatches2() throws Exception {

        getRouter().GET("/*{anything}").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String content = "{% if isRoute('/one/two/') %}yes{% else %}no{% endif %}";
                String result = context.templating().evaluate(content);
                assertEquals(result, "yes");

                context.response().sendPlainText("ok");
            }
        });

        HttpResponse response = GET("/one/two").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void routeDynamicMatches3() throws Exception {

        getRouter().GET("/*{anything}").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String content = "{% if isRoute('one/two') %}yes{% else %}no{% endif %}";
                String result = context.templating().evaluate(content);
                assertEquals(result, "yes");

                context.response().sendPlainText("ok");
            }
        });

        HttpResponse response = GET("/one/two/").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void routeRegEx1() throws Exception {

        getRouter().GET("/one").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String content = "{% if isRoute('.*', true) %}yes{% else %}no{% endif %}";
                String result = context.templating().evaluate(content);
                assertEquals(result, "yes");

                context.response().sendPlainText("ok");
            }
        });

        HttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void routeRegEx2() throws Exception {

        getRouter().GET("/one").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String content = "{% if isRoute('/.{3}', true) %}yes{% else %}no{% endif %}";
                String result = context.templating().evaluate(content);
                assertEquals(result, "yes");

                context.response().sendPlainText("ok");
            }
        });

        HttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void routeRegEx3() throws Exception {

        getRouter().GET("/one/two").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String content = "{% if isRoute('/one/.*', true) %}yes{% else %}no{% endif %}";
                String result = context.templating().evaluate(content);
                assertEquals(result, "yes");

                context.response().sendPlainText("ok");
            }
        });

        HttpResponse response = GET("/one/two").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void routeRegEx4() throws Exception {

        getRouter().GET("/one").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String content = "{% if isRoute('/(one|two)', true) %}yes{% else %}no{% endif %}";
                String result = context.templating().evaluate(content);
                assertEquals(result, "yes");

                context.response().sendPlainText("ok");
            }
        });

        HttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void routeRegEx4b() throws Exception {

        getRouter().GET("/two").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String content = "{% if isRoute('/(one|two)', true) %}yes{% else %}no{% endif %}";
                String result = context.templating().evaluate(content);
                assertEquals(result, "yes");

                context.response().sendPlainText("ok");
            }
        });

        HttpResponse response = GET("/two").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void routeRegExNoMatchTrailingSlash() throws Exception {

        getRouter().GET("/one").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String content = "{% if isRoute('/(one|two)/', true) %}yes{% else %}no{% endif %}";
                String result = context.templating().evaluate(content);
                assertEquals(result, "no");

                context.response().sendPlainText("ok");
            }
        });

        HttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void routeRegExMatch() throws Exception {

        getRouter().GET("/one").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String content = "{% if isRoute('/(one|two)(/?$|/.*)', true) %}yes{% else %}no{% endif %}";
                String result = context.templating().evaluate(content);
                assertEquals(result, "yes");

                context.response().sendPlainText("ok");
            }
        });

        HttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void routeRegExMatch2() throws Exception {

        getRouter().GET("/two").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String content = "{% if isRoute('/(one|two)(/?$|/.*)', true) %}yes{% else %}no{% endif %}";
                String result = context.templating().evaluate(content);
                assertEquals(result, "yes");

                context.response().sendPlainText("ok");
            }
        });

        HttpResponse response = GET("/two").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void routeRegExMatch3() throws Exception {

        getRouter().GET("/one/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String content = "{% if isRoute('/(one|two)(/?$|/.*)', true) %}yes{% else %}no{% endif %}";
                String result = context.templating().evaluate(content);
                assertEquals(result, "yes");

                context.response().sendPlainText("ok");
            }
        });

        HttpResponse response = GET("/one/").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void routeRegExMatch4() throws Exception {

        getRouter().GET("/one/two").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String content = "{% if isRoute('/(one|two)(/?$|/.*)', true) %}yes{% else %}no{% endif %}";
                String result = context.templating().evaluate(content);
                assertEquals(result, "yes");

                context.response().sendPlainText("ok");
            }
        });

        HttpResponse response = GET("/one/two").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void routeRegExMatchNot() throws Exception {

        getRouter().GET("oneeee").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String content = "{% if isRoute('/(one|two)(/?$|/.*)', true) %}yes{% else %}no{% endif %}";
                String result = context.templating().evaluate(content);
                assertEquals(result, "no");

                context.response().sendPlainText("ok");
            }
        });

        HttpResponse response = GET("/oneeee").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void routeRegExSlashPrefixMissing() throws Exception {

        getRouter().GET("/one/two").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String content = "{% if isRoute('one/.*', true) %}yes{% else %}no{% endif %}";
                String result = context.templating().evaluate(content);
                assertEquals(result, "no");

                context.response().sendPlainText("ok");
            }
        });

        HttpResponse response = GET("/one/two").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void routeExactWithSubPaths() throws Exception {

        getRouter().GET("/one/two").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String content = "{% if isRoute('/one', false, true) %}yes{% else %}no{% endif %}";
                String result = context.templating().evaluate(content);
                assertEquals(result, "yes");

                context.response().sendPlainText("ok");
            }
        });

        HttpResponse response = GET("/one/two").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void routeExactWithSubPathsNoMatch() throws Exception {

        getRouter().GET("/oneee").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String content = "{% if isRoute('/one', false, true) %}yes{% else %}no{% endif %}";
                String result = context.templating().evaluate(content);
                assertEquals(result, "no");

                context.response().sendPlainText("ok");
            }
        });

        HttpResponse response = GET("/oneee").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void routeRegExWithSubPaths() throws Exception {

        getRouter().GET("/one").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String content = "{% if isRoute('/(one|two)', true, true) %}yes{% else %}no{% endif %}";
                String result = context.templating().evaluate(content);
                assertEquals(result, "yes");

                context.response().sendPlainText("ok");
            }
        });

        HttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void routeRegExWithSubPaths2() throws Exception {

        getRouter().GET("/two").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String content = "{% if isRoute('/(one|two)', true, true) %}yes{% else %}no{% endif %}";
                String result = context.templating().evaluate(content);
                assertEquals(result, "yes");

                context.response().sendPlainText("ok");
            }
        });

        HttpResponse response = GET("/two").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }


    @Test
    public void routeRegExWithSubPaths3() throws Exception {

        getRouter().GET("/one/two").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String content = "{% if isRoute('/(one|two)', true, true) %}yes{% else %}no{% endif %}";
                String result = context.templating().evaluate(content);
                assertEquals(result, "yes");

                context.response().sendPlainText("ok");
            }
        });

        HttpResponse response = GET("/one/two").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void routeRegExWithSubPaths4() throws Exception {

        getRouter().GET("/two/three/four").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String content = "{% if isRoute('/(one|two)', true, true) %}yes{% else %}no{% endif %}";
                String result = context.templating().evaluate(content);
                assertEquals(result, "yes");

                context.response().sendPlainText("ok");
            }
        });

        HttpResponse response = GET("/two/three/four").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void routeRegExWithSubPathsNoMatch() throws Exception {

        getRouter().GET("/oneee").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String content = "{% if isRoute('/(one|two)', true, true) %}yes{% else %}no{% endif %}";
                String result = context.templating().evaluate(content);
                assertEquals(result, "no");

                context.response().sendPlainText("ok");
            }
        });

        HttpResponse response = GET("/oneee").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void routeRegExWithSubPathsButEndsWithDollarSign() throws Exception {

        getRouter().GET("/one/two/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String content = "{% if isRoute('/one$', true, true) %}yes{% else %}no{% endif %}";
                String result = context.templating().evaluate(content);
                assertEquals(result, "no");

                context.response().sendPlainText("ok");
            }
        });

        HttpResponse response = GET("/one/two/").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void routeIdMatch() throws Exception {

        getRouter().GET("/one").id("myRouteId").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String content = "{% if isRouteId('myRouteId') %}yes{% else %}no{% endif %}";
                String result = context.templating().evaluate(content);
                assertEquals(result, "yes");

                context.response().sendPlainText("ok");
            }
        });

        HttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void routeIdNoMatch() throws Exception {

        getRouter().GET("/one").id("myRouteId").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String content = "{% if isRouteId('nope') %}yes{% else %}no{% endif %}";
                String result = context.templating().evaluate(content);
                assertEquals(result, "no");

                context.response().sendPlainText("ok");
            }
        });

        HttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

}




