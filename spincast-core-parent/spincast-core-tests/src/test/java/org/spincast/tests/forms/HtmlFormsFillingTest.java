package org.spincast.tests.forms;

import static org.junit.Assert.assertEquals;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;
import org.spincast.core.exchange.IDefaultRequestContext;
import org.spincast.core.json.IJsonArray;
import org.spincast.core.json.IJsonObject;
import org.spincast.core.routing.IHandler;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.defaults.tests.SpincastDefaultNoAppIntegrationTestBase;
import org.spincast.plugins.httpclient.IHttpResponse;
import org.spincast.shaded.org.apache.commons.io.IOUtils;
import org.spincast.shaded.org.apache.commons.lang3.StringUtils;
import org.spincast.shaded.org.apache.http.HttpStatus;

public class HtmlFormsFillingTest extends SpincastDefaultNoAppIntegrationTestBase {

    protected String formatHtml(String html) {

        if(StringUtils.isBlank(html)) {
            return "";
        }

        Document doc = Jsoup.parse(html);
        doc.outputSettings().indentAmount(4);
        doc.outputSettings().charset("UTF-8");
        doc.outputSettings().prettyPrint(true);
        doc.outputSettings().outline(true);
        //doc.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
        String result = doc.outerHtml();

        return result;
    }

    @Test
    public void fillHtmlForm() throws Exception {

        getRouter().GET("/").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                IJsonObject model = context.json().create();
                model.put("email", "test@example.com");

                IJsonObject obj1 = context.json().create();
                model.put("obj1", obj1);

                obj1.put("name", "Stromgol");
                IJsonArray arr1 = context.json().createArray();
                obj1.put("books", arr1);
                arr1.add("book1");
                arr1.add("book2");
                arr1.add("book3");

                IJsonObject obj2 = context.json().create();
                arr1.add(obj2);

                obj2.put("name", "obj2");

                context.response().sendTemplateHtml("/forms/form01_1.html", model);
            }
        });

        IHttpResponse response = GET("/").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.HTML.getMainVariationWithUtf8Charset(), response.getContentType());

        String expected = IOUtils.toString(this.getClass().getResourceAsStream("/forms/form01_2.html"), "UTF-8");
        assertEquals(expected, formatHtml(response.getContentAsString()));
    }

    @Test
    public void dynamicNumberOfElements() throws Exception {

        getRouter().GET("/").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                IJsonObject model = context.json().create();

                IJsonArray arr1 = context.json().createArray();
                model.put("books", arr1);
                arr1.add("book1");
                arr1.add("book2");
                arr1.add("book3");
                arr1.add("book4");
                arr1.add("book5");

                context.response().sendTemplateHtml("/forms/form02_1.html", model);
            }
        });

        IHttpResponse response = GET("/").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.HTML.getMainVariationWithUtf8Charset(), response.getContentType());

        String expected = IOUtils.toString(this.getClass().getResourceAsStream("/forms/form02_2.html"), "UTF-8");
        assertEquals(expected, formatHtml(response.getContentAsString()));
    }

    @Test
    public void checkboxes() throws Exception {

        getRouter().GET("/").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                IJsonObject model = context.json().create();

                IJsonArray arr1 = context.json().createArray();
                model.put("books", arr1);

                IJsonObject book1 = context.json().create();
                book1.put("value", "book1");
                book1.put("selected", true);
                arr1.add(book1);

                IJsonObject book2 = context.json().create();
                book2.put("value", "book2");
                book2.put("selected", false);
                arr1.add(book2);

                IJsonObject book3 = context.json().create();
                book3.put("value", "book3");
                book3.put("selected", true);
                arr1.add(book3);

                context.response().sendTemplateHtml("/forms/form03_1.html", model);
            }
        });

        IHttpResponse response = GET("/").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.HTML.getMainVariationWithUtf8Charset(), response.getContentType());

        String expected = IOUtils.toString(this.getClass().getResourceAsStream("/forms/form03_2.html"), "UTF-8");
        assertEquals(expected, formatHtml(response.getContentAsString()));
    }

    @Test
    public void radios() throws Exception {

        getRouter().GET("/").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                IJsonObject model = context.json().create();

                IJsonArray arr1 = context.json().createArray();
                model.put("books", arr1);

                IJsonObject book1 = context.json().create();
                book1.put("value", "book1");
                book1.put("text", "My book2");
                book1.put("selected", false);
                arr1.add(book1);

                IJsonObject book2 = context.json().create();
                book2.put("value", "book2");
                book2.put("text", "My book2");
                book2.put("selected", true);
                arr1.add(book2);

                IJsonObject book3 = context.json().create();
                book3.put("value", "book3");
                book3.put("text", "My book2");
                book3.put("selected", false);
                arr1.add(book3);

                context.response().sendTemplateHtml("/forms/form06_1.html", model);
            }
        });

        IHttpResponse response = GET("/").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.HTML.getMainVariationWithUtf8Charset(), response.getContentType());

        String expected = IOUtils.toString(this.getClass().getResourceAsStream("/forms/form06_2.html"), "UTF-8");
        assertEquals(expected, formatHtml(response.getContentAsString()));
    }

    @Test
    public void select() throws Exception {

        getRouter().GET("/").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                IJsonObject model = context.json().create();

                IJsonArray arr1 = context.json().createArray();
                model.put("books", arr1);

                IJsonObject book1 = context.json().create();
                book1.put("value", "book1");
                book1.put("text", "My book1");
                book1.put("selected", false);
                arr1.add(book1);

                IJsonObject book2 = context.json().create();
                book2.put("value", "book2");
                book2.put("text", "My book2");
                book2.put("selected", true);
                arr1.add(book2);

                IJsonObject book3 = context.json().create();
                book3.put("value", "book3");
                book3.put("text", "My book3");
                book3.put("selected", false);
                arr1.add(book3);

                context.response().sendTemplateHtml("/forms/form04_1.html", model);
            }
        });

        IHttpResponse response = GET("/").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.HTML.getMainVariationWithUtf8Charset(), response.getContentType());

        String expected = IOUtils.toString(this.getClass().getResourceAsStream("/forms/form04_2.html"), "UTF-8");
        assertEquals(expected, formatHtml(response.getContentAsString()));
    }

    @Test
    public void selectMultiple() throws Exception {

        getRouter().GET("/").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                IJsonObject model = context.json().create();

                IJsonArray arr1 = context.json().createArray();
                model.put("books", arr1);

                IJsonObject book1 = context.json().create();
                book1.put("value", "book1");
                book1.put("text", "My book1");
                book1.put("selected", true);
                arr1.add(book1);

                IJsonObject book2 = context.json().create();
                book2.put("value", "book2");
                book2.put("text", "My book2");
                book2.put("selected", true);
                arr1.add(book2);

                IJsonObject book3 = context.json().create();
                book3.put("value", "book3");
                book3.put("text", "My book3");
                book3.put("selected", false);
                arr1.add(book3);

                context.response().sendTemplateHtml("/forms/form05_1.html", model);
            }
        });

        IHttpResponse response = GET("/").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.HTML.getMainVariationWithUtf8Charset(), response.getContentType());

        String expected = IOUtils.toString(this.getClass().getResourceAsStream("/forms/form05_2.html"), "UTF-8");
        assertEquals(expected, formatHtml(response.getContentAsString()));
    }

    @Test
    public void deepReferences() throws Exception {

        getRouter().GET("/").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                IJsonObject model = context.json().create();

                IJsonObject user1 = context.json().create();
                model.put("user1", user1);
                IJsonObject user2 = context.json().create();
                model.put("user2", user2);

                IJsonObject child1 = context.json().create();
                user2.put("child1", child1);
                IJsonObject child2 = context.json().create();
                user2.put("child2", child2);

                IJsonArray arr1 = context.json().createArray();
                child2.put("books", arr1);

                IJsonObject book1 = context.json().create();
                book1.put("value", "book1");
                book1.put("text", "My book1");
                book1.put("selected", true);
                arr1.add(book1);

                IJsonObject book2 = context.json().create();
                book2.put("value", "book2");
                book2.put("text", "My book2");
                book2.put("selected", true);

                IJsonObject info = context.json().create();
                book2.put("info", info);
                info.put("title", "The Title");
                info.put("year", "1999");

                arr1.add(book2);

                IJsonObject book3 = context.json().create();
                book3.put("value", "book3");
                book3.put("text", "My book3");
                book3.put("selected", false);
                arr1.add(book3);

                context.response().sendTemplateHtml("/forms/form07_1.html", model);
            }
        });

        IHttpResponse response = GET("/").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.HTML.getMainVariationWithUtf8Charset(), response.getContentType());

        String expected = IOUtils.toString(this.getClass().getResourceAsStream("/forms/form07_2.html"), "UTF-8");
        assertEquals(expected, formatHtml(response.getContentAsString()));
    }

}
