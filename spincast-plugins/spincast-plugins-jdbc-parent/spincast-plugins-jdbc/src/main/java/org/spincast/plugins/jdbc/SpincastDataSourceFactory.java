package org.spincast.plugins.jdbc;

import javax.sql.DataSource;

public interface SpincastDataSourceFactory {

    public SpincastDataSource create(DataSource dataSource);
}
