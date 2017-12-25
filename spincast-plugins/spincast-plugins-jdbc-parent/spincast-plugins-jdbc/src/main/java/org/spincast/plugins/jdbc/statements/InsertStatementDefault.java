package org.spincast.plugins.jdbc.statements;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.spincast.core.utils.SpincastStatics;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

public class InsertStatementDefault extends StatementBase implements InsertStatement {

    @AssistedInject
    public InsertStatementDefault(@Assisted Connection connection,
                                  QueryResultFactory queryResultFactory) {
        super(connection,
              queryResultFactory);
    }

    @Override
    public QueryResult insert() {
        return insertPrivate(null);
    }

    @Override
    public InsertResultWithGeneratedKey insertGetGeneratedKeys(String primaryKeyName) {
        return insertPrivate(primaryKeyName);
    }

    protected InsertResultWithGeneratedKey insertPrivate(String primaryKeyName) {
        Connection connection = getConnection();
        try {
            PreparedStatement realStatement;
            if (primaryKeyName != null) {

                //==========================================
                // Statement.RETURN_GENERATED_KEYS will not work witht Oracle.
                // This method will.
                //==========================================
                realStatement = connection.prepareStatement(getParsedQuery(), new String[]{primaryKeyName});
            } else {
                realStatement = connection.prepareStatement(getParsedQuery());
            }
            addCurrentParamsToStatement(realStatement);

            try {
                int queryResult = realStatement.executeUpdate();

                if (primaryKeyName == null) {
                    return getQueryResultFactory().create(queryResult, null);
                }

                ResultSet resultSet = realStatement.getGeneratedKeys();
                if (resultSet != null && resultSet.next()) {
                    Long id = resultSet.getLong(1);
                    return getQueryResultFactory().create(queryResult, id);
                }

                throw new RuntimeException("Unable to get the generated id!");
            } finally {
                close(realStatement);
            }
        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

}
