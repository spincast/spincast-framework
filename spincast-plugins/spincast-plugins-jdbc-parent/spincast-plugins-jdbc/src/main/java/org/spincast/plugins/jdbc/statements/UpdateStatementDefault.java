package org.spincast.plugins.jdbc.statements;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.spincast.core.utils.SpincastStatics;
import org.spincast.plugins.jdbc.SpincastResultSetDefault;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

public class UpdateStatementDefault extends StatementBase implements UpdateStatement {

    @AssistedInject
    public UpdateStatementDefault(@Assisted Connection connection) {
        super(connection);
    }

    protected PreparedStatement getStatementWithParamsAdded(Connection connection) {
        try {
            PreparedStatement statement = connection.prepareStatement(getParsedQuery());

            addCurrentParamsToStatement(statement);

            return statement;
        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    @Override
    public int update() {
        Connection connection = getConnection();
        try {
            PreparedStatement realStatement = getStatementWithParamsAdded(connection);
            try {
                int affectedRows = realStatement.executeUpdate();
                return affectedRows;
            } finally {
                close(realStatement);
            }
        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    @Override
    public <T> List<T> update(ResultSetHandler<T> resultSetHandler) {
        Connection connection = getConnection();
        try {
            PreparedStatement realStatement = getStatementWithParamsAdded(connection);
            try {
                ResultSet resultSet = realStatement.executeQuery();
                try {
                    List<T> items = new ArrayList<>();

                    if (resultSetHandler != null && resultSet.isBeforeFirst()) {
                        while (resultSet.next()) {
                            T item = resultSetHandler.handle(new SpincastResultSetDefault(resultSet));
                            items.add(item);
                        }
                    }
                    return items;
                } finally {
                    close(resultSet);
                }
            } finally {
                close(realStatement);
            }
        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    @Override
    public void setCase(String paramName, String columnNameToCheck, Map<?, ?> map) {

        StringBuilder caseQuery = new StringBuilder();

        if (map == null || map.size() == 0) {
            caseQuery.append(" null ");
        } else {


            caseQuery.append(" CASE ");

            for (Entry<?, ?> entry : map.entrySet()) {

                String caseParamName = createUniqueParamName();
                caseQuery.append("WHEN ").append(columnNameToCheck).append("=").append(":").append(caseParamName).append(" ");
                addParam(caseParamName, entry.getKey());

                caseParamName = createUniqueParamName();
                caseQuery.append(" THEN ").append(":").append(caseParamName).append(" ");
                addParam(caseParamName, entry.getValue());
            }

            caseQuery.append(" END ");
        }

        getStaticTokens().put(paramName, caseQuery.toString());
    }

    protected String createUniqueParamName() {
        return "x" + UUID.randomUUID().toString().replace("-", "_") + "x";
    }


}
