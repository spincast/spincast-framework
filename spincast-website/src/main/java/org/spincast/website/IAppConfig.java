package org.spincast.website;

import java.util.List;

import org.spincast.core.config.ISpincastConfig;
import org.spincast.shaded.org.apache.commons.lang3.tuple.Pair;

public interface IAppConfig extends ISpincastConfig {

    /**
     * The application can't know for sure by itself on which
     * scheme/host/port it is served (for example : "https://www.example.com:8080").
     * We have to specify it in the configurations to be able to
     * build absolute URLs.
     */
    public String getServerSchemeHostPort();

    /**
     * The number of news entries on a regular "News" listing page.
     */
    public int getNbrNewsEntriesOnNewsPage();

    /**
     * The number of news entries for a feed request.
     */
    public int getNbrNewsEntriesPerFeedRequest();

    /**
     * A list of username/password that allow administrative
     * privileges.
     */
    public List<Pair<String, String>> getAdminUsernamesPasswords();

}
