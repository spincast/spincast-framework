package org.spincast.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;
import org.spincast.core.exchange.DefaultRequestContext;
import org.spincast.core.guice.MainArgs;
import org.spincast.core.routing.Handler;
import org.spincast.defaults.bootstrapping.Spincast;
import org.spincast.defaults.testing.IntegrationTestNoAppDefaultContextsBase;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.shaded.org.apache.http.HttpStatus;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

public class MainArgsTest extends IntegrationTestNoAppDefaultContextsBase {

    @Override
    protected Injector createInjector() {

        return Spincast.configure()
                       .init(this.args);
    }

    protected String[] args = new String[]{"one", "two"};

    @Inject
    @MainArgs
    protected String[] mainArgsArray;

    @Inject
    @MainArgs
    protected List<String> mainArgsList;

    @Test
    public void mainArgsTest() throws Exception {

        assertNotNull(this.mainArgsArray);
        assertEquals(2, this.mainArgsArray.length);
        assertEquals("one", this.mainArgsArray[0]);
        assertEquals("two", this.mainArgsArray[1]);

        assertNotNull(this.mainArgsList);
        assertEquals(2, this.mainArgsList.size());
        assertEquals("one", this.mainArgsList.get(0));
        assertEquals("two", this.mainArgsList.get(1));

        getRouter().GET("/one").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String[] mainArgsArray = context.guice()
                                                .getInstance(Key.get(new TypeLiteral<String[]>() {}, MainArgs.class));
                assertNotNull(mainArgsArray);
                assertEquals(2, mainArgsArray.length);
                assertEquals("one", mainArgsArray[0]);
                assertEquals("two", mainArgsArray[1]);

                List<String> mainArgsList = context.guice().getInstance(Key.get(new TypeLiteral<List<String>>() {},
                                                                                MainArgs.class));
                assertNotNull(mainArgsList);
                assertEquals(2, mainArgsList.size());
                assertEquals("one", mainArgsList.get(0));
                assertEquals("two", mainArgsList.get(1));
            }
        });

        HttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

}
