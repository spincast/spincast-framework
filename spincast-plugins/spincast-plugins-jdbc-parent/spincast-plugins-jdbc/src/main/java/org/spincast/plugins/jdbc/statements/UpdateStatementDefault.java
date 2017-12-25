package org.spincast.plugins.jdbc.statements;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.spincast.core.utils.SpincastStatics;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

public class UpdateStatementDefault extends StatementBase implements UpdateStatement {

    @AssistedInject
    public UpdateStatementDefault(@Assisted Connection connection,
                                  QueryResultFactory queryResultFactory) {
        super(connection,
              queryResultFactory);
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
    public QueryResult update() {
        Connection connection = getConnection();
        try {
            PreparedStatement realStatement = getStatementWithParamsAdded(connection);
            try {
                int queryResult = realStatement.executeUpdate();
                QueryResult queryResultObj = getQueryResultFactory().create(queryResult);
                return queryResultObj;
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
