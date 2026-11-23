package org.pshenko.hibernate.config.hibernate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class HibernateProperties {
    private final String driver;
    private final String url;
    private final String username;
    private final String password;
    private final String hbm2dll;
    private final String sessionContext;
    private final String showSql;
    private final String formatSql;

    public HibernateProperties(
        @Value("${db.driver}") String driver,
        @Value("${db.url}") String url,
        @Value("${db.username}") String username,
        @Value("${db.password}") String password,
        @Value("${hibernate.hbm2ddl.auto}") String hbm2dll,
        @Value("${hibernate.current_session_context_class}") String sessionContext,
        @Value("${hibernate.show_sql}") String showSql,
        @Value("${hibernate.format_sql}") String formatSql
    ) {
        this.driver = driver;
        this.url = url;
        this.username = username;
        this.password = password;
        this.hbm2dll = hbm2dll;
        this.sessionContext = sessionContext;
        this.showSql = showSql;
        this.formatSql = formatSql;
    }

    public String getDriver() {
        return driver;
    }

    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getHbm2dll() {
        return hbm2dll;
    }

    public String getSessionContext() {
        return sessionContext;
    }

    public String getShowSql() {
        return showSql;
    }

    public String getFormatSql() {
        return formatSql;
    }
}
