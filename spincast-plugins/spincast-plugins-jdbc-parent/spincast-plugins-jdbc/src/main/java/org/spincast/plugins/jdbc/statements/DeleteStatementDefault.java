package org.spincast.plugins.jdbc.statements;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.spincast.core.utils.SpincastStatics;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

public class DeleteStatementDefault extends StatementBase implements DeleteStatement {

    @AssistedInject
    public DeleteStatementDefault(@Assisted Connection connection,
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
    public QueryResult delete() {
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


}
