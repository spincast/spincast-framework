package org.spincast.tests.forms;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;
import org.spincast.core.exchange.IDefaultRequestContext;
import org.spincast.core.json.IJsonObject;
import org.spincast.core.routing.IHandler;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.defaults.tests.SpincastDefaultNoAppIntegrationTestBase;
import org.spincast.plugins.httpclient.IHttpResponse;
import org.spincast.shaded.org.apache.http.HttpStatus;

public class HtmlFormsParsingTest extends SpincastDefaultNoAppIntegrationTestBase {

    @Test
    public void simple() throws Exception {

        getRouter().POST("/").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                String name = context.request().getFormDataFirst("name");
                assertEquals("Stromgol", name);

                IJsonObject jsonObj = context.request().getFormDatas();
                assertNotNull(jsonObj);
                name = jsonObj.getArrayFirstString("name");
                assertEquals("Stromgol", name);

                context.response().sendPlainText("ok");
            }
        });

        IHttpResponse response = POST("/").addEntityFormDataValue("name", "Stromgol").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("ok", response.getContentAsString());
    }

    @Test
    public void oneKeyTwoValues() throws Exception {

        getRouter().POST("/").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                List<String> names = context.request().getFormData("name");
                assertNotNull(names);
                assertEquals(2, names.size());
                assertEquals("Stromgol", names.get(0));
                assertEquals("Slomo", names.get(1));

                context.response().sendPlainText("ok");
            }
        });

        IHttpResponse response = POST("/").addEntityFormDataValue("name", "Stromgol")
                                          .addEntityFormDataValue("name", "Slomo").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("ok", response.getContentAsString());
    }

    @Test
    public void formDataObjectIsImmutable() throws Exception {

        getRouter().POST("/").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                IJsonObject formDatas = context.request().getFormDatas();
                assertNotNull(formDatas);
                assertEquals("Stromgol", formDatas.getArrayFirstString("name"));

                try {
                    formDatas.put("nope", "immutable");
                    fail();
                } catch(Exception ex) {
                }
                IJsonObject mutableClone = formDatas.clone();
                assertNotNull(mutableClone);
                mutableClone.put("key1", "value1");

                assertEquals("value1", mutableClone.getString("key1"));

                assertEquals("nope", formDatas.getString("key1", "nope"));

                context.response().sendPlainText("ok");
            }
        });

        IHttpResponse response = POST("/").addEntityFormDataValue("name", "Stromgol").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("ok", response.getContentAsString());
    }

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
                              .getArrayFirstString(2);

                assertNotNull(name);
                assertEquals("Slomo", name);

                context.response().sendPlainText("ok");
            }
        });

        IHttpResponse response =
                POST("/").addEntityFormDataValue("user2.child2.books[1].author[\"info\"]['names'][0]", "Stromgol1")
                         .addEntityFormDataValue("user2.child2.books[1].author[\"info\"]['names'][0]", "Stromgol2")
                         .addEntityFormDataValue("user2.child2.books[1].author[\"info\"]['names'][2]", "Slomo")
                         .send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("ok", response.getContentAsString());
    }

}
