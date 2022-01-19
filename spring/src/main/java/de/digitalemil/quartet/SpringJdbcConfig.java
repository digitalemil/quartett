package de.digitalemil.quartet;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
//@Configuration
public class SpringJdbcConfig {

   // @Bean
    public DataSource ybDataSource(String ip, String port, String user, String password, String dbname, String endpoints, String poolsize, String geo)  {
        Properties poolProperties = new Properties();
        poolProperties.setProperty("dataSourceClassName", "com.yugabyte.ysql.YBClusterAwareDataSource");
        poolProperties.setProperty("maximumPoolSize", poolsize);
        poolProperties.setProperty("dataSource.serverName", ip);
        poolProperties.setProperty("dataSource.portNumber", port);
        poolProperties.setProperty("dataSource.databaseName", dbname);
        poolProperties.setProperty("dataSource.user", user);
        poolProperties.setProperty("dataSource.password", password);
        poolProperties.setProperty("dataSource.topologyKeys", geo);
        poolProperties.setProperty("dataSource.additionalEndpoints", endpoints);
        poolProperties.setProperty("poolName", "ybdb");

        HikariConfig config = new HikariConfig(poolProperties);
        config.validate();
        HikariDataSource ds = new HikariDataSource(config);
        try {
            System.out.println("Datasource: " + ds.getConnection().getMetaData().getURL());
        } catch (SQLException e) {
            System.err.println("Could not connect: "+ e);
        }
       
        return ds;
    }

    
    public DataSource dataSource(String ip, String port, String user, String password, String dbname) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl(
                "jdbc:postgresql://"+ip+":"+port+"/"+dbname);
        dataSource.setUsername(user);
        dataSource.setPassword(password);
        System.out.println("Datasource: " + dataSource.getUrl());
      
        return dataSource;
    }
}