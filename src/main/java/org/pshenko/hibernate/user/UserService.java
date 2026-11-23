package org.pshenko.hibernate.user;

import org.pshenko.hibernate.account.AccountService;
import org.pshenko.hibernate.exception.BankException;
import org.pshenko.hibernate.util.TransactionHelper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final TransactionHelper transactionHelper;
    private final AccountService accountService;

    public UserService(TransactionHelper transactionHelper, AccountService accountService) {
        this.transactionHelper = transactionHelper;
        this.accountService = accountService;
    }

    public User create(String rawLogin) {
        String login = validateAndTrim(rawLogin);
        return transactionHelper.executeInTransaction(session -> {
            findByLogin(login).ifPresent(user -> {
                throw new BankException("user with login \"%s\" already exists".formatted(login));
            });
            User user = new User(login);
            session.persist(user);
            accountService.create(user.getId());
            return user;
        });
    }

    public Optional<User> findByLogin(String rawLogin) {
        String login = validateAndTrim(rawLogin);
        return transactionHelper.executeInTransaction(session -> {
            return session.createQuery("from User where login = :login", User.class)
                .setParameter("login", login)
                .uniqueResultOptional();
        });
    }

    public Optional<User> findById(int id) {
        return transactionHelper.executeInTransaction(session -> {
            return Optional.of(session.find(User.class, id));
        });
    }

    public List<User> findAll() {
        return transactionHelper.executeInTransaction(session -> {
            return session.createQuery("select u from User u left join fetch u.accounts", User.class)
                .getResultList();
        });
    }

    private String validateAndTrim(String login) {
        if (login == null || login.isBlank()) {
            throw new BankException("login cannot be empty");
        }
        return login.trim();
    }
}
