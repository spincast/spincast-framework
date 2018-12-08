package org.spincast.plugins.jdbc.statements;

import java.util.List;


public interface BatchInsertStatement extends Statement {

    /**
     * Run the batch insert query. Returns the same informations as the
     * native JDBC specs:
     * @see https://docs.oracle.com/javase/7/docs/api/java/sql/Statement.html#executeBatch()
     */
    public int[] batchInsert();

    /**
     * Run the batch insert query and return the generated ids, if 
     * the current driver supports it.
     * <p>
     * Note that some drivers don't support the return of generated keys when using batch inserts.
     * An exception will be throwed if the generated keys cant be retrieved.
     */
    public List<Long> batchInsert(String primaryKeyName);

    public void addBatch();

}
