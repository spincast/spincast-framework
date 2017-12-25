package org.spincast.plugins.jdbc;


public interface TransactionalScope<T> {

    public T run() throws Exception;
}
