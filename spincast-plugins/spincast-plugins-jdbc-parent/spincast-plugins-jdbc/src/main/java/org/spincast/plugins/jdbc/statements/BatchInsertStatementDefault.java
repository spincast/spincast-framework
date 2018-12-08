package org.spincast.plugins.jdbc.statements;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.utils.SpincastStatics;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

public class BatchInsertStatementDefault extends StatementBase implements BatchInsertStatement {

    protected final static Logger logger = LoggerFactory.getLogger(BatchInsertStatementDefault.class);

    private final List<Map<String, Object>> batchParams = new ArrayList<Map<String, Object>>();


    @AssistedInject
    public BatchInsertStatementDefault(@Assisted Connection connection) {
        super(connection);
    }

    protected List<Map<String, Object>> getBatchParams() {
        return this.batchParams;
    }

    protected void addBatchParams(Map<String, Object> params) {
        getBatchParams().add(params);
    }

    @Override
    public void addBatch() {
        addBatchParams(getParams());
        clearParams();
    }

    protected int getBatchInsertSize() {
        return getBatchParams().size();
    }

    @Override
    public int[] batchInsert() {
        Connection connection = getConnection();
        try {
            PreparedStatement realStatement = connection.prepareStatement(getParsedQuery());

            try {
                for (Map<String, Object> params : getBatchParams()) {
                    addParamsToStatement(realStatement, params);
                    realStatement.addBatch();
                }

                int[] queryResults = realStatement.executeBatch();
                return queryResults;
            } finally {
                close(realStatement);
            }
        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    @Override
    public List<Long> batchInsert(String primaryKeyName) {
        Connection connection = getConnection();
        try {
            PreparedStatement realStatement = connection.prepareStatement(getParsedQuery(), new String[]{primaryKeyName});

            try {
                for (Map<String, Object> params : getBatchParams()) {
                    addParamsToStatement(realStatement, params);
                    realStatement.addBatch();
                }

                int[] queryResults = realStatement.executeBatch();

                List<Long> generatedKeys = new ArrayList<Long>();
                ResultSet resultSet = realStatement.getGeneratedKeys();
                if (resultSet != null && resultSet.next()) {
                    int pos = 0;
                    do {
                        generatedKeys.add(resultSet.getLong(1));
                        pos++;
                    } while (resultSet.next());

                    if (pos != queryResults.length) {
                        throw new RuntimeException("Unable to get all the generated ids!");
                    }
                } else {
                    throw new RuntimeException("Unable to get the generated ids!");
                }

                return generatedKeys;
            } finally {
                close(realStatement);
            }
        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

}
