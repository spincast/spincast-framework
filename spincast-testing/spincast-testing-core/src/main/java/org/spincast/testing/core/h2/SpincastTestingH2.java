package org.spincast.testing.core.h2;

import java.sql.Connection;

import javax.sql.DataSource;

import org.h2.tools.Server;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.plugins.jdbc.JdbcQueries;
import org.spincast.plugins.jdbc.JdbcUtils;
import org.spincast.plugins.jdbc.SpincastDataSource;
import org.spincast.plugins.jdbc.SpincastDataSourceFactory;
import org.spincast.plugins.jdbc.statements.UpdateStatement;
import org.spincast.shaded.org.apache.commons.lang3.StringUtils;
import org.spincast.testing.core.utils.SpincastTestingUtils;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Singleton
public class SpincastTestingH2 implements Provider<SpincastDataSource> {

    private final SpincastTestingH2Config spincastTestingH2Config;
    private final JdbcUtils jdbcUtils;
    private final SpincastDataSourceFactory spincastDataSourceFactory;
    private Server h2Server = null;
    private Integer serverPort;
    private SpincastDataSource dataSource;

    @Inject
    public SpincastTestingH2(SpincastTestingH2Config spincastTestingH2Config,
                             JdbcUtils jdbcUtils,
                             SpincastDataSourceFactory spincastDataSourceFactory) {
        this.spincastTestingH2Config = spincastTestingH2Config;
        this.jdbcUtils = jdbcUtils;
        this.spincastDataSourceFactory = spincastDataSourceFactory;
    }

    protected Server getH2Server() {
        return this.h2Server;
    }

    protected SpincastTestingH2Config getSpincastTestingH2Config() {
        return this.spincastTestingH2Config;
    }

    protected JdbcUtils getJdbcUtils() {
        return this.jdbcUtils;
    }

    protected SpincastDataSourceFactory getSpincastDataSourceFactory() {
        return this.spincastDataSourceFactory;
    }

    protected int getServerPort() {
        if (this.serverPort == null) {
            Integer port = getSpincastTestingH2Config().getServerPort();
            if (port == null) {
                port = SpincastTestingUtils.findFreePort();
            }
            this.serverPort = port;
        }
        return this.serverPort;
    }

    @Inject
    public void init() {
        try {
            this.h2Server =
                    Server.createTcpServer("-tcpPort", getServerPort() + "", "-tcpAllowOthers").start();
        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    protected String createConnectionString() {
        StringBuilder b = new StringBuilder();
        b.append("jdbc:h2:tcp://");
        b.append(getSpincastTestingH2Config().getServerHost()).append(":").append(getServerPort());
        b.append("/mem:").append(getSpincastTestingH2Config().getDatabaseName());
        b.append(";DATABASE_TO_UPPER=" + Boolean.toString(getSpincastTestingH2Config().isDatabaseToUpper()));

        String compatibilityMode = getSpincastTestingH2Config().getCompatibilityMode();
        if (!StringUtils.isBlank(compatibilityMode)) {
            b.append(";MODE=").append(compatibilityMode);
        }

        return b.toString();
    }

    @Override
    public SpincastDataSource get() {
        if (this.dataSource == null) {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(createConnectionString());
            config.setUsername("");
            config.setPassword("");
            config.setMaximumPoolSize(10);

            DataSource ds = new HikariDataSource(config);
            this.dataSource = getSpincastDataSourceFactory().create(ds);
        }
        return this.dataSource;
    }

    /**
     * Stops the server
     */
    public void stopServer() {
        if (this.h2Server != null) {
            try {
                if (this.h2Server.isRunning(false)) {
                    this.h2Server.stop();
                }
            } catch (Exception ex) {
                System.err.println(ex);
            }
        }
    }

    /**
     * Clears the whole database.
     */
    public void clearDatabase() {
        getJdbcUtils().scopes().autoCommit(get(), new JdbcQueries<Void>() {

            @Override
            public Void run(Connection connection) {

                UpdateStatement stm = getJdbcUtils().statements().createUpdateStatement(connection);
                stm.sql("DROP ALL OBJECTS DELETE FILES ");
                stm.update();
                return null;
            }
        });
    }


}
