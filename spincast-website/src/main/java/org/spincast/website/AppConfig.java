package org.spincast.website;

import java.util.List;

import org.spincast.core.config.SpincastConfig;
import org.spincast.shaded.org.apache.commons.lang3.tuple.Pair;

/**
 * Application configurations.
 */
public interface AppConfig extends SpincastConfig {

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
