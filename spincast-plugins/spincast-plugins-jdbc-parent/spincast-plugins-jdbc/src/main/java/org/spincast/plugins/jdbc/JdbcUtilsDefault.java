package org.spincast.plugins.jdbc;

import com.google.inject.Inject;

public class JdbcUtilsDefault implements JdbcUtils {

    private final JdbcScope jdbcScope;
    private final JdbcStatementFactory jdbcStatementFactory;

    @Inject
    public JdbcUtilsDefault(JdbcScope jdbcScope,
                            JdbcStatementFactory jdbcStatementFactory) {
        this.jdbcScope = jdbcScope;
        this.jdbcStatementFactory = jdbcStatementFactory;
    }

    @Override
    public JdbcStatementFactory statements() {
        return this.jdbcStatementFactory;
    }

    @Override
    public JdbcScope scopes() {
        return this.jdbcScope;
    }

}
