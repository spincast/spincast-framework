package org.spincast.website.repositories;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.spincast.core.templating.ITemplatingEngine;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.shaded.org.apache.commons.io.IOUtils;
import org.spincast.shaded.org.apache.commons.lang3.StringUtils;
import org.spincast.website.IAppConfig;
import org.spincast.website.models.INewsEntriesAndTotalNbr;
import org.spincast.website.models.INewsEntry;
import org.spincast.website.models.NewsEntry;

import com.google.common.collect.Lists;
import com.google.inject.Inject;

/**
 * Implementation of the News repository that takes the 
 * news entries from template files.
 */
public class TemplateFilesRepository implements INewsRepository {

    private final IAppConfig appConfig;
    private final ITemplatingEngine templatingEngine;

    private final Object getNewsEntriesLocalLock = new Object();

    private List<INewsEntry> newsEntries;
    private List<INewsEntry> newsEntriesAsc;
    private List<INewsEntry> newsEntriesDesc;
    private Map<Long, INewsEntry> newsEntriesById;

    /**
     * Constructor
     */
    @Inject
    public TemplateFilesRepository(IAppConfig appConfig,
                                   ITemplatingEngine templatingEngine) {
        this.appConfig = appConfig;
        this.templatingEngine = templatingEngine;
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

    protected IAppConfig getAppConfig() {
        return this.appConfig;
    }

    protected ITemplatingEngine getTemplatingEngine() {
        return this.templatingEngine;
    }

    @Override
    public int getNewsEntriesTotalNumber() {
        return getNewsEntriesLocal().size();
    }

    @Override
    public List<INewsEntry> getNewsEntries(boolean ascOrder) {

        if(ascOrder) {
            if(this.newsEntriesAsc == null) {

                List<INewsEntry> entries = getNewsEntriesLocal();

                Collections.sort(entries, new Comparator<INewsEntry>() {

                    @Override
                    public int compare(INewsEntry entry1, INewsEntry entry2) {
                        return entry1.getPublishedDate().compareTo(entry2.getPublishedDate());
                    }
                });

                this.newsEntriesAsc = entries;
            }
            return this.newsEntriesAsc;

        } else {
            if(this.newsEntriesDesc == null) {
                List<INewsEntry> newsEntriesAsc = getNewsEntries(true);
                this.newsEntriesDesc = Lists.reverse(newsEntriesAsc);
            }
            return this.newsEntriesDesc;
        }
    }

    @Override
    public List<INewsEntry> getNewsEntries(int startPos, int endPos, boolean ascOrder) {
        return getNewsEntriesAndTotalNbr(startPos, endPos, ascOrder).getNewsEntries();
    }

    @Override
    public INewsEntriesAndTotalNbr getNewsEntriesAndTotalNbr(int startPos, int endPos, boolean ascOrder) {

        if(startPos < 1) {
            startPos = 1;
        }

        if(endPos < 1) {
            endPos = 1;
        }

        if(endPos < startPos) {
            endPos = startPos;
        }

        final List<INewsEntry> entries = getNewsEntries(ascOrder);

        if(entries.size() == 0 || startPos > entries.size()) {

            return new INewsEntriesAndTotalNbr() {

                @Override
                public List<INewsEntry> getNewsEntries() {
                    return new ArrayList<INewsEntry>();
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
        return new INewsEntriesAndTotalNbr() {

            @Override
            public List<INewsEntry> getNewsEntries() {
                return Collections.unmodifiableList(entries.subList(startPosFinal - 1, endPosFinal)); // endPos is exclusive here
            }

            @Override
            public int getNbrNewsEntriesTotal() {
                return entries.size();
            }
        };
    }

    @Override
    public INewsEntry getNewsEntry(long newsId) {
        return getNewsEntriesById().get(newsId);
    }

    protected Map<Long, INewsEntry> getNewsEntriesById() {

        if(this.newsEntriesById == null) {
            this.newsEntriesById = new HashMap<Long, INewsEntry>();
            List<INewsEntry> newsEntries = getNewsEntriesLocal();
            for(INewsEntry newsEntry : newsEntries) {
                this.newsEntriesById.put(newsEntry.getId(), newsEntry);
            }
        }

        return this.newsEntriesById;
    }

    protected List<INewsEntry> getNewsEntriesLocal() {

        if(this.newsEntries == null) {
            synchronized(this.getNewsEntriesLocalLock) {
                if(this.newsEntries == null) {

                    try {
                        List<INewsEntry> newsEntries = new ArrayList<INewsEntry>();

                        ClassLoader cl = this.getClass().getClassLoader();
                        InputStream in = null;
                        BufferedReader br = null;
                        try {
                            in = cl.getResourceAsStream("news/");
                            br = new BufferedReader(new InputStreamReader(in));
                            String fileName;
                            while((fileName = br.readLine()) != null) {

                                if(!fileName.endsWith(".html")) {
                                    continue;
                                }

                                InputStream stream = cl.getResourceAsStream("news/" + fileName);
                                try {
                                    StringWriter writer = new StringWriter();
                                    IOUtils.copy(stream, writer, "UTF-8");
                                    String newsEntryContent = writer.toString();

                                    Properties metaProps = getMetaProperties(newsEntryContent);

                                    String idStr = metaProps.getProperty("id");
                                    long id = Long.parseLong(idStr);
                                    String dateStr = metaProps.getProperty("dateUTC");
                                    String title = metaProps.getProperty("title");

                                    newsEntryContent = cleanContent(newsEntryContent);

                                    INewsEntry entry = new NewsEntry(id, dateStr, title, newsEntryContent);
                                    newsEntries.add(entry);
                                } finally {
                                    IOUtils.closeQuietly(stream);
                                }
                            }

                            this.newsEntries = newsEntries;
                        } finally {
                            IOUtils.closeQuietly(in);
                            IOUtils.closeQuietly(br);
                        }
                    } catch(Exception ex) {
                        throw SpincastStatics.runtimize(ex);
                    }
                }
            }
        }

        return this.newsEntries;
    }

    protected Properties getMetaProperties(String newsEntryContent) {

        try {
            Matcher matcher = Pattern.compile("(?s)<!-- META_START(.*)META_END -->").matcher(newsEntryContent);
            matcher.find();
            String propsStr = StringUtils.strip(matcher.group(1), " \t\r\n");

            Properties props = new Properties();
            props.load(new StringReader(propsStr));

            return props;

        } catch(Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    protected String cleanContent(String newsEntryContent) {

        //==========================================
        // Removes the meta informations
        //==========================================
        newsEntryContent = newsEntryContent.replaceAll("(?s)<!-- META_START.*META_END -->", "");
        newsEntryContent = StringUtils.strip(newsEntryContent, "\r\n");

        //==========================================
        // Replaces some placeholders
        //==========================================
        String appUrlPrefix = getAppConfig().getServerSchemeHostPort();
        newsEntryContent = getTemplatingEngine().evaluate(newsEntryContent, SpincastStatics.params("appUrlPrefix", appUrlPrefix));

        return newsEntryContent;
    }

}
