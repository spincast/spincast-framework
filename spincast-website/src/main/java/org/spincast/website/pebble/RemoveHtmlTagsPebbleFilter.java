package org.spincast.website.pebble;

import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.spincast.core.utils.SpincastStatics;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.extension.AbstractExtension;
import com.mitchellbosecke.pebble.extension.Filter;
import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

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
            public Object apply(Object value,
                                Map<String, Object> args,
                                PebbleTemplate self,
                                EvaluationContext evaluationContext,
                                int lineNumber) throws PebbleException {
                if (value == null) {
                    return null;
                }
                String str = (String)value;

                return Jsoup.parse(str).text();
            }
        };

        return SpincastStatics.map(REMOVE_HTML_TAGS_FILTER_NAME, filter);
    }

}
