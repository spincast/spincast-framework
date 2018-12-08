package org.spincast.plugins.jdbc.statements;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.spincast.core.utils.SpincastStatics;
import org.spincast.plugins.jdbc.SpincastResultSetDefault;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

public class DeleteStatementDefault extends StatementBase implements DeleteStatement {

    @AssistedInject
    public DeleteStatementDefault(@Assisted Connection connection) {
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
    public int delete() {
        Connection connection = getConnection();
        try {
            PreparedStatement realStatement = getStatementWithParamsAdded(connection);
            try {
                int affectedRowsNbr = realStatement.executeUpdate();
                return affectedRowsNbr;
            } finally {
                close(realStatement);
            }
        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    @Override
    public <T> List<T> delete(ResultSetHandler<T> resultSetHandler) {
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


}
