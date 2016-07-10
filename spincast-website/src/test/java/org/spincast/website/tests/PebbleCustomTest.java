package org.spincast.website.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.spincast.core.templating.ITemplatingEngine;
import org.spincast.core.utils.SpincastStatics;

import com.google.inject.Inject;

public class PebbleCustomTest extends WebsiteIntegrationTestBase {

    @Inject
    protected ITemplatingEngine templatingEngine;

    protected ITemplatingEngine getTemplatingEngine() {
        return this.templatingEngine;
    }

    @Test
    public void stripHtmlTagsFilter() throws Exception {

        String template = "<div>{{var | strip}}</div>";
        String var = "<h1>This <em>should not</em> contain any <code>HTML tags</code>!</h1>";

        String result = getTemplatingEngine().evaluate(template, SpincastStatics.params("var", var));
        assertEquals("<div>This should not contain any HTML tags!</div>", result);

    }

}