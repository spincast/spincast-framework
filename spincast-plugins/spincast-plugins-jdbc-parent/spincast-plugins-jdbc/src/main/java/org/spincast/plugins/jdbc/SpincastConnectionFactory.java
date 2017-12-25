package org.spincast.plugins.jdbc;

import java.sql.Connection;

public interface SpincastConnectionFactory {

    SpincastConnection create(Connection originalConnection);
}
