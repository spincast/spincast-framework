package org.spincast.website.models;

import java.util.ArrayList;
import java.util.List;

import org.spincast.website.IAppConfig;

import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * Spincast news entries are currently hardcoded here.
 * Dates are UTC.
 */
public class NewsEntriesProvider implements Provider<List<INewsEntry>> {

    private final IAppConfig appConfig;
    private List<INewsEntry> newsEntries;

    @Inject
    public NewsEntriesProvider(IAppConfig appConfig) {
        this.appConfig = appConfig;
    }

    protected IAppConfig getAppConfig() {
        return this.appConfig;
    }

    @Override
    public List<INewsEntry> get() {

        // @formatter:off
        if(this.newsEntries == null) {
            this.newsEntries = new ArrayList<INewsEntry>();

            String appUrlPrefix = getAppConfig().getServerSchemeHostPort();

            INewsEntry entry = null;
            entry = new NewsEntry("2016-05-17 24:00",
                                  "Spincast is now used to build itself!",
                                  "<p>Spincast is now used to build itself! <img src=\"" + appUrlPrefix + "/public/images/crazy.png\" /></p>" +
                                  "<p>How is this possible? Well, if you don't know yet, a Maven build process " +
                                  "contains multiple <i>phases</i>, one of them is <i>compile</i> and this " +
                                  "is where Spincast is actually compiled. But there are many other important phases following that one, and this is where the " +
                                  "compiled Spincast classes can be used!</p>" +
                                  "<p>In <code>spincast-website</code>'s <a href=\"https://github.com/spincast/spincast-framework/blob/master/spincast-website/pom.xml\">pom.xml</a>, " +
                                  "during the <code>prepare-package</code> phase, we declare <code>exec-maven-plugin</code> " +
                                  "plugins to dynamically call some Spincast classes. One of those classes is " +
                                  "<a href=\"https://github.com/spincast/spincast-framework/blob/master/spincast-website/src/main/java/org/spincast/website/maven/SpincastMavenPreparePackage.java\">SpincastMavenPreparePackage</a>, " +
                                  "for example. " +
                                  "Since Spincast is already compiled at this point, we can use it! In this particular case, we use Spincast to " +
                                  "create a modified version of the <i>Quick Start</i> " +
                                  "application, to zip it, and then to add it to the website's \"public\" folder, where it can be downloaded.</p>" +
                                  "<p>If you look at <code>SpincastMavenPreparePackage</code>'s code, you'll see that in its <code>main(...)</code> method " +
                                  "we start a Guice context using the " +
                                  "<code>SpincastDefaultGuiceModule</code> Spincast's Guice module, exactly as we could do for a real Spincast application! This " +
                                  "allows us to access all the functionalities, all the utilities provided by Spincast. For example, here we use a Spincast utility " +
                                  "to zip the <i>Quick Start</i> sources. Note that we don't " +
                                  "start a HTTP server though, since we don't need it!</p>" +
                                  "<p>In summary, instead of using a <code>maven-antrun-plugin</code> plugin for example, and having to deal with cumbersome " +
                                  "XML logic when some custom steps are required during a build, we simply start a Guice context and we have full access " +
                                  "to Spincast.</p>" +
                                  "<p>We will probably use that little trick in a reusable " +
                                  "<a href=\"https://maven.apache.org/guides/plugin/guide-java-plugin-development.html\">Maven Mojo plugin</a> one day, " +
                                  "but for build steps that are <i>specific</i> to a project, this approach works very well.</p>");
            
            this.newsEntries.add(entry);
            
            entry = null;
            entry = new NewsEntry("2016-05-16 23:00",
                                  "Javadoc now available online",
                                  "<p>The Spincast Javadoc is now <a href=\"" + appUrlPrefix + "/public/javadoc/\">available online</a>.</p>" +
                                  "<p>A link to that Javadoc has been added at the top of the <a href=\"" + appUrlPrefix + "/documentation/\">documentation</a> " +
                                  "page, and at the top of each plugin's page.</p>" +
                                  "<p>Look for thoses links: <img src=\"" + appUrlPrefix + "/public/images/javadoc-link.png\" /> !</p>");
            
            this.newsEntries.add(entry);
            
            entry = null;
            entry = new NewsEntry("2016-05-13 00:00",
                                  "Spincast news page and feed",
                                  "<p>A new <a href=\"" + appUrlPrefix + "/news\">What's new?</a> page is now online!</p>" +
                                  "<p>Each time a new plugin is available, or each time an interesting thing happens in " +
                                  "Spincast world, this page is going to be updated.</p>" +
                                  "<p>You can also access those news using the <a href=\"" + appUrlPrefix + "/rss\">RSS feed</a>.</p>");
            
            this.newsEntries.add(entry);
            
            entry = null;
            entry = new NewsEntry("2016-05-10 00:00",
                                  "New plugin available: Spincast Validation",
                                  "<p>A new plugin is available: <a href=\"" + appUrlPrefix + "/plugins/spincast-validation\"><em>Spincast Validation</em></a>.</p>" +
                                  
                                  "<p>This plugin provides a pattern and some classes to help validate your beans/models. " +
                                  "Have a look at the <a href=\"" + appUrlPrefix + "/plugins/spincast-validation#usage\">Usage</a> section for a quick example!</p>");
            
            this.newsEntries.add(entry);
            
            entry = new NewsEntry("2016-05-08 00:00",
                                  "Spincast is now listed on Todo-Backend (todobackend.com)",
                                  "<p>Spincast is now listed on <a href=\"http://todobackend.com/\">Todo-Backend</a> <em>(todobackend.com)</em>.</p>" +
                                  
                                  "<p>This first implementation simply saves the <code>todos</code> in memory. If you have suggestions " +
                                  "for another implementation, <a href=\"https://groups.google.com/forum/#!topic/spincast/3T5vuN-Lp1w\">let us know</a>!</p>" +
                                  
                                  "<p>We plan on developing one with <a href=\"https://www.docker.com/\">Docker</a> and " +
                                  "<a href=\"http://www.postgresql.org/\">PostgreSQL</a> soon.</p>");
            
            this.newsEntries.add(entry);
            
        }
        // @formatter:on

        return this.newsEntries;
    }

}
