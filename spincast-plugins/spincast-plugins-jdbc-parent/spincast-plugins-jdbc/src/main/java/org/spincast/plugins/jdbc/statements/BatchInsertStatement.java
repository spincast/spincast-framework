package org.spincast.plugins.jdbc.statements;

import java.util.List;


public interface BatchInsertStatement extends Statement {

    /**
     * Run the batch insert query.
     */
    public List<? extends QueryResult> batchInsert();

    /**
     * Run the batch insert query and return the generated ids, if current driver supports it.
     * 
     * Note that some driver don't support the return of generated keys when using batch inserts.
     * An exception will be throwed if the generated keys cant be retrieved. Use batchInsert() instead
     * if you use a driver that do not support this feature or if you don't need the generated keys.
     */
    public List<InsertResultWithGeneratedKey> batchInsertGetGeneratedKeys(String primaryKeyName);

    public void addBatch();

}
