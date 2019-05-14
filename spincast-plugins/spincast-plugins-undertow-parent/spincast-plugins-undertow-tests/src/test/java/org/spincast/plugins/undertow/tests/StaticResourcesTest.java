package org.spincast.plugins.undertow.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.spincast.core.routing.StaticResourceType;
import org.spincast.core.server.Server;
import org.spincast.testing.defaults.NoAppStartHttpServerTestingBase;

import com.google.inject.Inject;

public class StaticResourcesTest extends NoAppStartHttpServerTestingBase {

    @Inject
    protected Server server;

    @Test
    public void addRemove() throws Exception {

        getRouter().file("/a").classpath("/spincast/plugins/undertow/a.txt").handle();
        getRouter().file("/b").classpath("/spincast/plugins/undertow/b.txt").handle();

        assertEquals(2, this.server.getStaticResourcesServed().size());

        this.server.removeAllStaticResourcesServed();
        assertEquals(0, this.server.getStaticResourcesServed().size());

        getRouter().file("/a").classpath("/spincast/plugins/undertow/a.txt").handle();
        getRouter().file("/b").classpath("/spincast/plugins/undertow/b.txt").handle();
        assertEquals(2, this.server.getStaticResourcesServed().size());

        this.server.removeStaticResourcesServed(StaticResourceType.FILE_FROM_CLASSPATH, "/b");
        assertEquals(1, this.server.getStaticResourcesServed().size());

    }

}
