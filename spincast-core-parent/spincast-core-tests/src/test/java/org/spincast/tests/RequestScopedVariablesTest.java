package org.spincast.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import org.junit.Test;
import org.spincast.core.exchange.DefaultRequestContext;
import org.spincast.core.routing.Handler;
import org.spincast.core.routing.Router;
import org.spincast.core.websocket.DefaultWebsocketContext;
import org.spincast.defaults.tests.SpincastDefaultNoAppIntegrationTestBase;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.shaded.org.apache.http.HttpStatus;

import com.google.inject.Key;
import com.google.inject.TypeLiteral;

public class RequestScopedVariablesTest extends SpincastDefaultNoAppIntegrationTestBase {

    @Test
    public void variables() throws Exception {

        Router<DefaultRequestContext, DefaultWebsocketContext> router = getRouter();

        router.GET("/one").pos(-1).save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                Map<String, Object> variables = context.variables().getAll();
                assertNotNull(variables);
                int nbr = variables.size();

                context.variables().add("key1", "val1");

                assertEquals("val1", context.variables().get("key1"));

                assertEquals(nbr + 1, context.variables().getAll().size());

            }
        });
        router.GET("/one")

              .before(new Handler<DefaultRequestContext>() {

                  @Override
                  public void handle(DefaultRequestContext context) {
                      Map<String, Object> variables = context.variables().getAll();
                      assertNotNull(variables);
                      int nbr = variables.size();
                      assertEquals("val1", context.variables().get("key1"));

                      Map<String, Object> vars = new HashMap<String, Object>();
                      vars.put("key2", "val2");
                      vars.put("key3", "val3");

                      context.variables().add(vars);

                      variables = context.variables().getAll();
                      assertEquals(nbr + 2, variables.size());
                      assertEquals("val1", context.variables().get("key1"));
                      assertEquals("val2", context.variables().get("key2"));
                      assertEquals("val3", context.variables().get("key3"));
                  }
              })
              .after(new Handler<DefaultRequestContext>() {

                  @Override
                  public void handle(DefaultRequestContext context) {
                      Map<String, Object> variables = context.variables().getAll();
                      assertNotNull(variables);
                      int nbr = variables.size();
                      assertEquals("val4", context.variables().get("key4"));

                      Map<String, Object> vars = new HashMap<String, Object>();
                      vars.put("key2", "val2");
                      vars.put("key3", "val3");

                      context.variables().add(vars);

                      variables = context.variables().getAll();
                      assertEquals(nbr + 2, variables.size());
                      assertEquals("val4", context.variables().get("key4"));
                      assertEquals("val2", context.variables().get("key2"));
                      assertEquals("val3", context.variables().get("key3"));
                  }
              })

              .save(new Handler<DefaultRequestContext>() {

                  @Override
                  public void handle(DefaultRequestContext context) {
                      Map<String, Object> variables = context.variables().getAll();
                      assertNotNull(variables);
                      int nbr = variables.size();

                      assertEquals("val1", context.variables().get("key1"));
                      assertEquals("val2", context.variables().get("key2"));
                      assertEquals("val3", context.variables().get("key3"));

                      context.variables().remove("key1");
                      context.variables().remove("key2");
                      context.variables().remove("key3");

                      assertEquals(nbr - 3, context.variables().getAll().size());

                      context.variables().add("key4", "val4");
                  }
              });

        router.GET("/one").pos(1).save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                Map<String, Object> variables = context.variables().getAll();
                assertNotNull(variables);
                int nbr = variables.size();

                assertEquals("val4", context.variables().get("key4"));
                assertEquals("val2", context.variables().get("key2"));
                assertEquals("val3", context.variables().get("key3"));

                context.variables().remove("key2");
                variables = context.variables().getAll();
                assertEquals(nbr - 1, variables.size());
                assertEquals("val4", context.variables().get("key4"));
                assertEquals("val3", context.variables().get("key3"));
            }
        });

        HttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void classAndKey() throws Exception {

        getRouter().GET("/").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                context.variables().add("asClass", new Date());

                Date date = context.variables().get("asClass", Date.class);
                assertNotNull(date);

                try {
                    @SuppressWarnings("unused")
                    TimeZone nope = context.variables().get("asClass", TimeZone.class);
                    fail();
                } catch(Exception ex) {
                }

                context.variables().add("asKey", new ArrayList<Date>());

                Key<ArrayList<Date>> key = Key.get(new TypeLiteral<ArrayList<Date>>() {});
                ArrayList<Date> dates = context.variables().get("asKey", key);
                assertNotNull(dates);

                Key<HashMap<String, Date>> key2 = Key.get(new TypeLiteral<HashMap<String, Date>>() {});
                try {
                    @SuppressWarnings("unused")
                    HashMap<String, Date> nope = context.variables().get("asKey", key2);
                    fail();
                } catch(Exception ex) {
                }
            }
        });

        HttpResponse response = GET("/").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

}
