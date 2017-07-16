package org.spincast.plugins.pebble.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.spincast.core.config.SpincastConfig;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.json.JsonArray;
import org.spincast.core.json.JsonManager;
import org.spincast.core.json.JsonObject;
import org.spincast.core.templating.TemplatingEngine;
import org.spincast.core.validation.JsonObjectValidationSet;
import org.spincast.defaults.testing.NoAppStartHttpServerTestingBase;
import org.spincast.plugins.pebble.SpincastPebbleTemplatingEngineConfig;
import org.spincast.plugins.pebble.SpincastPebbleTemplatingEngineConfigDefault;

import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.Scopes;

public class SpincastFunctionsAndFiltersTest extends NoAppStartHttpServerTestingBase {


    @Override
    protected Module getExtraOverridingModule2() {
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
        model.put("name", "Stromgol");
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

        model.put("user", user);

        String html = getTemplatingEngine().evaluate("{{user.name | default('')}}", model);
        assertEquals("", html);
    }

    @Test
    public void valueNull() throws Exception {

        JsonObject model = getJsonManager().create();

        JsonObject user = getJsonManager().create();
        user.put("name", null);

        model.put("user", user);

        String html = getTemplatingEngine().evaluate("{{user.name | default('')}}", model);
        assertEquals("", html);
    }

    @Test
    public void valueEmpty() throws Exception {

        JsonObject model = getJsonManager().create();

        JsonObject user = getJsonManager().create();
        user.put("name", "");

        model.put("user", user);

        String html = getTemplatingEngine().evaluate("{{user.name | default('')}}", model);
        assertEquals("", html);
    }

    @Test
    public void value() throws Exception {

        JsonObject model = getJsonManager().create();

        JsonObject user = getJsonManager().create();
        user.put("name", "Stromgol");

        model.put("user", user);

        String html = getTemplatingEngine().evaluate("{{user.name | default('')}}", model);
        assertEquals("Stromgol", html);
    }

    @Test
    public void valueWithArray() throws Exception {

        JsonObject model = getJsonManager().create();

        JsonObject user = getJsonManager().create();
        model.put("user", user);

        user.put("name", "Stromgol");

        JsonArray books = getJsonManager().createArray();
        user.put("books", books);

        JsonObject book = getJsonManager().create();
        book.put("title", "book1");
        books.add(book);

        book = getJsonManager().create();
        book.put("title", "book2");
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
        user.put("color", "red");

        model.put("user", user);

        String html =
                getTemplatingEngine().evaluate("{{user.color | checked('blue')}}-{{user.color | checked('red')}}-{{user.color | checked('green')}}",
                                               model);
        assertEquals("-checked-", html);
    }

    @Test
    public void checkedFilterWithBoolean() throws Exception {

        JsonObject model = getJsonManager().create();

        JsonArray colors = getJsonManager().createArray();
        model.put("colors", colors);

        JsonObject color = getJsonManager().create();
        color.put("name", "blue");
        color.put("selected", false);
        colors.add(color);

        color = getJsonManager().create();
        color.put("name", "red");
        color.put("selected", true);
        colors.add(color);

        color = getJsonManager().create();
        color.put("name", "green");
        color.put("selected", false);
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
        model.put("user", user);
        user.put("color", "red");

        JsonArray validColors = getJsonManager().createArray();
        model.put("validColors", validColors);
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
        model.put("user", user);
        user.put("color", "red");

        String html = getTemplatingEngine().evaluate("{{user.color | checked(['blue', 'red', 'yellow'])}}",
                                                     model);
        assertEquals("checked", html);
    }

    @Test
    public void checkedFilterWithListNoMatch() throws Exception {

        JsonObject model = getJsonManager().create();

        JsonObject user = getJsonManager().create();
        model.put("user", user);
        user.put("color", "red");

        JsonArray validColors = getJsonManager().createArray();
        model.put("validColors", validColors);
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
        model.put("user", user);
        user.put("color", "red");

        String html = getTemplatingEngine().evaluate("{{user.color | checked(['blue', 'green', 'yellow'])}}",
                                                     model);
        assertEquals("", html);
    }

    @Test
    public void checkedFilterEquivalenceTrue() throws Exception {

        JsonObject model = getJsonManager().create();
        model.put("key", "true");

        String html =
                getTemplatingEngine().evaluate("{{key | checked('true')}}-{{key | checked(true)}}-{{key | checked('1')}}",
                                               model);
        assertEquals("checked-checked-", html);
    }

    @Test
    public void checkedFilterEquivalenceFalse() throws Exception {

        JsonObject model = getJsonManager().create();
        model.put("key", "false");

        String html =
                getTemplatingEngine().evaluate("{{key | checked('false')}}-{{key | checked(false)}}-{{key | checked('0')}}",
                                               model);
        assertEquals("checked-checked-", html);
    }

    @Test
    public void checkedFilterEquivalenceNumber() throws Exception {

        JsonObject model = getJsonManager().create();
        model.put("key", 123);

        String html =
                getTemplatingEngine().evaluate("{{key | checked('123')}}-{{key | checked(123.00)}}-{{key | checked('123.2')}}",
                                               model);
        assertEquals("checked-checked-", html);
    }

    @Test
    public void checkedFilterEquivalenceTrueInverse() throws Exception {

        JsonObject model = getJsonManager().create();
        model.put("key", true);

        String html =
                getTemplatingEngine().evaluate("{{key | checked('true')}}-{{key | checked(true)}}-{{key | checked('1')}}",
                                               model);
        assertEquals("checked-checked-", html);
    }

    @Test
    public void checkedFilterEquivalenceFalseInverse() throws Exception {

        JsonObject model = getJsonManager().create();
        model.put("key", false);

        String html =
                getTemplatingEngine().evaluate("{{key | checked('false')}}-{{key | checked(false)}}-{{key | checked('0')}}",
                                               model);
        assertEquals("checked-checked-", html);
    }

    @Test
    public void checkedFilterEquivalenceNumberInverse() throws Exception {

        JsonObject model = getJsonManager().create();
        model.put("key", "123.0");

        String html =
                getTemplatingEngine().evaluate("{{key | checked(123)}}-{{key | checked(123.00)}}-{{key | checked('123.2')}}",
                                               model);
        assertEquals("checked-checked-", html);
    }

    @Test
    public void selectedFilterWithValue() throws Exception {

        JsonObject model = getJsonManager().create();

        JsonObject user = getJsonManager().create();
        user.put("color", "red");

        model.put("user", user);

        String html =
                getTemplatingEngine().evaluate("{{user.color | selected('blue')}}-{{user.color | selected('red')}}-{{user.color | selected('green')}}",
                                               model);
        assertEquals("-selected-", html);
    }

    @Test
    public void selectedFilterWithBoolean() throws Exception {

        JsonObject model = getJsonManager().create();

        JsonArray colors = getJsonManager().createArray();
        model.put("colors", colors);

        JsonObject color = getJsonManager().create();
        color.put("name", "blue");
        color.put("selected", false);
        colors.add(color);

        color = getJsonManager().create();
        color.put("name", "red");
        color.put("selected", true);
        colors.add(color);

        color = getJsonManager().create();
        color.put("name", "green");
        color.put("selected", false);
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
        model.put("user", user);
        user.put("color", "red");

        JsonArray validColors = getJsonManager().createArray();
        model.put("validColors", validColors);
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
        model.put("user", user);
        user.put("color", "red");

        String html = getTemplatingEngine().evaluate("{{user.color | selected(['blue', 'red', 'yellow'])}}",
                                                     model);
        assertEquals("selected", html);
    }

    @Test
    public void selectedFilterWithListNoMatch() throws Exception {

        JsonObject model = getJsonManager().create();

        JsonObject user = getJsonManager().create();
        model.put("user", user);
        user.put("color", "red");

        JsonArray validColors = getJsonManager().createArray();
        model.put("validColors", validColors);
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
        model.put("user", user);
        user.put("color", "red");

        String html = getTemplatingEngine().evaluate("{{user.color | selected(['blue', 'green', 'yellow'])}}",
                                                     model);
        assertEquals("", html);
    }

    @Test
    public void selectedFilterEquivalenceTrue() throws Exception {

        JsonObject model = getJsonManager().create();
        model.put("key", "true");

        String html =
                getTemplatingEngine().evaluate("{{key | selected('true')}}-{{key | selected(true)}}-{{key | selected('1')}}",
                                               model);
        assertEquals("selected-selected-", html);
    }

    @Test
    public void selectedFilterEquivalenceFalse() throws Exception {

        JsonObject model = getJsonManager().create();
        model.put("key", "false");

        String html =
                getTemplatingEngine().evaluate("{{key | selected('false')}}-{{key | selected(false)}}-{{key | selected('0')}}",
                                               model);
        assertEquals("selected-selected-", html);
    }

    @Test
    public void selectedFilterEquivalenceNumber() throws Exception {

        JsonObject model = getJsonManager().create();
        model.put("key", 123);

        String html =
                getTemplatingEngine().evaluate("{{key | selected('123')}}-{{key | selected(123.00)}}-{{key | selected('123.2')}}",
                                               model);
        assertEquals("selected-selected-", html);
    }

    @Test
    public void selectedFilterEquivalenceTrueInverse() throws Exception {

        JsonObject model = getJsonManager().create();
        model.put("key", true);

        String html =
                getTemplatingEngine().evaluate("{{key | selected('true')}}-{{key | selected(true)}}-{{key | selected('1')}}",
                                               model);
        assertEquals("selected-selected-", html);
    }

    @Test
    public void selectedFilterEquivalenceFalseInverse() throws Exception {

        JsonObject model = getJsonManager().create();
        model.put("key", false);

        String html =
                getTemplatingEngine().evaluate("{{key | selected('false')}}-{{key | selected(false)}}-{{key | selected('0')}}",
                                               model);
        assertEquals("selected-selected-", html);
    }

    @Test
    public void selectedFilterEquivalenceNumberInverse() throws Exception {

        JsonObject model = getJsonManager().create();
        model.put("key", "123.0");

        String html =
                getTemplatingEngine().evaluate("{{key | selected(123)}}-{{key | selected(123.00)}}-{{key | selected('123.2')}}",
                                               model);
        assertEquals("selected-selected-", html);
    }

    @Test
    public void filterValidationMessagesValid() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("name", "Stromgol");
        obj.put("age", 123);

        JsonObjectValidationSet validationSet = obj.validationSet();
        validationSet.validationNotBlank().jsonPath("name").validate();
        validationSet.validationGreater(100).jsonPath("age").validate();

        JsonObject model = getJsonManager().create();
        model.put("user.validation", validationSet);

        String html = getTemplatingEngine().evaluate("{{user.validation.name | validationMessages()}}",
                                                     model);
        assertEquals("", html);

        html = getTemplatingEngine().evaluate("{{user.nope | validationMessages()}}",
                                              model);
        assertEquals("", html);
    }

    @Test
    public void filterValidationMessagesInvalid() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("name", "");
        obj.put("age", 123);

        JsonObjectValidationSet validationSet = obj.validationSet();
        validationSet.validationNotBlank().jsonPath("name").validate();
        validationSet.validationGreater(100).jsonPath("age").validate();

        JsonObject model = getJsonManager().create();
        model.put("user.validation", validationSet);

        String html =
                getTemplatingEngine().evaluate("{{user.validation.name | validationMessages()}}",
                                               model);
        assertTrue(html.contains("msgError"));
        assertTrue(html.contains("validationMessages"));

        html = getTemplatingEngine().evaluate("{{user.validation.age | validationMessages()}}",
                                              model);
        assertEquals("", html);
    }

    @Test
    public void filterValidationGroupMessagesValid() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("name", "Stromgol");
        obj.put("age", 123);

        JsonObjectValidationSet validationSet = obj.validationSet();
        validationSet.validationNotBlank().jsonPath("name").validate();
        validationSet.validationGreater(100).jsonPath("age").validate();

        JsonObject model = getJsonManager().create();
        model.put("user.validation", validationSet);

        String html = getTemplatingEngine().evaluate("{{user.validation.name | validationGroupMessages()}}",
                                                     model);
        assertEquals("", html);

        html = getTemplatingEngine().evaluate("{{user.validation.age | validationGroupMessages()}}",
                                              model);
        assertEquals("", html);
    }

    @Test
    public void filterValidationGroupMessagesInvalid() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("name", "");
        obj.put("age", 123);

        JsonObjectValidationSet validationSet = obj.validationSet();
        validationSet.validationNotBlank().jsonPath("name").validate();
        validationSet.validationGreater(100).jsonPath("age").validate();

        JsonObject model = getJsonManager().create();
        model.put("user.validation", validationSet);

        String html =
                getTemplatingEngine().evaluate("{{user.validation.name | validationGroupMessages()}}",
                                               model);
        assertTrue(html.contains("msgError"));
        assertTrue(html.contains("validationGroupMessages"));

        html = getTemplatingEngine().evaluate("{{user.validation.age | validationGroupMessages()}}",
                                              model);
        assertEquals("", html);
    }

    @Test
    public void filterValidationClassValid() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("name", "Stromgol");
        obj.put("age", 123);

        JsonObjectValidationSet validationSet = obj.validationSet();
        validationSet.validationNotBlank().jsonPath("name").validate();
        validationSet.validationGreater(100).jsonPath("age").validate();

        JsonObject model = getJsonManager().create();
        model.put("user.validation", validationSet);

        String html = getTemplatingEngine().evaluate("{{user.validation.name | validationClass()}}",
                                                     model);
        assertEquals("has-no-message", html);

        html = getTemplatingEngine().evaluate("{{user.validation.age | validationClass()}}",
                                              model);
        assertEquals("has-no-message", html);
    }

    @Test
    public void filterValidationClassInvalid() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("name", "");
        obj.put("age", 123);

        JsonObjectValidationSet validationSet = obj.validationSet();
        validationSet.validationNotBlank().jsonPath("name").validate();
        validationSet.validationGreater(100).jsonPath("age").addMessageOnSuccess().validate();

        JsonObject model = getJsonManager().create();
        model.put("user.validation", validationSet);

        String html =
                getTemplatingEngine().evaluate("{{user.validation.name | validationClass()}}",
                                               model);
        assertEquals("has-error", html);

        html = getTemplatingEngine().evaluate("{{user.validation.age | validationClass()}}",
                                              model);
        assertEquals("has-success", html);
    }

    @Test
    public void filterFreshYes() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("name", "Stromgol");
        obj.put("age", 123);

        JsonObject model = getJsonManager().create();

        String html = getTemplatingEngine().evaluate(" {% if user.validation._ | validationFresh() %}fresh{% endif %}",
                                                     model)
                                           .trim();
        assertEquals("fresh", html);
    }

    @Test
    public void filterFreshNot() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("name", "Stromgol");
        obj.put("age", 123);

        JsonObjectValidationSet validationSet = obj.validationSet();
        validationSet.validationNotBlank().jsonPath("name").validate();
        validationSet.validationGreater(100).jsonPath("age").validate();

        JsonObject model = getJsonManager().create();
        model.put("user.validation", validationSet);

        String html = getTemplatingEngine().evaluate(" {% if user.validation._ | validationFresh() %}fresh{% endif %}",
                                                     model)
                                           .trim();
        assertEquals("", html);
    }

    @Test
    public void filterSubmittedYes() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("name", "Stromgol");
        obj.put("age", 123);

        JsonObjectValidationSet validationSet = obj.validationSet();
        validationSet.validationNotBlank().jsonPath("name").validate();
        validationSet.validationGreater(100).jsonPath("age").validate();

        JsonObject model = getJsonManager().create();
        model.put("user.validation", validationSet);

        String html = getTemplatingEngine().evaluate(" {% if user.validation._ | validationSubmitted() %}submitted{% endif %}",
                                                     model)
                                           .trim();
        assertEquals("submitted", html);
    }

    @Test
    public void filterSubmittedNot() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("name", "Stromgol");
        obj.put("age", 123);

        JsonObject model = getJsonManager().create();

        String html = getTemplatingEngine().evaluate(" {% if user.validation._ | validationSubmitted() %}fresh{% endif %}",
                                                     model)
                                           .trim();
        assertEquals("", html);
    }

    @Test
    public void filterHasSuccessesFieldFalse() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("name", "Stromgol");
        obj.put("age", 123);

        JsonObjectValidationSet validationSet = obj.validationSet();
        validationSet.validationNotBlank().jsonPath("name").addMessageOnSuccess().validate();
        validationSet.validationGreater(100).jsonPath("age").validate();

        JsonObject model = getJsonManager().create();
        model.put("user.validation", validationSet);

        String html = getTemplatingEngine().evaluate(" {% if user.validation.age | validationHasSuccesses() %}yes{% endif %}",
                                                     model)
                                           .trim();
        assertEquals("", html);
    }

    @Test
    public void filterHasSuccessesFieldTrue() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("name", "Stromgol");
        obj.put("age", 123);

        JsonObjectValidationSet validationSet = obj.validationSet();
        validationSet.validationNotBlank().jsonPath("name").addMessageOnSuccess().validate();
        validationSet.validationGreater(100).jsonPath("age").addMessageOnSuccess().validate();

        JsonObject model = getJsonManager().create();
        model.put("user.validation", validationSet);

        String html = getTemplatingEngine().evaluate(" {% if user.validation.age | validationHasSuccesses() %}yes{% endif %}",
                                                     model)
                                           .trim();
        assertEquals("yes", html);
    }

    @Test
    public void filterHasSuccessesFormFalse() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("name", "");
        obj.put("age", 123);

        JsonObjectValidationSet validationSet = obj.validationSet();
        validationSet.validationNotBlank().jsonPath("name").validate();
        validationSet.validationGreater(100).jsonPath("age").validate();

        JsonObject model = getJsonManager().create();
        model.put("user.validation", validationSet);

        String html = getTemplatingEngine().evaluate(" {% if user.validation._ | validationHasSuccesses() %}yes{% endif %}",
                                                     model)
                                           .trim();
        assertEquals("", html);
    }

    @Test
    public void filterHasSuccessesFormTrue() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("name", "");
        obj.put("age", 123);

        JsonObjectValidationSet validationSet = obj.validationSet();
        validationSet.validationNotBlank().jsonPath("name").validate();
        validationSet.validationGreater(100).jsonPath("age").addMessageOnSuccess().validate();

        JsonObject model = getJsonManager().create();
        model.put("user.validation", validationSet);

        String html = getTemplatingEngine().evaluate(" {% if user.validation._ | validationHasSuccesses() %}yes{% endif %}",
                                                     model)
                                           .trim();
        assertEquals("yes", html);
    }

    @Test
    public void filterHasErrorsFieldFalse() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("name", "");
        obj.put("age", 123);

        JsonObjectValidationSet validationSet = obj.validationSet();
        validationSet.validationNotBlank().jsonPath("name").validate();
        validationSet.validationGreater(100).jsonPath("age").validate();

        JsonObject model = getJsonManager().create();
        model.put("user.validation", validationSet);

        String html = getTemplatingEngine().evaluate(" {% if user.validation.age | validationHasErrors() %}yes{% endif %}",
                                                     model)
                                           .trim();
        assertEquals("", html);
    }

    @Test
    public void filterHasErrorsFieldTrue() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("name", "");
        obj.put("age", 12);

        JsonObjectValidationSet validationSet = obj.validationSet();
        validationSet.validationNotBlank().jsonPath("name").validate();
        validationSet.validationGreater(100).jsonPath("age").validate();

        JsonObject model = getJsonManager().create();
        model.put("user.validation", validationSet);

        String html = getTemplatingEngine().evaluate(" {% if user.validation.age | validationHasErrors() %}yes{% endif %}",
                                                     model)
                                           .trim();
        assertEquals("yes", html);
    }

    @Test
    public void filterHasErrorsFormFalse() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("name", "Stromgol");
        obj.put("age", 123);

        JsonObjectValidationSet validationSet = obj.validationSet();
        validationSet.validationNotBlank().jsonPath("name").validate();
        validationSet.validationGreater(100).jsonPath("age").validate();

        JsonObject model = getJsonManager().create();
        model.put("user.validation", validationSet);

        String html = getTemplatingEngine().evaluate(" {% if user.validation._ | validationHasErrors() %}yes{% endif %}",
                                                     model)
                                           .trim();
        assertEquals("", html);
    }

    @Test
    public void filterHasErrorsFormTrue() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("name", "");
        obj.put("age", 123);

        JsonObjectValidationSet validationSet = obj.validationSet();
        validationSet.validationNotBlank().jsonPath("name").validate();
        validationSet.validationGreater(100).jsonPath("age").validate();

        JsonObject model = getJsonManager().create();
        model.put("user.validation", validationSet);

        String html = getTemplatingEngine().evaluate(" {% if user.validation._ | validationHasErrors() %}yes{% endif %}",
                                                     model)
                                           .trim();
        assertEquals("yes", html);
    }

    @Test
    public void filterHasWarningsFieldFalse() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("name", "");
        obj.put("age", 123);

        JsonObjectValidationSet validationSet = obj.validationSet();
        validationSet.validationNotBlank().jsonPath("name").validate();
        validationSet.validationGreater(100).jsonPath("age").validate();

        JsonObject model = getJsonManager().create();
        model.put("user.validation", validationSet);

        String html = getTemplatingEngine().evaluate(" {% if user.validation.age | validationHasWarnings() %}yes{% endif %}",
                                                     model)
                                           .trim();
        assertEquals("", html);
    }

    @Test
    public void filterHasWarningsFieldFalse2() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("name", "");
        obj.put("age", 12);

        JsonObjectValidationSet validationSet = obj.validationSet();
        validationSet.validationNotBlank().jsonPath("name").validate();
        validationSet.validationGreater(100).jsonPath("age").validate();

        JsonObject model = getJsonManager().create();
        model.put("user.validation", validationSet);

        String html = getTemplatingEngine().evaluate(" {% if user.validation.age | validationHasWarnings() %}yes{% endif %}",
                                                     model)
                                           .trim();
        assertEquals("", html);
    }

    @Test
    public void filterHasWarningsFieldTrue() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("name", "");
        obj.put("age", 12);

        JsonObjectValidationSet validationSet = obj.validationSet();
        validationSet.validationNotBlank().jsonPath("name").validate();
        validationSet.validationGreater(100).jsonPath("age").treatErrorAsWarning().validate();

        JsonObject model = getJsonManager().create();
        model.put("user.validation", validationSet);

        String html = getTemplatingEngine().evaluate(" {% if user.validation.age | validationHasWarnings() %}yes{% endif %}",
                                                     model)
                                           .trim();
        assertEquals("yes", html);
    }

    @Test
    public void filterHasWarningsFormFalse() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("name", "Stromgol");
        obj.put("age", 123);

        JsonObjectValidationSet validationSet = obj.validationSet();
        validationSet.validationNotBlank().jsonPath("name").validate();
        validationSet.validationGreater(100).jsonPath("age").validate();

        JsonObject model = getJsonManager().create();
        model.put("user.validation", validationSet);

        String html = getTemplatingEngine().evaluate(" {% if user.validation._ | validationHasWarnings() %}yes{% endif %}",
                                                     model)
                                           .trim();
        assertEquals("", html);
    }

    @Test
    public void filterHasWarningsFormFalse2() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("name", "");
        obj.put("age", 123);

        JsonObjectValidationSet validationSet = obj.validationSet();
        validationSet.validationNotBlank().jsonPath("name").validate();
        validationSet.validationGreater(100).jsonPath("age").validate();

        JsonObject model = getJsonManager().create();
        model.put("user.validation", validationSet);

        String html = getTemplatingEngine().evaluate(" {% if user.validation._ | validationHasWarnings() %}yes{% endif %}",
                                                     model)
                                           .trim();
        assertEquals("", html);
    }

    @Test
    public void filterHasWarningsFormTrue() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("name", "");
        obj.put("age", 123);

        JsonObjectValidationSet validationSet = obj.validationSet();
        validationSet.validationNotBlank().jsonPath("name").treatErrorAsWarning().validate();
        validationSet.validationGreater(100).jsonPath("age").validate();

        JsonObject model = getJsonManager().create();
        model.put("user.validation", validationSet);

        String html = getTemplatingEngine().evaluate(" {% if user.validation._ | validationHasWarnings() %}yes{% endif %}",
                                                     model)
                                           .trim();
        assertEquals("yes", html);
    }

    @Test
    public void filterIsValidFieldTrue() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("name", "Stromgol");
        obj.put("age", 123);

        JsonObjectValidationSet validationSet = obj.validationSet();
        validationSet.validationNotBlank().jsonPath("name").validate();
        validationSet.validationGreater(100).jsonPath("age").validate();

        JsonObject model = getJsonManager().create();
        model.put("user.validation", validationSet);

        String html = getTemplatingEngine().evaluate(" {% if user.validation.name | validationIsValid() %}yes{% endif %}",
                                                     model)
                                           .trim();
        assertEquals("yes", html);
    }

    @Test
    public void filterIsValidFieldTrue2() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("name", "");
        obj.put("age", 12);

        JsonObjectValidationSet validationSet = obj.validationSet();
        validationSet.validationNotBlank().jsonPath("name").treatErrorAsWarning().validate();
        validationSet.validationGreater(100).jsonPath("age").validate();

        JsonObject model = getJsonManager().create();
        model.put("user.validation", validationSet);

        String html = getTemplatingEngine().evaluate(" {% if user.validation.name | validationIsValid() %}yes{% endif %}",
                                                     model)
                                           .trim();
        assertEquals("yes", html);
    }

    @Test
    public void filterIsValidFieldFalse() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("name", "");
        obj.put("age", 12);

        JsonObjectValidationSet validationSet = obj.validationSet();
        validationSet.validationNotBlank().jsonPath("name").validate();
        validationSet.validationGreater(100).jsonPath("age").validate();

        JsonObject model = getJsonManager().create();
        model.put("user.validation", validationSet);

        String html = getTemplatingEngine().evaluate(" {% if user.validation.name | validationIsValid() %}yes{% endif %}",
                                                     model)
                                           .trim();
        assertEquals("", html);
    }

    @Test
    public void filterIsValidFormTrue() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("name", "Stromgol");
        obj.put("age", 123);

        JsonObjectValidationSet validationSet = obj.validationSet();
        validationSet.validationNotBlank().jsonPath("name").validate();
        validationSet.validationGreater(100).jsonPath("age").validate();

        JsonObject model = getJsonManager().create();
        model.put("user.validation", validationSet);

        String html = getTemplatingEngine().evaluate(" {% if user.validation._ | validationIsValid() %}yes{% endif %}",
                                                     model)
                                           .trim();
        assertEquals("yes", html);
    }

    @Test
    public void filterIsValidFormTrue2() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("name", "");
        obj.put("age", 123);

        JsonObjectValidationSet validationSet = obj.validationSet();
        validationSet.validationNotBlank().jsonPath("name").treatErrorAsWarning().validate();
        validationSet.validationGreater(100).jsonPath("age").validate();

        JsonObject model = getJsonManager().create();
        model.put("user.validation", validationSet);

        String html = getTemplatingEngine().evaluate(" {% if user.validation._ | validationIsValid() %}yes{% endif %}",
                                                     model)
                                           .trim();
        assertEquals("yes", html);
    }

    @Test
    public void filterIsValidFormFalse() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("name", "");
        obj.put("age", 123);

        JsonObjectValidationSet validationSet = obj.validationSet();
        validationSet.validationNotBlank().jsonPath("name").validate();
        validationSet.validationGreater(100).jsonPath("age").validate();

        JsonObject model = getJsonManager().create();
        model.put("user.validation", validationSet);

        String html = getTemplatingEngine().evaluate(" {% if user.validation._ | validationIsValid() %}yes{% endif %}",
                                                     model)
                                           .trim();
        assertEquals("", html);
    }

    @Test
    public void filterGet() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("name", "Stromgol");

        JsonObject model = getJsonManager().create();
        model.put("toto.titi", obj);

        String html = getTemplatingEngine().evaluate("{{('toto.ti' + 'ti.na' + 'me') | get()}}", model).trim();
        assertEquals("Stromgol", html);
    }

    @Test
    public void functionGet() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("name", "Stromgol");

        JsonObject model = getJsonManager().create();
        model.put("toto.titi", obj);

        String html = getTemplatingEngine().evaluate("{{get('toto.ti' + 'ti.na' + 'me')}}", model).trim();
        assertEquals("Stromgol", html);
    }

    @Test
    public void functionGetVariable() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("name", "Stromgol");

        JsonObject model = getJsonManager().create();
        model.put("toto.titi", obj);

        String html = getTemplatingEngine().evaluate("{% set myKey = 'toto.ti' + 'ti.na' + 'me' %} {{get(myKey)}}", model).trim();
        assertEquals("Stromgol", html);
    }

}
