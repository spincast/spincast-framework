package org.spincast.tests.forms;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.spincast.core.exchange.IDefaultRequestContext;
import org.spincast.core.json.IJsonArray;
import org.spincast.core.json.IJsonObject;
import org.spincast.core.routing.IHandler;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.defaults.tests.SpincastDefaultNoAppIntegrationTestBase;
import org.spincast.plugins.httpclient.IHttpResponse;
import org.spincast.shaded.org.apache.http.HttpStatus;

public class HtmlFormsParsingTest extends SpincastDefaultNoAppIntegrationTestBase {

    @Test
    public void formDatasModificationShouldNotAffectOriginalFormDatas() throws Exception {

        getRouter().POST("/").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                IJsonObject formDatas = context.request().getFormDatas();
                assertNotNull(formDatas);
                assertFalse(formDatas.isKeyExists("nope"));

                IJsonArray books = formDatas.getJsonArray("books");
                assertNotNull(books);
                assertEquals(2, books.size());
                assertEquals("aaa", books.getString(0));
                assertEquals("bbb", books.getString(1));

                // This shouldn't affect the original formDatas.
                formDatas.put("nope", "nope!");
                books.add("ccc");
                assertEquals(3, books.size());
                assertTrue(formDatas.isKeyExists("nope"));

                // Fresh, unaffected, FormDatas
                IJsonObject formDatasFresh = context.request().getFormDatas();
                assertNotNull(formDatasFresh);
                assertFalse(formDatasFresh.isKeyExists("nope"));

                books = formDatasFresh.getJsonArray("books");
                assertNotNull(books);
                assertEquals(2, books.size());
                assertEquals("aaa", books.getString(0));
                assertEquals("bbb", books.getString(1));

                context.response().sendPlainText("ok");
            }
        });

        List<String> values = new ArrayList<String>();
        values.add("aaa");
        values.add("bbb");

        IHttpResponse response = POST("/").setEntityFormData("books", values).send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("ok", response.getContentAsString());
    }

    @Test
    public void simple() throws Exception {

        getRouter().POST("/").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                String name = context.request().getFormDataFirst("name");
                assertEquals("Stromgol", name);

                IJsonObject jsonObj = context.request().getFormDatas();
                assertNotNull(jsonObj);
                name = jsonObj.getArrayFirstString("name", null);
                assertEquals("Stromgol", name);

                context.response().sendPlainText("ok");
            }
        });

        IHttpResponse response = POST("/").addEntityFormDataValue("name", "Stromgol").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("ok", response.getContentAsString());
    }

    @Ignore
    @Test
    public void multipleLevels() throws Exception {

        getRouter().POST("/").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                IJsonObject jsonObj = context.request().getFormDatas();
                assertNotNull(jsonObj);

                String defaultValue = jsonObj.getJsonObjectOrEmpty("user2")
                                             .getJsonObjectOrEmpty("child2")
                                             .getJsonArrayOrEmpty("books")
                                             .getJsonObjectOrEmpty(1)
                                             .getJsonObjectOrEmpty("info")
                                             .getString("nope", "defaultValue");
                assertEquals("defaultValue", defaultValue);

                String name = jsonObj.getJsonObjectOrEmpty("user2")
                                     .getJsonObjectOrEmpty("child2")
                                     .getJsonArrayOrEmpty("books")
                                     .getJsonObjectOrEmpty(1)
                                     .getJsonObjectOrEmpty("info")
                                     .getString("name", null);
                assertNotNull(name);
                assertEquals("Stromgol", name);

                context.response().sendPlainText("ok");
            }
        });

        IHttpResponse response = POST("/").addEntityFormDataValue("user2.child2.books[1].info.name", "Stromgol")
                                          .send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("ok", response.getContentAsString());
    }

}
