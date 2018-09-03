package org.spincast.plugins.jdbc.statements;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;

import org.spincast.core.utils.SpincastStatics;
import org.spincast.plugins.jdbc.JdbcUtils;
import org.spincast.plugins.jdbc.utils.BasicFormatterImpl;

/**
 * 
 * If the connection's {@link Connection#getAutoCommit()} is <code>true/code>,
 * the connection will be automatically closed. If you want to prevent this,
 * you can use the <code>disableCloseConnection</code> parameter... You
 * can then reuse the connection, but it's up to you to close it when you're
 * done.
 * <p>
 * Some code based on :
 * @author adam_crume
 * http://www.javaworld.com/article/2077706/core-java/named-parameters-for-preparedstatement.html
 */
public abstract class StatementBase implements Statement {

    private StringBuilder queryBuilder;
    private String parsedQuery;
    private final Connection connection;
    private final QueryResultFactory queryResultFactory;

    private static final BasicFormatterImpl sqlFormmatter = new BasicFormatterImpl();

    private final Map<String, Set<Integer>> indexMap = new HashMap<>();

    private Map<String, Object> params = new HashMap<>();
    private Map<String, String> staticTokens = new HashMap<>();

    public StatementBase(Connection connection,
                         QueryResultFactory queryResultFactory) {
        this.queryBuilder = new StringBuilder();
        this.connection = connection;
        this.queryResultFactory = queryResultFactory;
    }

    protected StringBuilder getQueryBuilder() {
        return this.queryBuilder;
    }

    protected BasicFormatterImpl getSqlFormmatter() {
        return sqlFormmatter;
    }

    @Override
    public void sql(String sql) {
        getQueryBuilder().append(sql);
    }

    @Override
    public void clearSql() {
        clearSql(false);
    }

    @Override
    public void clearSql(boolean keepCurrentBoundParams) {
        getQueryBuilder().setLength(0);
        this.parsedQuery = null;

        if (!keepCurrentBoundParams) {
            getStaticTokens().clear();
            getParams().clear();
        }
    }

    @Override
    public String getSql(boolean friendly) {

        String sql = parse();

        if (friendly) {
            sql = getSqlFormmatter().format(sql);
        }

        return sql;
    }

    protected String getOriginalQuery() {
        return getQueryBuilder().toString();
    }

    protected String getParsedQuery() {
        if (this.parsedQuery == null) {
            this.parsedQuery = parse();
        }

        return this.parsedQuery;
    }

    protected Map<String, Object> getParams() {
        return this.params;
    }

    protected void setParams(Map<String, Object> params) {
        this.params = params;
    }

    protected Map<String, String> getStaticTokens() {
        return this.staticTokens;
    }

    protected void setStaticTokens(Map<String, String> tokens) {
        this.staticTokens = tokens;
    }

    protected void copyParamsAndStaticTokensTo(Statement newStm) {

        if (newStm == null) {
            return;
        }

        if (!(newStm instanceof StatementBase)) {
            throw new RuntimeException("Expected a " + StatementBase.class.getSimpleName() + " object here : " +
                                       newStm.getClass().getSimpleName());
        }
        StatementBase statementBase = (StatementBase)newStm;
        statementBase.setParams(getParams());
        statementBase.setStaticTokens(getStaticTokens());
    }

    protected String parse() {

        String query = getOriginalQuery();
        if (query == null || query.length() == 0) {
            return "";
        }

        //==========================================
        // Replaces static tokens first. Those can contain
        // named parameters!
        //==========================================
        Map<String, String> staticTokens = getStaticTokens();
        if (staticTokens != null) {
            for (Entry<String, String> entry : staticTokens.entrySet()) {
                String pattern = ":" + Pattern.quote(entry.getKey()) + "(?=($|[^A-Za-z0-9_]))";
                query = query.replaceAll(pattern, entry.getValue());
            }
        }

        Map<String, Set<Integer>> paramMap = getIndexMap();

        int length = query.length();
        StringBuffer parsedQuery = new StringBuffer(length);
        boolean inSingleQuote = false;
        boolean inDoubleQuote = false;
        int index = 1;

        for (int i = 0; i < length; i++) {
            char c = query.charAt(i);
            if (inSingleQuote) {
                if (c == '\'') {
                    inSingleQuote = false;
                }
            } else if (inDoubleQuote) {
                if (c == '"') {
                    inDoubleQuote = false;
                }
            } else {
                if (c == '\'') {
                    inSingleQuote = true;
                } else if (c == '"') {
                    inDoubleQuote = true;
                } else if (c == ':' && i + 1 < length &&
                           Character.isJavaIdentifierStart(query.charAt(i + 1))) {
                    int j = i + 2;
                    while (j < length && Character.isJavaIdentifierPart(query.charAt(j))) {
                        j++;
                    }
                    String name = query.substring(i + 1, j);

                    //==========================================@formatter:off 
                    // Replace the parameter with a question mark  
                    //==========================================@formatter:on
                    c = '?';
                    i += name.length(); // skip past the end if the parameter

                    Set<Integer> indexList = (Set<Integer>)paramMap.get(name);
                    if (indexList == null) {
                        indexList = new HashSet<>();
                        paramMap.put(name, indexList);
                    }
                    indexList.add(new Integer(index));

                    index++;
                }
            }
            if (c != Character.MIN_VALUE) {
                parsedQuery.append(c);
            }
        }

        return parsedQuery.toString();
    }

    protected void clearParams() {
        //==========================================
        // Set to a *new instance* since some statement may
        // used multiple params sets (ex : batch insert)
        //==========================================
        this.params = new HashMap<String, Object>();
    }

    protected QueryResultFactory getQueryResultFactory() {
        return this.queryResultFactory;
    }

    protected Map<String, Set<Integer>> getIndexMap() {
        return this.indexMap;
    }

    protected Connection getConnection() {
        return this.connection;
    }

    protected void addCurrentParamsToStatement(PreparedStatement statement) {
        addParamsToStatement(statement, getParams());
    }

    protected void addParamsToStatement(PreparedStatement statement, Map<String, Object> params) {
        if (statement == null) {
            throw new RuntimeException("statement can't be NULL");
        }

        if (params == null || params.size() == 0) {
            return;
        }

        try {
            for (Entry<String, Object> paramEntry : params.entrySet()) {
                String paramName = paramEntry.getKey();
                Object value = paramEntry.getValue();

                Set<Integer> indexes = getIndexMap().get(paramName);
                if (indexes != null) {
                    for (Integer index : indexes) {

                        if (value instanceof Instant) {
                            Timestamp ts = Timestamp.from((Instant)value);
                            statement.setTimestamp(index, ts, JdbcUtils.UTC_CALENDAR);

                        } else if (value instanceof LocalDate) {
                            java.sql.Date sqlDate = java.sql.Date.valueOf((LocalDate)value);
                            statement.setDate(index, sqlDate);

                        } else {
                            statement.setObject(index, value);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    /**
     * Closes statement quietly
     */
    protected void close(PreparedStatement statement) {
        if (statement != null) {
            try {
                statement.close();
            } catch (Exception ex) {
                //...
            }
        }
    }

    /**
     * Closes resultSet quietly
     */
    protected void close(ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (Exception ex) {
                //...
            }
        }
    }

    public void addParam(String paramName, Object value) {
        getParams().put(paramName, value);
    }

    @Override
    public void setInstant(String paramName, Instant value) {
        addParam(paramName, value);
    }

    @Override
    public void setDate(String paramName, LocalDate value) {
        addParam(paramName, value);
    }

    @Override
    public void setString(String paramName, String value) {
        addParam(paramName, value);
    }

    @Override
    public void setBoolean(String paramName, Boolean value) {
        addParam(paramName, value);
    }

    @Override
    public void setInteger(String paramName, Integer value) {
        addParam(paramName, value);
    }

    @Override
    public void setLong(String paramName, Long value) {
        addParam(paramName, value);
    }

    @Override
    public void setFloat(String paramName, Float value) {
        addParam(paramName, value);
    }

    @Override
    public void setDouble(String paramName, Double value) {
        addParam(paramName, value);
    }

    @Override
    public void setInLong(String paramName, Set<Long> items) {
        StringBuilder in = new StringBuilder();
        if (items != null && items.size() > 0) {
            for (Long item : items) {
                if (item != null) {
                    in.append(item).append(",");
                }
            }
            if (in.length() > 0) {
                in.deleteCharAt(in.length() - 1);
            }
        }
        getStaticTokens().put(paramName, in.toString());
    }

    @Override
    public void setInInteger(String paramName, Set<Integer> items) {
        StringBuilder in = new StringBuilder();
        if (items != null && items.size() > 0) {
            for (Integer item : items) {
                if (item != null) {
                    in.append(item).append(",");
                }
            }
            if (in.length() > 0) {
                in.deleteCharAt(in.length() - 1);
            }
        }
        getStaticTokens().put(paramName, in.toString());
    }

    @Override
    public void setInString(String paramName, Set<String> items) {
        StringBuilder in = new StringBuilder();
        if (items != null && items.size() > 0) {
            for (String item : items) {
                if (item != null) {
                    in.append("'").append(item).append("',");
                }
            }
            if (in.length() > 0) {
                in.deleteCharAt(in.length() - 1);
            }
        }
        getStaticTokens().put(paramName, in.toString());
    }

    @Override
    public void setInStringFromEnumNames(String paramName, Set<? extends Enum<?>> enumItems) {

        Set<String> names = new HashSet<String>();
        if (enumItems != null) {
            for (Enum<?> enumItem : enumItems) {
                names.add(enumItem.name());
            }
        }
        setInString(paramName, names);
    }


    @Override
    public String toString() {
        return getSql(true);
    }

}
