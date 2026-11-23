package org.pshenko.hibernate.user;

import jakarta.persistence.*;
import org.pshenko.hibernate.account.Account;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "login", unique = true, nullable = false)
    private String login;

    @OneToMany(
        mappedBy = "user",
        fetch = FetchType.LAZY,
        orphanRemoval = true
    )
    private List<Account> accounts = new ArrayList<>();

    public User() {

    }

    public User(String login) {
        this.login = login;
    }

    public Integer getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void addAccount(Account account) {
        accounts.add(account);
        account.setUser(this);
    }

    public void removeAccount(Account account) {
        accounts.remove(account);
        account.setUser(null);
    }

    @Override
    public String toString() {
        return "User{" +
            "id=" + id +
            ", login='" + login + '\'' +
            ", accounts=" + accounts +
            '}';
    }
}
