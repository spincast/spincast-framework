package org.spincast.website.pebble;

import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.spincast.core.utils.SpincastStatics;

import com.mitchellbosecke.pebble.extension.AbstractExtension;
import com.mitchellbosecke.pebble.extension.Filter;

/**
 * Pebble filter to remove the HTML tags.
 */
public class RemoveHtmlTagsPebbleFilter extends AbstractExtension {

    public static final String REMOVE_HTML_TAGS_FILTER_NAME = "strip";

    @Override
    public Map<String, Filter> getFilters() {

        Filter filter = new Filter() {

            @Override
            public List<String> getArgumentNames() {
                return null;
            }

            @Override
            public Object apply(Object input, Map<String, Object> args) {
                if(input == null) {
                    return null;
                }
                String str = (String)input;

                return Jsoup.parse(str).text();
            }
        };

        return SpincastStatics.map(REMOVE_HTML_TAGS_FILTER_NAME, filter);
    }

}
