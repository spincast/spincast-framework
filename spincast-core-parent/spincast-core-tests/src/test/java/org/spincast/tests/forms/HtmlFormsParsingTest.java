package org.spincast.tests.forms;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.spincast.core.exchange.DefaultRequestContext;
import org.spincast.core.json.JsonArray;
import org.spincast.core.json.JsonManager;
import org.spincast.core.json.JsonObject;
import org.spincast.core.routing.Handler;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.testing.defaults.NoAppStartHttpServerTestingBase;

import com.google.inject.Inject;

public class HtmlFormsParsingTest extends NoAppStartHttpServerTestingBase {

    @Inject
    protected JsonManager jsonManager;

    protected JsonManager getJsonManager() {
        return this.jsonManager;
    }

    @Test
    public void simple() throws Exception {

        getRouter().POST("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String name = context.request().getFormBodyAsJsonObject().getString("name");
                assertEquals("Stromgol", name);

                JsonObject jsonObj = context.request().getFormBodyAsJsonObject();
                assertNotNull(jsonObj);
                name = jsonObj.getString("name");
                assertEquals("Stromgol", name);

                context.response().sendPlainText("ok");
            }
        });

        HttpResponse response = POST("/").addFormBodyFieldValue("name", "Stromgol").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("ok", response.getContentAsString());
    }

    @Test
    public void oneKeyTwoValues() throws Exception {

        getRouter().POST("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                JsonArray names = context.request().getFormBodyAsJsonObject().getJsonArray("name");
                assertNotNull(names);
                assertEquals(2, names.size());
                assertEquals("Stromgol", names.getString(0));
                assertEquals("Slomo", names.getString(1));

                context.response().sendPlainText("ok");
            }
        });

        HttpResponse response = POST("/").addFormBodyFieldValue("name[0]", "Stromgol")
                                         .addFormBodyFieldValue("name[1]", "Slomo").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("ok", response.getContentAsString());
    }

    @Test
    public void notArrayKeyOneValue() throws Exception {

        getRouter().POST("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                try {
                    context.request().getFormBodyAsJsonObject().getJsonArray("name");
                    fail();
                } catch (Exception ex) {
                }

                context.response().sendPlainText("ok");
            }
        });

        HttpResponse response = POST("/").addFormBodyFieldValue("name", "Stromgol").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("ok", response.getContentAsString());
    }

    @Test
    public void notArrayKeyMultipleValues() throws Exception {

        getRouter().POST("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                JsonArray array = context.request().getFormBodyAsJsonObject().getJsonArray("name");
                assertNotNull(array);

                // Order is not garanteed
                Set<String> set = new HashSet<>();
                set.add(array.getString(0));
                set.add(array.getString(1));

                assertTrue(set.contains("Stromgol"));
                assertTrue(set.contains("Slomo"));

                context.response().sendPlainText("ok");
            }
        });

        HttpResponse response = POST("/").addFormBodyFieldValue("name", "Stromgol")
                                         .addFormBodyFieldValue("name", "Slomo").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("ok", response.getContentAsString());
    }

    @Test
    public void arrayKeyOneValue() throws Exception {

        getRouter().POST("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                JsonArray array = context.request().getFormBodyAsJsonObject().getJsonArray("name");
                assertNotNull(array);
                assertEquals("Stromgol", array.getString(0));

                context.response().sendPlainText("ok");
            }
        });

        HttpResponse response = POST("/").addFormBodyFieldValue("name[]", "Stromgol").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("ok", response.getContentAsString());
    }

    @Test
    public void arrayKeyMultipleValues() throws Exception {

        getRouter().POST("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                JsonArray array = context.request().getFormBodyAsJsonObject().getJsonArray("name");
                assertNotNull(array);

                // Order is not garanteed
                Set<String> set = new HashSet<>();
                set.add(array.getString(0));
                set.add(array.getString(1));

                assertTrue(set.contains("Stromgol"));
                assertTrue(set.contains("Slomo"));

                context.response().sendPlainText("ok");
            }
        });

        HttpResponse response = POST("/").addFormBodyFieldValue("name[]", "Stromgol")
                                         .addFormBodyFieldValue("name[]", "Slomo").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("ok", response.getContentAsString());
    }

    @Test
    public void arrayMultipleElementsAtSamePositionLastOneWins() throws Exception {

        getRouter().POST("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                JsonArray array = context.request().getFormBodyAsJsonObject().getJsonArray("name");
                assertNotNull(array);
                assertEquals(2, array.size());

                assertEquals("Stromgol1", array.getString(0));
                assertEquals("Slomo", array.getString(1));

                context.response().sendPlainText("ok");
            }
        });

        HttpResponse response = POST("/").addFormBodyFieldValue("name[0]", "Stromgol1")
                                         .addFormBodyFieldValue("name[1]", "first")
                                         .addFormBodyFieldValue("name[1]", "Slomo").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("ok", response.getContentAsString());
    }

    @Test
    public void oneValueInArray() throws Exception {

        getRouter().POST("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                JsonArray names = context.request().getFormBodyAsJsonObject().getJsonArray("name");
                assertNotNull(names);
                assertEquals(1, names.size());
                assertEquals("Stromgol", names.getString(0));

                context.response().sendPlainText("ok");
            }
        });

        HttpResponse response = POST("/").addFormBodyFieldValue("name[0]", "Stromgol").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("ok", response.getContentAsString());
    }

    @Test
    public void formDataObjectIsImmutable() throws Exception {

        getRouter().POST("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                JsonObject formDatas = context.request().getFormBodyAsJsonObject();
                assertNotNull(formDatas);
                assertEquals("Stromgol", formDatas.getString("name"));

                try {
                    formDatas.set("nope", "immutable");
                    fail();
                } catch (Exception ex) {
                }
                JsonObject mutableClone = formDatas.clone(true);
                assertNotNull(mutableClone);
                mutableClone.set("key1", "value1");

                assertEquals("value1", mutableClone.getString("key1"));

                assertEquals("nope", formDatas.getString("key1", "nope"));

                context.response().sendPlainText("ok");
            }
        });

        HttpResponse response = POST("/").addFormBodyFieldValue("name", "Stromgol").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("ok", response.getContentAsString());
    }

    @Test
    public void multipleLevels() throws Exception {

        getRouter().POST("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                JsonObject jsonObj = context.request().getFormBodyAsJsonObject();
                assertNotNull(jsonObj);

                String defaultValue = jsonObj.getJsonObjectOrEmpty("user2")
                                             .getJsonObjectOrEmpty("child2")
                                             .getJsonArrayOrEmpty("books")
                                             .getJsonObjectOrEmpty(1)
                                             .getJsonObjectOrEmpty("author")
                                             .getJsonObjectOrEmpty("info")
                                             .getJsonArrayOrEmpty("names")
                                             .getArrayFirstString(123, "defaultValue");
                assertEquals("defaultValue", defaultValue);

                String name = jsonObj.getJsonObjectOrEmpty("user2")
                                     .getJsonObjectOrEmpty("child2")
                                     .getJsonArrayOrEmpty("books")
                                     .getJsonObjectOrEmpty(1)
                                     .getJsonObjectOrEmpty("author")
                                     .getJsonObjectOrEmpty("info")
                                     .getJsonArrayOrEmpty("names")
                                     .getArrayFirstString(0);

                assertNotNull(name);
                assertEquals("Stromgol1", name);

                name = jsonObj.getJsonObjectOrEmpty("user2")
                              .getJsonObjectOrEmpty("child2")
                              .getJsonArrayOrEmpty("books")
                              .getJsonObjectOrEmpty(1)
                              .getJsonObjectOrEmpty("author")
                              .getJsonObjectOrEmpty("info")
                              .getJsonArrayOrEmpty("names")
                              .getJsonArrayOrEmpty(0).getString(1);

                assertNotNull(name);
                assertEquals("Stromgol2", name);

                name = jsonObj.getJsonObjectOrEmpty("user2")
                              .getJsonObjectOrEmpty("child2")
                              .getJsonArrayOrEmpty("books")
                              .getJsonObjectOrEmpty(1)
                              .getJsonObjectOrEmpty("author")
                              .getJsonObjectOrEmpty("info")
                              .getJsonArrayOrEmpty("names")
                              .getString(2);

                assertNotNull(name);
                assertEquals("Slomo", name);

                context.response().sendPlainText("ok");
            }
        });

        HttpResponse response =
                POST("/").addFormBodyFieldValue("user2.child2.books[1].author[\"info\"]['names'][0][0]", "Stromgol1")
                         .addFormBodyFieldValue("user2.child2.books[1].author[\"info\"]['names'][0][1]", "Stromgol2")
                         .addFormBodyFieldValue("user2.child2.books[1].author[\"info\"]['names'][2]", "Slomo")
                         .send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("ok", response.getContentAsString());
    }

    @Test
    public void multipleLevelsOrder2() throws Exception {

        getRouter().POST("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                JsonObject jsonObj = context.request().getFormBodyAsJsonObject();
                assertNotNull(jsonObj);

                String defaultValue = jsonObj.getJsonObjectOrEmpty("user2")
                                             .getJsonObjectOrEmpty("child2")
                                             .getJsonArrayOrEmpty("books")
                                             .getJsonObjectOrEmpty(1)
                                             .getJsonObjectOrEmpty("author")
                                             .getJsonObjectOrEmpty("info")
                                             .getJsonArrayOrEmpty("names")
                                             .getArrayFirstString(123, "defaultValue");
                assertEquals("defaultValue", defaultValue);

                String name = jsonObj.getJsonObjectOrEmpty("user2")
                                     .getJsonObjectOrEmpty("child2")
                                     .getJsonArrayOrEmpty("books")
                                     .getJsonObjectOrEmpty(1)
                                     .getJsonObjectOrEmpty("author")
                                     .getJsonObjectOrEmpty("info")
                                     .getJsonArrayOrEmpty("names")
                                     .getArrayFirstString(0);

                assertNotNull(name);
                assertEquals("Stromgol1", name);

                name = jsonObj.getJsonObjectOrEmpty("user2")
                              .getJsonObjectOrEmpty("child2")
                              .getJsonArrayOrEmpty("books")
                              .getJsonObjectOrEmpty(1)
                              .getJsonObjectOrEmpty("author")
                              .getJsonObjectOrEmpty("info")
                              .getJsonArrayOrEmpty("names")
                              .getJsonArrayOrEmpty(0).getString(1);

                assertNotNull(name);
                assertEquals("Stromgol2", name);

                name = jsonObj.getJsonObjectOrEmpty("user2")
                              .getJsonObjectOrEmpty("child2")
                              .getJsonArrayOrEmpty("books")
                              .getJsonObjectOrEmpty(1)
                              .getJsonObjectOrEmpty("author")
                              .getJsonObjectOrEmpty("info")
                              .getJsonArrayOrEmpty("names")
                              .getString(2);

                assertNotNull(name);
                assertEquals("Slomo", name);

                context.response().sendPlainText("ok");
            }
        });

        HttpResponse response =
                POST("/").addFormBodyFieldValue("user2.child2.books[1].author[\"info\"]['names'][0][0]", "Stromgol1")
                         .addFormBodyFieldValue("user2.child2.books[1].author[\"info\"]['names'][2]", "Slomo")
                         .addFormBodyFieldValue("user2.child2.books[1].author[\"info\"]['names'][0][1]", "Stromgol2")
                         .send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("ok", response.getContentAsString());
    }

    @Test
    public void multipleLevelsOrder3() throws Exception {

        getRouter().POST("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                JsonObject jsonObj = context.request().getFormBodyAsJsonObject();
                assertNotNull(jsonObj);

                String defaultValue = jsonObj.getJsonObjectOrEmpty("user2")
                                             .getJsonObjectOrEmpty("child2")
                                             .getJsonArrayOrEmpty("books")
                                             .getJsonObjectOrEmpty(1)
                                             .getJsonObjectOrEmpty("author")
                                             .getJsonObjectOrEmpty("info")
                                             .getJsonArrayOrEmpty("names")
                                             .getArrayFirstString(123, "defaultValue");
                assertEquals("defaultValue", defaultValue);

                String name = jsonObj.getJsonObjectOrEmpty("user2")
                                     .getJsonObjectOrEmpty("child2")
                                     .getJsonArrayOrEmpty("books")
                                     .getJsonObjectOrEmpty(1)
                                     .getJsonObjectOrEmpty("author")
                                     .getJsonObjectOrEmpty("info")
                                     .getJsonArrayOrEmpty("names")
                                     .getArrayFirstString(0);

                assertNotNull(name);
                assertEquals("Stromgol1", name);

                name = jsonObj.getJsonObjectOrEmpty("user2")
                              .getJsonObjectOrEmpty("child2")
                              .getJsonArrayOrEmpty("books")
                              .getJsonObjectOrEmpty(1)
                              .getJsonObjectOrEmpty("author")
                              .getJsonObjectOrEmpty("info")
                              .getJsonArrayOrEmpty("names")
                              .getJsonArrayOrEmpty(0).getString(1);

                assertNotNull(name);
                assertEquals("Stromgol2", name);

                name = jsonObj.getJsonObjectOrEmpty("user2")
                              .getJsonObjectOrEmpty("child2")
                              .getJsonArrayOrEmpty("books")
                              .getJsonObjectOrEmpty(1)
                              .getJsonObjectOrEmpty("author")
                              .getJsonObjectOrEmpty("info")
                              .getJsonArrayOrEmpty("names")
                              .getString(2);

                assertNotNull(name);
                assertEquals("Slomo", name);

                context.response().sendPlainText("ok");
            }
        });

        HttpResponse response =
                POST("/").addFormBodyFieldValue("user2.child2.books[1].author[\"info\"]['names'][2]", "Slomo")
                         .addFormBodyFieldValue("user2.child2.books[1].author[\"info\"]['names'][0][0]", "Stromgol1")
                         .addFormBodyFieldValue("user2.child2.books[1].author[\"info\"]['names'][0][1]", "Stromgol2")
                         .send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("ok", response.getContentAsString());
    }

    @Test
    public void emptyIndexesAreFilledWithNulls() throws Exception {

        getRouter().POST("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                JsonObject jsonObj = context.request().getFormBodyAsJsonObject();
                assertNotNull(jsonObj);

                Object val = jsonObj.getJsonArrayOrEmpty("books")
                                    .getJsonObjectOrEmpty(0)
                                    .getJsonArrayOrEmpty("titi")
                                    .getJsonObjectOrEmpty(0)
                                    .getString("toto");
                assertEquals("Slomo", val);

                val = jsonObj.getJsonArrayOrEmpty("books")
                             .getJsonObjectOrEmpty(0)
                             .getJsonArrayOrEmpty("titi")
                             .getJsonObject(1, getJsonManager().create());
                assertEquals(null, val);

                val = jsonObj.getJsonArrayOrEmpty("books")
                             .getJsonObjectOrEmpty(0)
                             .getJsonArrayOrEmpty("titi")
                             .getJsonObject(2, getJsonManager().create());
                assertEquals(null, val);

                val = jsonObj.getJsonArrayOrEmpty("books")
                             .getJsonObjectOrEmpty(0)
                             .getJsonArrayOrEmpty("titi")
                             .getJsonObject(3, null);
                assertNotNull(val);

                val = jsonObj.getJsonArrayOrEmpty("books")
                             .getJsonObjectOrEmpty(0)
                             .getJsonArrayOrEmpty("titi")
                             .getJsonObject(4, getJsonManager().create());
                assertNotNull(val);

                val = jsonObj.getJsonArrayOrEmpty("books")
                             .getJsonObjectOrEmpty(2)
                             .getJsonArrayOrEmpty("titi")
                             .getJsonObjectOrEmpty(2)
                             .getString("toto");
                assertEquals("Stromgol1", val);

                val = jsonObj.getJsonArrayOrEmpty("books")
                             .getJsonObject(1, getJsonManager().create());
                assertEquals(null, val);

                context.response().sendPlainText("ok");
            }
        });

        HttpResponse response =
                POST("/").addFormBodyFieldValue("books[0].titi[0].toto", "Slomo")
                         .addFormBodyFieldValue("books[0].titi[3].toto", "Stromgol2")
                         .addFormBodyFieldValue("books[2].titi[2].toto", "Stromgol1")

                         .send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("ok", response.getContentAsString());
    }

}
