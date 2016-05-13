package org.spincast.website.models;

import java.util.ArrayList;
import java.util.List;

import org.spincast.website.IAppConfig;

import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * Spincast news entries are currently hardcoded here.
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
            entry = new NewsEntry("2016-05-13",
                                  "Spincast news page and feed",
                                  "<p>A new <a href=\"" + appUrlPrefix + "/news\">What's new?</a> page is now online!</p>" +
                                  "<p>Each time a new plugin is available, or each time an interesting thing happens in " +
                                  "Spincast world, this page is going to be updated.</p>" +
                                  "<p>You can also access those news using the <a href=\"" + appUrlPrefix + "/rss\">RSS feed</a>.</p>");
            
            this.newsEntries.add(entry);
            
            entry = null;
            entry = new NewsEntry("2016-05-10",
                                  "New plugin available: Spincast Validation",
                                  "<p>A new plugin is available: <a href=\"" + appUrlPrefix + "/plugins/spincast-validation\"><em>Spincast Validation</em></a>.</p>" +
                                  
                                  "<p>This plugin provides a pattern and some classes to help validate your beans/models. " +
                                  "Have a look at the <a href=\"" + appUrlPrefix + "/plugins/spincast-validation#usage\">Usage</a> section for a quick example!</p>");
            
            this.newsEntries.add(entry);
            
            entry = new NewsEntry("2016-05-08",
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
