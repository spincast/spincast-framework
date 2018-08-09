package org.spincast.plugins.jdbc;

import javax.sql.DataSource;

import org.aopalliance.intercept.MethodInterceptor;
import org.spincast.core.exchange.RequestContext;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.websocket.WebsocketContext;
import org.spincast.plugins.jdbc.statements.BatchInsertStatement;
import org.spincast.plugins.jdbc.statements.BatchInsertStatementDefault;
import org.spincast.plugins.jdbc.statements.DeleteStatement;
import org.spincast.plugins.jdbc.statements.DeleteStatementDefault;
import org.spincast.plugins.jdbc.statements.InsertResultWithGeneratedKey;
import org.spincast.plugins.jdbc.statements.InsertResultWithGeneratedKeyDefault;
import org.spincast.plugins.jdbc.statements.InsertStatement;
import org.spincast.plugins.jdbc.statements.InsertStatementDefault;
import org.spincast.plugins.jdbc.statements.QueryResult;
import org.spincast.plugins.jdbc.statements.QueryResultDefault;
import org.spincast.plugins.jdbc.statements.QueryResultFactory;
import org.spincast.plugins.jdbc.statements.SelectStatement;
import org.spincast.plugins.jdbc.statements.SelectStatementDefault;
import org.spincast.plugins.jdbc.statements.UpdateStatement;
import org.spincast.plugins.jdbc.statements.UpdateStatementDefault;

import com.google.inject.Scopes;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.matcher.Matchers;

public class SpincastJdbcPluginModule extends SpincastGuiceModuleBase {

    public SpincastJdbcPluginModule() {
        super();
    }

    public SpincastJdbcPluginModule(Class<? extends RequestContext<?>> requestContextImplementationClass,
                                    Class<? extends WebsocketContext<?>> websocketContextImplementationClass) {
        super(requestContextImplementationClass, websocketContextImplementationClass);
    }

    @Override
    protected void configure() {

        install(new FactoryModuleBuilder().implement(SelectStatement.class, getSelectJdbcStatementImpl())
                                          .implement(InsertStatement.class, getInsertJdbcStatementDefaultImpl())
                                          .implement(BatchInsertStatement.class, getBatchInsertJdbcStatementImpl())
                                          .implement(UpdateStatement.class, getUpdateJdbcStatementImpl())
                                          .implement(DeleteStatement.class, getDeleteJdbcStatementImpl())
                                          .build(JdbcStatementFactory.class));

        install(new FactoryModuleBuilder().implement(QueryResult.class, getQueryResultImpl())
                                          .implement(InsertResultWithGeneratedKey.class,
                                                     getInsertResultWithGeneratedKeyImpl())
                                          .build(QueryResultFactory.class));

        install(new FactoryModuleBuilder().implement(SpincastDataSource.class, getSpincastDataSourceImpl())
                                          .build(SpincastDataSourceFactory.class));

        install(new FactoryModuleBuilder().implement(SpincastConnection.class, getSpincastConnectionImpl())
                                          .build(SpincastConnectionFactory.class));

        bind(JdbcUtils.class).to(getJdbcUtilsImpl()).in(Scopes.SINGLETON);

        bindInterceptor(Matchers.subclassesOf(DataSource.class),
                        Matchers.any(),
                        getDataSourceInterceptor());

    }

    protected Class<? extends SelectStatement> getSelectJdbcStatementImpl() {
        return SelectStatementDefault.class;
    }

    protected Class<? extends InsertStatement> getInsertJdbcStatementDefaultImpl() {
        return InsertStatementDefault.class;
    }

    protected Class<? extends BatchInsertStatement> getBatchInsertJdbcStatementImpl() {
        return BatchInsertStatementDefault.class;
    }

    protected Class<? extends UpdateStatement> getUpdateJdbcStatementImpl() {
        return UpdateStatementDefault.class;
    }

    protected Class<? extends DeleteStatement> getDeleteJdbcStatementImpl() {
        return DeleteStatementDefault.class;
    }

    protected Class<? extends QueryResult> getQueryResultImpl() {
        return QueryResultDefault.class;
    }

    protected Class<? extends InsertResultWithGeneratedKey> getInsertResultWithGeneratedKeyImpl() {
        return InsertResultWithGeneratedKeyDefault.class;
    }

    protected Class<? extends SpincastDataSource> getSpincastDataSourceImpl() {
        return SpincastDataSourceDefault.class;
    }

    protected Class<? extends SpincastConnection> getSpincastConnectionImpl() {
        return SpincastConnectionDefault.class;
    }

    protected MethodInterceptor getDataSourceInterceptor() {
        return new DataSourceInterceptor(getProvider(JdbcScope.class));
    }

    private Class<? extends JdbcUtils> getJdbcUtilsImpl() {
        return JdbcUtilsDefault.class;
    }



}
