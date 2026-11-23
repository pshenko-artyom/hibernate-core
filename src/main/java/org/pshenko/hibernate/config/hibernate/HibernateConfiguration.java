package org.pshenko.hibernate.config.hibernate;

import org.hibernate.SessionFactory;
import org.pshenko.hibernate.account.Account;
import org.pshenko.hibernate.user.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HibernateConfiguration {
    private final HibernateProperties props;

    public HibernateConfiguration(HibernateProperties props) {
        this.props = props;
    }

    @Bean
    public SessionFactory sessionFactory() {
        org.hibernate.cfg.Configuration cfg = new org.hibernate.cfg.Configuration();
        cfg
            .addPackage("org.pshenko.hibernate")
            .addAnnotatedClass(Account.class)
            .addAnnotatedClass(User.class)
            .setProperty("hibernate.connection.driver_class", props.getDriver())
            .setProperty("hibernate.connection.url", props.getUrl())
            .setProperty("hibernate.connection.username", props.getUsername())
            .setProperty("hibernate.connection.password", props.getPassword())
            .setProperty("hibernate.hbm2ddl.auto", props.getHbm2dll())
            .setProperty("hibernate.current_session_context_class", props.getSessionContext())
            .setProperty("hibernate.show_sql", props.getShowSql())
            .setProperty("hibernate.format_sql", props.getFormatSql());

        return cfg.buildSessionFactory();
    }
}
