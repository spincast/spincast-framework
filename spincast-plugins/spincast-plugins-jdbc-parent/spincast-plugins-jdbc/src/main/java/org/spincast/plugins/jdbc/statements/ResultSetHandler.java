package org.spincast.plugins.jdbc.statements;

import org.spincast.plugins.jdbc.SpincastResultSet;

public interface ResultSetHandler<T> {

    public T handle(SpincastResultSet rs) throws Exception;

}
