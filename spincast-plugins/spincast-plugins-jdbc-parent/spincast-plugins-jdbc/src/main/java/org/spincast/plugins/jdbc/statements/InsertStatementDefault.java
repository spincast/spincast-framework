package org.spincast.plugins.jdbc.statements;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.spincast.core.utils.SpincastStatics;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

public class InsertStatementDefault extends StatementBase implements InsertStatement {

    @AssistedInject
    public InsertStatementDefault(@Assisted Connection connection) {
        super(connection);
    }

    @Override
    public int insert() {
        Long affectedRowsNbr = insertPrivate(null);
        return affectedRowsNbr.intValue();
    }

    @Override
    public Long insertGetGeneratedId(String primaryKeyColumnName) {
        return insertPrivate(primaryKeyColumnName);
    }

    /**
     * If <code>primaryKeyColumnName</code> is null, returns the
     * affected rows number. Otherwise returns the generated id.
     */
    protected Long insertPrivate(String primaryKeyColumnName) {
        Connection connection = getConnection();
        try {
            PreparedStatement realStatement;
            if (primaryKeyColumnName != null) {

                //==========================================
                // Statement.RETURN_GENERATED_KEYS will not work witht Oracle.
                // This method will.
                //==========================================
                realStatement = connection.prepareStatement(getParsedQuery(), new String[]{primaryKeyColumnName});
            } else {
                realStatement = connection.prepareStatement(getParsedQuery());
            }
            addCurrentParamsToStatement(realStatement);

            try {
                int affectedRowsNbr = realStatement.executeUpdate();

                if (primaryKeyColumnName == null) {
                    return Long.valueOf(affectedRowsNbr);
                }

                ResultSet resultSet = realStatement.getGeneratedKeys();
                if (resultSet != null && resultSet.next()) {
                    Long id = resultSet.getLong(1);
                    return id;
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
