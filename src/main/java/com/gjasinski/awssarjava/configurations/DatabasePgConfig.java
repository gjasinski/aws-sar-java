package com.gjasinski.awssarjava.configurations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.gjasinski.awssarjava.repositories",
        entityManagerFactoryRef = "pgEntityManager",
        transactionManagerRef = "pgTransactionManager"
)
public class DatabasePgConfig {

    @Value("${spring.hibernate.hbm2ddl.auto.database1}")
    private String database1;
    @Value("${spring.jpa.database1-platform}")
    private String database1Platform;
    @Value("${spring.datasource1.driver-class-name}")
    private String driverClassName;
    @Value("${spring.datasource1.url}")
    private String dataSource1Url;
    @Value("${spring.datasource1.username}")
    private String username;
    @Value("${spring.datasource1.password}")
    private String password;

    @Bean
    @Primary
    public LocalContainerEntityManagerFactoryBean pgEntityManager() {
        LocalContainerEntityManagerFactoryBean em
                = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(pgDataSource());
        em.setPackagesToScan(
                new String[]{"com.gjasinski.awssarjava.entity"});

        HibernateJpaVendorAdapter vendorAdapter
                = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", database1);
        properties.put("hibernate.dialect", database1Platform);
        em.setJpaPropertyMap(properties);

        return em;
    }

    @Primary
    @Bean
    public DataSource pgDataSource() {

        DriverManagerDataSource dataSource
                = new DriverManagerDataSource();
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUrl(dataSource1Url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);

        return dataSource;
    }

    @Primary
    @Bean
    public PlatformTransactionManager pgTransactionManager() {

        JpaTransactionManager transactionManager
                = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(
                pgEntityManager().getObject());
        return transactionManager;
    }
}
