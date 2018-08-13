package org.spincast.plugins.dateformatter;

import java.time.Instant;
import java.util.Date;

public interface DateFormatterFactory {

    public DateFormatter createFormatter(Date date);

    public DateFormatter createFormatter(Instant instant);

    public RelativeDateFormatter createRelativeFormatter(Date date);

    public RelativeDateFormatter createRelativeFormatter(Instant instant);

}
