package org.spincast.plugins.jdbc;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import com.google.inject.Provider;

public class DataSourceInterceptor implements MethodInterceptor {

    private final Provider<JdbcScope> jdbcScopeProvider;
    private JdbcScope jdbcScope;

    public DataSourceInterceptor(Provider<JdbcScope> jdbcScopeProvider) {
        this.jdbcScopeProvider = jdbcScopeProvider;
    }

    protected JdbcScope getJdbcScope() {

        if (this.jdbcScope == null) {
            this.jdbcScope = this.jdbcScopeProvider.get();
        }
        return this.jdbcScope;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {

        if (!"getConnection".equals(invocation.getMethod().getName())) {
            return invocation.proceed();
        }

        return getJdbcScope().getConnectionInterceptor(invocation);
    }

}
