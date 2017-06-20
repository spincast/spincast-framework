package org.spincast.website.repositories;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.spincast.core.json.JsonArray;
import org.spincast.core.json.JsonManager;
import org.spincast.core.json.JsonObject;
import org.spincast.core.templating.TemplatingEngine;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.core.utils.SpincastUtils;
import org.spincast.website.AppConfig;
import org.spincast.website.models.NewsEntriesAndTotalNbr;
import org.spincast.website.models.NewsEntry;
import org.spincast.website.models.NewsEntryDefault;

import com.google.common.collect.Lists;
import com.google.inject.Inject;

/**
 * Implementation of the News repository that takes the 
 * news entries from template files.
 */
public class TemplateFilesRepository implements NewsRepository {

    private final AppConfig appConfig;
    private final TemplatingEngine templatingEngine;
    private final SpincastUtils spincastUtils;
    private final JsonManager jsonManager;

    private final Object getNewsEntriesLocalLock = new Object();

    private List<NewsEntry> newsEntries;
    private List<NewsEntry> newsEntriesAsc;
    private List<NewsEntry> newsEntriesDesc;
    private Map<Long, NewsEntry> newsEntriesById;

    /**
     * Constructor
     */
    @Inject
    public TemplateFilesRepository(AppConfig appConfig,
                                   TemplatingEngine templatingEngine,
                                   SpincastUtils spincastUtils,
                                   JsonManager jsonManager) {
        this.appConfig = appConfig;
        this.templatingEngine = templatingEngine;
        this.spincastUtils = spincastUtils;
        this.jsonManager = jsonManager;
    }

    /**
     * Init
     */
    @Inject
    public void init() {

        //==========================================
        // Load news entries
        //==========================================
        getNewsEntriesLocal();
    }

    protected AppConfig getAppConfig() {
        return this.appConfig;
    }

    protected TemplatingEngine getTemplatingEngine() {
        return this.templatingEngine;
    }

    protected SpincastUtils getSpincastUtils() {
        return this.spincastUtils;
    }

    protected JsonManager getJsonManager() {
        return this.jsonManager;
    }

    @Override
    public int getNewsEntriesTotalNumber() {
        return getNewsEntriesLocal().size();
    }

    @Override
    public List<NewsEntry> getNewsEntries(boolean ascOrder) {

        if(ascOrder) {
            if(this.newsEntriesAsc == null || getAppConfig().isDebugEnabled()) {

                List<NewsEntry> entries = getNewsEntriesLocal();

                Collections.sort(entries, new Comparator<NewsEntry>() {

                    @Override
                    public int compare(NewsEntry entry1, NewsEntry entry2) {
                        return entry1.getPublishedDate().compareTo(entry2.getPublishedDate());
                    }
                });

                this.newsEntriesAsc = entries;
            }
            return this.newsEntriesAsc;

        } else {
            if(this.newsEntriesDesc == null || getAppConfig().isDebugEnabled()) {
                List<NewsEntry> newsEntriesAsc = getNewsEntries(true);
                this.newsEntriesDesc = Lists.reverse(newsEntriesAsc);
            }
            return this.newsEntriesDesc;
        }
    }

    @Override
    public List<NewsEntry> getNewsEntries(int startPos, int endPos, boolean ascOrder) {
        return getNewsEntriesAndTotalNbr(startPos, endPos, ascOrder).getNewsEntries();
    }

    @Override
    public NewsEntriesAndTotalNbr getNewsEntriesAndTotalNbr(int startPos, int endPos, boolean ascOrder) {

        if(startPos < 1) {
            startPos = 1;
        }

        if(endPos < 1) {
            endPos = 1;
        }

        if(endPos < startPos) {
            endPos = startPos;
        }

        final List<NewsEntry> entries = getNewsEntries(ascOrder);

        if(entries.size() == 0 || startPos > entries.size()) {

            return new NewsEntriesAndTotalNbr() {

                @Override
                public List<NewsEntry> getNewsEntries() {
                    return new ArrayList<NewsEntry>();
                }

                @Override
                public int getNbrNewsEntriesTotal() {
                    return entries.size();
                }
            };
        }

        if(endPos > entries.size()) {
            endPos = entries.size();
        }

        final int startPosFinal = startPos;
        final int endPosFinal = endPos;
        return new NewsEntriesAndTotalNbr() {

            @Override
            public List<NewsEntry> getNewsEntries() {
                return Collections.unmodifiableList(entries.subList(startPosFinal - 1, endPosFinal)); // endPos is exclusive here
            }

            @Override
            public int getNbrNewsEntriesTotal() {
                return entries.size();
            }
        };
    }

    @Override
    public NewsEntry getNewsEntry(long newsId) {
        return getNewsEntriesById().get(newsId);
    }

    protected Map<Long, NewsEntry> getNewsEntriesById() {

        if(this.newsEntriesById == null || getAppConfig().isDebugEnabled()) {
            this.newsEntriesById = new HashMap<Long, NewsEntry>();
            List<NewsEntry> newsEntries = getNewsEntriesLocal();
            for(NewsEntry newsEntry : newsEntries) {
                this.newsEntriesById.put(newsEntry.getId(), newsEntry);
            }
        }

        return this.newsEntriesById;
    }

    protected List<NewsEntry> getNewsEntriesLocal() {

        if(this.newsEntries == null || getAppConfig().isDebugEnabled()) {
            synchronized(this.getNewsEntriesLocalLock) {
                if(this.newsEntries == null || getAppConfig().isDebugEnabled()) {

                    try {

                        List<NewsEntry> newsEntries = new ArrayList<NewsEntry>();

                        JsonObject newsJsonObj = getJsonManager().fromClasspathFile("/news/news-index.json");

                        Map<String, Object> params = new HashMap<String, Object>();
                        params.put("appUrlPrefix", getAppConfig().getPublicUrlBase());

                        JsonArray newsArray = newsJsonObj.getJsonArray("news");
                        for(int i = 0; i < newsArray.size(); i++) {
                            JsonObject newsObj = newsArray.getJsonObject(i);

                            String path = newsObj.getString("path");
                            String newsEntryContent = getSpincastUtils().readClasspathFile(path);
                            if(newsEntryContent == null) {
                                throw new RuntimeException("News not found : " + path);
                            }

                            //==========================================
                            // Replace placeholders
                            //==========================================
                            newsEntryContent = getTemplatingEngine().evaluate(newsEntryContent, params);

                            long id = newsObj.getLong("id");
                            Date date = newsObj.getDate("date");
                            String title = newsObj.getString("title");

                            NewsEntry entry = new NewsEntryDefault(id, date, title, newsEntryContent);
                            newsEntries.add(entry);
                        }

                        this.newsEntries = newsEntries;

                    } catch(Exception ex) {
                        throw SpincastStatics.runtimize(ex);
                    }
                }
            }
        }

        return this.newsEntries;
    }

}
