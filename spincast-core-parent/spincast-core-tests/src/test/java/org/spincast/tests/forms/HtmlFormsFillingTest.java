package org.spincast.tests.forms;

import static org.junit.Assert.assertEquals;

import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;
import org.spincast.core.exchange.DefaultRequestContext;
import org.spincast.core.json.JsonArray;
import org.spincast.core.json.JsonObject;
import org.spincast.core.routing.Handler;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.shaded.org.apache.commons.io.IOUtils;
import org.spincast.shaded.org.apache.commons.lang3.StringUtils;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.testing.defaults.NoAppStartHttpServerTestingBase;

public class HtmlFormsFillingTest extends NoAppStartHttpServerTestingBase {

    private static final Pattern REMOVE_TRAILING_PATTERN = Pattern.compile("\\p{Blank}+$", Pattern.MULTILINE);

    protected String formatHtml(String html) {

        if (StringUtils.isBlank(html)) {
            return "";
        }

        Document doc = Jsoup.parse(html);
        doc.outputSettings().indentAmount(4);
        doc.outputSettings().charset("UTF-8");
        doc.outputSettings().prettyPrint(true);
        doc.outputSettings().outline(true);
        //doc.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
        String result = doc.outerHtml();

        // TODO temp, to remove when JSoup release a stable version
        // containing the fix.
        // @see https://github.com/jhy/jsoup/issues/1689
        result = REMOVE_TRAILING_PATTERN.matcher(result).replaceAll("");

        return result;
    }

    @Test
    public void fillHtmlForm() throws Exception {

        getRouter().GET("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                JsonObject model = context.json().create();
                model.set("email", "test@example.com");

                JsonObject obj1 = context.json().create();
                model.set("obj1", obj1);

                obj1.set("name", "Stromgol");
                JsonArray arr1 = context.json().createArray();
                obj1.set("books", arr1);
                arr1.add("book1");
                arr1.add("book2");
                arr1.add("book3");

                JsonObject obj2 = context.json().create();
                arr1.add(obj2);

                obj2.set("name", "obj2");

                context.response().setModel(model);
                context.response().sendTemplateHtml("/forms/form01_1.html");
            }
        });

        HttpResponse response = GET("/").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.HTML.getMainVariationWithUtf8Charset(), response.getContentType());

        String expected = IOUtils.toString(this.getClass().getResourceAsStream("/forms/form01_2.html"), "UTF-8");
        expected = formatHtml(expected);
        String got = formatHtml(response.getContentAsString());
        assertEquals(expected, got);
    }

    @Test
    public void dynamicNumberOfElements() throws Exception {

        getRouter().GET("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                JsonObject model = context.json().create();

                JsonArray arr1 = context.json().createArray();
                model.set("books", arr1);
                arr1.add("book1");
                arr1.add("book2");
                arr1.add("book3");
                arr1.add("book4");
                arr1.add("book5");

                context.response().setModel(model);

                context.response().sendTemplateHtml("/forms/form02_1.html");
            }
        });

        HttpResponse response = GET("/").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.HTML.getMainVariationWithUtf8Charset(), response.getContentType());

        String expected = IOUtils.toString(this.getClass().getResourceAsStream("/forms/form02_2.html"), "UTF-8");
        expected = formatHtml(expected);
        assertEquals(expected, formatHtml(response.getContentAsString()));
    }

    @Test
    public void checkboxes() throws Exception {

        getRouter().GET("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                JsonObject model = context.json().create();

                JsonArray arr1 = context.json().createArray();
                model.set("books", arr1);

                JsonObject book1 = context.json().create();
                book1.set("value", "book1");
                book1.set("selected", true);
                arr1.add(book1);

                JsonObject book2 = context.json().create();
                book2.set("value", "book2");
                book2.set("selected", false);
                arr1.add(book2);

                JsonObject book3 = context.json().create();
                book3.set("value", "book3");
                book3.set("selected", true);
                arr1.add(book3);

                context.response().setModel(model);

                context.response().sendTemplateHtml("/forms/form03_1.html");
            }
        });

        HttpResponse response = GET("/").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.HTML.getMainVariationWithUtf8Charset(), response.getContentType());

        String expected = IOUtils.toString(this.getClass().getResourceAsStream("/forms/form03_2.html"), "UTF-8");
        expected = formatHtml(expected);
        assertEquals(expected, formatHtml(response.getContentAsString()));
    }

    @Test
    public void radios() throws Exception {

        getRouter().GET("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                JsonObject model = context.json().create();

                JsonArray arr1 = context.json().createArray();
                model.set("books", arr1);

                JsonObject book1 = context.json().create();
                book1.set("value", "book1");
                book1.set("text", "My book2");
                book1.set("selected", false);
                arr1.add(book1);

                JsonObject book2 = context.json().create();
                book2.set("value", "book2");
                book2.set("text", "My book2");
                book2.set("selected", true);
                arr1.add(book2);

                JsonObject book3 = context.json().create();
                book3.set("value", "book3");
                book3.set("text", "My book2");
                book3.set("selected", false);
                arr1.add(book3);

                context.response().setModel(model);

                context.response().sendTemplateHtml("/forms/form06_1.html");
            }
        });

        HttpResponse response = GET("/").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.HTML.getMainVariationWithUtf8Charset(), response.getContentType());

        String expected = IOUtils.toString(this.getClass().getResourceAsStream("/forms/form06_2.html"), "UTF-8");
        expected = formatHtml(expected);
        String got = formatHtml(response.getContentAsString());
        assertEquals(expected, got);
    }

    @Test
    public void select() throws Exception {

        getRouter().GET("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                JsonObject model = context.json().create();

                JsonArray arr1 = context.json().createArray();
                model.set("books", arr1);

                JsonObject book1 = context.json().create();
                book1.set("value", "book1");
                book1.set("text", "My book1");
                book1.set("selected", false);
                arr1.add(book1);

                JsonObject book2 = context.json().create();
                book2.set("value", "book2");
                book2.set("text", "My book2");
                book2.set("selected", true);
                arr1.add(book2);

                JsonObject book3 = context.json().create();
                book3.set("value", "book3");
                book3.set("text", "My book3");
                book3.set("selected", false);
                arr1.add(book3);

                context.response().setModel(model);

                context.response().sendTemplateHtml("/forms/form04_1.html");
            }
        });

        HttpResponse response = GET("/").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.HTML.getMainVariationWithUtf8Charset(), response.getContentType());

        String expected = IOUtils.toString(this.getClass().getResourceAsStream("/forms/form04_2.html"), "UTF-8");
        expected = formatHtml(expected);
        assertEquals(expected, formatHtml(response.getContentAsString()));
    }

    @Test
    public void selectMultiple() throws Exception {

        getRouter().GET("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                JsonObject model = context.json().create();

                JsonArray arr1 = context.json().createArray();
                model.set("books", arr1);

                JsonObject book1 = context.json().create();
                book1.set("value", "book1");
                book1.set("text", "My book1");
                book1.set("selected", true);
                arr1.add(book1);

                JsonObject book2 = context.json().create();
                book2.set("value", "book2");
                book2.set("text", "My book2");
                book2.set("selected", true);
                arr1.add(book2);

                JsonObject book3 = context.json().create();
                book3.set("value", "book3");
                book3.set("text", "My book3");
                book3.set("selected", false);
                arr1.add(book3);

                context.response().setModel(model);

                context.response().sendTemplateHtml("/forms/form05_1.html");
            }
        });

        HttpResponse response = GET("/").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.HTML.getMainVariationWithUtf8Charset(), response.getContentType());

        String expected = IOUtils.toString(this.getClass().getResourceAsStream("/forms/form05_2.html"), "UTF-8");
        expected = formatHtml(expected);
        assertEquals(expected, formatHtml(response.getContentAsString()));
    }

    @Test
    public void deepReferences() throws Exception {

        getRouter().GET("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                JsonObject model = context.json().create();

                JsonObject user1 = context.json().create();
                model.set("user1", user1);
                JsonObject user2 = context.json().create();
                model.set("user2", user2);

                JsonObject child1 = context.json().create();
                user2.set("child1", child1);
                JsonObject child2 = context.json().create();
                user2.set("child2", child2);

                JsonArray arr1 = context.json().createArray();
                child2.set("books", arr1);

                JsonObject book1 = context.json().create();
                book1.set("value", "book1");
                book1.set("text", "My book1");
                book1.set("selected", true);
                arr1.add(book1);

                JsonObject book2 = context.json().create();
                book2.set("value", "book2");
                book2.set("text", "My book2");
                book2.set("selected", true);

                JsonObject info = context.json().create();
                book2.set("info", info);
                info.set("title", "The Title");
                info.set("year", "1999");

                arr1.add(book2);

                JsonObject book3 = context.json().create();
                book3.set("value", "book3");
                book3.set("text", "My book3");
                book3.set("selected", false);
                arr1.add(book3);

                context.response().setModel(model);

                context.response().sendTemplateHtml("/forms/form07_1.html");
            }
        });

        HttpResponse response = GET("/").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.HTML.getMainVariationWithUtf8Charset(), response.getContentType());

        String expected = IOUtils.toString(this.getClass().getResourceAsStream("/forms/form07_2.html"), "UTF-8");
        expected = formatHtml(expected);
        assertEquals(expected, formatHtml(response.getContentAsString()));
    }

}
