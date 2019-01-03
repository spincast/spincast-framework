package org.spincast.plugins.dateformatter;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.spincast.core.utils.SpincastStatics;
import org.spincast.shaded.org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.mitchellbosecke.pebble.extension.AbstractExtension;
import com.mitchellbosecke.pebble.extension.Filter;
import com.mitchellbosecke.pebble.extension.core.DefaultFilter;


public class SpincastDateFormatterPebbleExtensionDefault extends AbstractExtension
                                                         implements SpincastDateFormatterPebbleExtension {

    public static final String FILTER_NAME_DATE_FORMAT = "dateFormat";

    private final DateFormatterFactory dateFormatterFactory;

    @Inject
    public SpincastDateFormatterPebbleExtensionDefault(DateFormatterFactory dateFormatterFactory) {
        this.dateFormatterFactory = dateFormatterFactory;
    }

    protected DateFormatterFactory getDateFormatterFactory() {
        return this.dateFormatterFactory;
    }

    @Override
    public Map<String, Filter> getFilters() {

        Map<String, Filter> filters = new HashMap<String, Filter>();

        Filter filter = getDateFormatFilter();
        if (filter != null) {
            filters.put(getDateFormatFilterName(), filter);
        }

        return filters;
    }

    protected String getDateFormatFilterName() {
        return FILTER_NAME_DATE_FORMAT;
    }

    protected Filter getDateFormatFilter() {

        Filter filter = new DefaultFilter() {

            @Override
            public List<String> getArgumentNames() {
                return Lists.newArrayList("firstParam",
                                          "secondParam",
                                          "thirdParam");
            }

            @Override
            public Object apply(Object dateObj, Map<String, Object> args) {

                if (dateObj != null) {

                    try {

                        Object firstParam = args.get("firstParam");

                        //==========================================
                        // "Relative" formatter
                        //==========================================
                        if (firstParam != null &&
                            firstParam.toString().toLowerCase().equals("relative")) {
                            return formatUsingRelativeFormatter(dateObj, args);
                        }

                        //==========================================
                        // Standard formatter
                        //==========================================
                        return formatUsingStandardFormatter(dateObj, args);

                    } catch (Exception ex) {
                        return dateObj.toString();
                    }
                } else {
                    return "";
                }
            }
        };

        return filter;
    }

    protected Object formatUsingRelativeFormatter(Object dateObj, Map<String, Object> args) {

        RelativeDateFormatType relativeDateFormatType = null;

        Object relativeDateFormatTypeObj = args.get("secondParam");
        if (relativeDateFormatTypeObj != null) {
            try {
                String type = relativeDateFormatTypeObj.toString().toUpperCase();
                relativeDateFormatType = RelativeDateFormatType.valueOf(type);
            } catch (Exception ex) {
                // ok
            }
        }

        //==========================================
        // May already be a Date or Instant objects.
        //==========================================
        RelativeDateFormatter formatter;
        if (dateObj instanceof Date) {
            formatter = getDateFormatterFactory().createRelativeFormatter((Date)dateObj);
        } else if (dateObj instanceof Instant) {
            formatter = getDateFormatterFactory().createRelativeFormatter((Instant)dateObj);
        } else {
            Date date = SpincastStatics.parseISO8601date(dateObj.toString());
            formatter = getDateFormatterFactory().createRelativeFormatter(date);
        }

        if (relativeDateFormatType != null) {
            formatter.formatType(relativeDateFormatType);
        }

        String result = formatter.format();
        return result;
    }

    protected String formatUsingStandardFormatter(Object dateObj, Map<String, Object> args) {

        //==========================================
        // May already be a Date or Instant objects.
        //==========================================
        DateFormatter formatter;
        if (dateObj instanceof Date) {
            formatter = getDateFormatterFactory().createFormatter((Date)dateObj);
        } else if (dateObj instanceof Instant) {
            formatter = getDateFormatterFactory().createFormatter((Instant)dateObj);
        } else {
            Date date = SpincastStatics.parseISO8601date(dateObj.toString());
            formatter = getDateFormatterFactory().createFormatter(date);
        }

        //==========================================
        // Date part
        //==========================================
        boolean hasDatePart = false;
        Object datePatternNameOrPatternObj = args.get("firstParam");
        if (datePatternNameOrPatternObj != null) {
            String datePatternNameOrPattern = datePatternNameOrPatternObj.toString();
            if (!StringUtils.isBlank(datePatternNameOrPattern)) {
                hasDatePart = true;

                //==========================================
                // "_" means : use the default
                //==========================================
                if (!datePatternNameOrPattern.equals("_")) {
                    datePatternNameOrPattern = datePatternNameOrPattern.toUpperCase();

                    DatePattern datePattern = null;
                    try {
                        datePattern = DatePattern.valueOf(datePatternNameOrPattern);
                    } catch (Exception ex) {
                        // ok
                    }

                    if (datePattern != null) {
                        formatter.datePattern(datePattern);
                    } else {
                        formatter.datePattern(datePatternNameOrPattern);
                    }
                }
            }
        }

        //==========================================
        // Time part
        //==========================================
        boolean hasTimePart = false;
        Object timePatternNameOrPatternObj = args.get("secondParam");
        if (timePatternNameOrPatternObj != null) {
            String timePatternNameOrPattern = timePatternNameOrPatternObj.toString();
            if (!StringUtils.isBlank(timePatternNameOrPattern)) {
                hasTimePart = true;

                //==========================================
                // "_" means : use the default
                //==========================================
                if (!timePatternNameOrPattern.equals("_")) {
                    timePatternNameOrPattern = timePatternNameOrPattern.toUpperCase();

                    DatePattern timePattern = null;
                    try {
                        timePattern = DatePattern.valueOf(timePatternNameOrPattern);
                    } catch (Exception ex) {
                        // ok
                    }

                    if (timePattern != null) {
                        formatter.timePattern(timePattern);
                    } else {
                        formatter.timePattern(timePatternNameOrPattern);
                    }
                }
            }
        }

        //==========================================
        // Separator
        //==========================================
        Object separatorObj = args.get("thirdParam");
        if (separatorObj != null) {
            String separator = separatorObj.toString();
            formatter.separator(separator);
        }

        //==========================================
        // Parts
        //==========================================
        if (hasDatePart && !hasTimePart) {
            formatter.parts(DateParts.DATE);
        } else if (!hasDatePart && hasTimePart) {
            formatter.parts(DateParts.TIME);
        } else {
            formatter.parts(DateParts.BOTH);
        }

        String result = formatter.format();
        return result;
    }

}
