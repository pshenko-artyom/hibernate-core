package org.pshenko.hibernate.config;

import org.hibernate.SessionFactory;
import org.pshenko.hibernate.account.Account;
import org.pshenko.hibernate.user.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HibernateConfiguration {

    private final String driver;
    private final String url;
    private final String username;
    private final String password;
    private final String hbm2ddl;
    private final String sessionContext;
    private final String showSql;
    private final String formatSql;
    private final String dialect;

    public HibernateConfiguration(
        @Value("${db.driver}") String driver,
        @Value("${db.url}") String url,
        @Value("${db.username}") String username,
        @Value("${db.password}") String password,
        @Value("${hibernate.hbm2ddl.auto}") String hbm2ddl,
        @Value("${hibernate.current_session_context_class}") String sessionContext,
        @Value("${hibernate.show_sql}") String showSql,
        @Value("${hibernate.format_sql}") String formatSql,
        @Value("${db.dialect}") String dialect
    ) {
        this.driver = driver;
        this.url = url;
        this.username = username;
        this.password = password;
        this.hbm2ddl = hbm2ddl;
        this.sessionContext = sessionContext;
        this.showSql = showSql;
        this.formatSql = formatSql;
        this.dialect = dialect;
    }

    @Bean(destroyMethod = "close")
    public SessionFactory sessionFactory() {
        org.hibernate.cfg.Configuration cfg = new org.hibernate.cfg.Configuration();
        cfg
            .addAnnotatedClass(Account.class)
            .addAnnotatedClass(User.class)
            .addPackage("org.pshenko.hibernate")
            .setProperty("hibernate.connection.driver_class", driver)
            .setProperty("hibernate.connection.url", url)
            .setProperty("hibernate.connection.username", username)
            .setProperty("hibernate.connection.password", password)
            .setProperty("hibernate.dialect", dialect)
            .setProperty("hibernate.hbm2ddl.auto", hbm2ddl)
            .setProperty("hibernate.current_session_context_class", sessionContext)
            .setProperty("hibernate.show_sql", showSql)
            .setProperty("hibernate.format_sql", formatSql);
        return cfg.buildSessionFactory();
    }
}
