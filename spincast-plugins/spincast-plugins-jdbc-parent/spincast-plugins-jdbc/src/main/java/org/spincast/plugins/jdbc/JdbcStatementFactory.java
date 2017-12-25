package org.spincast.plugins.jdbc;


import java.sql.Connection;

import org.spincast.plugins.jdbc.statements.BatchInsertStatement;
import org.spincast.plugins.jdbc.statements.DeleteStatement;
import org.spincast.plugins.jdbc.statements.InsertStatement;
import org.spincast.plugins.jdbc.statements.SelectStatement;
import org.spincast.plugins.jdbc.statements.UpdateStatement;

public interface JdbcStatementFactory {

    /**
     * Creates a Select statement.
     */
    public SelectStatement createSelectStatement(Connection connection);

    /**
     * Creates an Insert statement.
     */
    public InsertStatement createInsertStatement(Connection connection);

    /**
     * Creates an Batch Insert statement.
     */
    public BatchInsertStatement createBatchInsertStatement(Connection connection);

    /**
     * Creates an Update statement.
     */
    public UpdateStatement createUpdateStatement(Connection connection);

    /**
     * Creates an Delete statement.
     */
    public DeleteStatement createDeleteStatement(Connection connection);

}
