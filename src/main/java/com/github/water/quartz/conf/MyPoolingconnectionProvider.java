package com.github.water.quartz.conf;

import java.sql.Connection;
import java.sql.SQLException;

import javax.annotation.Resource;

import org.quartz.utils.ConnectionProvider;
import org.springframework.beans.factory.annotation.Value;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * @Author : water
 * @Date : 2016年9月18日
 * @Desc : TODO
 * @version: V1.0
 */

public class MyPoolingconnectionProvider implements ConnectionProvider {

	/** Default maximum number of database connections in the pool. */
	public static final int DEFAULT_DB_MAX_CONNECTIONS = 10;

	/** Default maximum number of database connections in the pool. */
	public static final int DEFAULT_DB_MAX_CACHED_STATEMENTS_PER_CONNECTION = 120;

	private String driver;
	private String url;
	private String user;
	private String password;
	private int maxConnections;
//	private int maxCachedStatementsPerConnection;
//	private int maxIdleSeconds;
//	private String validationQuery;
//	private int idleConnectionValidationSeconds;
//	private boolean validateOnCheckout;
//	private String discardIdleConnectionsSeconds;

	@Resource
	private HikariDataSource datasource;

	public Connection getConnection() throws SQLException {
		return datasource.getConnection();
	}

	public void shutdown() throws SQLException {
		datasource.close();

	}

	/**
	 * 初始化方法，应该在调用其setter后调用
	 */
	public void initialize() throws SQLException {
		if (this.url == null) {
			throw new SQLException("DBPool could not be created: DB URL cannot be null");
		}

		if (this.driver == null) {
			throw new SQLException("DBPool driver could not be created: DB driver class name cannot be null!");
		}

		if (this.maxConnections < 0) {
			throw new SQLException(
					"DBPool maxConnectins could not be created: Max connections must be greater than zero!");
		}

		datasource = new HikariDataSource();
		HikariConfig config = new HikariConfig();
		config.setDriverClassName(this.driver);
		config.setJdbcUrl(this.url);
		config.setUsername(this.user);
		config.setPassword(this.password);
		config.setMaximumPoolSize(this.maxConnections);
		config.setMinimumIdle(1);
//		config.setMaxLifetime(maxIdleSeconds);
//		config.setMaximumPoolSize(this.maxCachedStatementsPerConnection);
		datasource = new HikariDataSource(config);
		// if (this.validationQuery != null) {
		// datasource.setPreferredTestQuery(this.validationQuery);
		// if (!validateOnCheckout)
		// datasource.setTestConnectionOnCheckin(true);
		// else
		// datasource.setTestConnectionOnCheckout(true);
		// datasource.setIdleConnectionTestPeriod(this.idleConnectionValidationSeconds);
		// }
	}

	/*------------------------------------------------- 
	 *  
	 * setters 如果有必要，你可以添加一些getter 
	 * ------------------------------------------------ 
	 */
	@Value("${spring.datasource}")
	public void setDriver(String driver) {
		this.driver = driver;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setMaxConnections(int maxConnections) {
		this.maxConnections = maxConnections;
	}

//	public void setMaxCachedStatementsPerConnection(int maxCachedStatementsPerConnection) {
//		this.maxCachedStatementsPerConnection = maxCachedStatementsPerConnection;
//	}
//
//	public void setMaxIdleSeconds(int maxIdleSeconds) {
//		this.maxIdleSeconds = maxIdleSeconds;
//	}
//
//	public void setValidationQuery(String validationQuery) {
//		this.validationQuery = validationQuery;
//	}
//
//	public void setIdleConnectionValidationSeconds(int idleConnectionValidationSeconds) {
//		this.idleConnectionValidationSeconds = idleConnectionValidationSeconds;
//	}
//
//	public void setValidateOnCheckout(boolean validateOnCheckout) {
//		this.validateOnCheckout = validateOnCheckout;
//	}
//
//	public void setDiscardIdleConnectionsSeconds(String discardIdleConnectionsSeconds) {
//		this.discardIdleConnectionsSeconds = discardIdleConnectionsSeconds;
//	}

	public void setDatasource(HikariDataSource datasource) {
		this.datasource = datasource;
	}

	protected HikariDataSource getDataSource() {
		return datasource;
	}
}
