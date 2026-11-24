package org.pshenko.hibernate.account;

import org.pshenko.hibernate.exception.BankException;
import org.pshenko.hibernate.user.User;
import org.pshenko.hibernate.util.TransactionHelper;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class AccountService {
    private final AccountProperties properties;
    private final TransactionHelper transactionHelper;

    public AccountService(AccountProperties properties, TransactionHelper transactionHelper) {
        this.properties = properties;
        this.transactionHelper = transactionHelper;
    }

    public Account create(int userId) {
        requirePositive(userId, "user id");
        return transactionHelper.executeInTransaction(session -> {
            User user = Optional.ofNullable(session.find(User.class, userId))
                .orElseThrow(() -> new BankException("user with id=%d not found".formatted(userId)));
            Account account = new Account(properties.getDefaultAmount());
            user.addAccount(account);
            session.persist(account);
            return account;
        });
    }

    public Account deposit(int accountId, int amount) {
        requirePositive(accountId, "account id");
        requirePositive(amount, "amount");
        return transactionHelper.executeInTransaction(session -> {
            Account account = findByIdOrThrow(accountId);
            int existing = account.getMoneyAmount();
            account.setMoneyAmount(existing + amount);
            return account;
        });
    }

    public Account withdraw(int accountId, int amount) {
        requirePositive(accountId, "account id");
        requirePositive(amount, "amount");
        return transactionHelper.executeInTransaction(session -> {
            Account account = findByIdOrThrow(accountId);
            int existing = account.getMoneyAmount();
            if (existing < amount) {
                throw new BankException("insufficient funds on account id=%d, moneyAmount=%d, attempted withdraw=%d"
                    .formatted(accountId, existing, amount));
            }
            account.setMoneyAmount(existing - amount);
            return account;
        });
    }

    public TransferResult transfer(int sourceAccountId, int targetAccountId, int amount) {
        requirePositive(sourceAccountId, "source account id");
        requirePositive(targetAccountId, "target account id");
        requirePositive(amount, "amount");
        if (sourceAccountId == targetAccountId) {
            throw new BankException("cannot transfer to the same account id=%d".formatted(sourceAccountId));
        }
        return transactionHelper.executeInTransaction(session -> {
            Account sourceAccount = findByIdOrThrow(sourceAccountId);
            Account targetAccount = findByIdOrThrow(targetAccountId);
            if (sourceAccount.getMoneyAmount() < amount) {
                throw new BankException("insufficient funds on source account id=%d, moneyAmount=%d, attempted transfer=%d"
                    .formatted(sourceAccountId, sourceAccount.getMoneyAmount(), amount));
            }
            boolean sameUser = Objects.equals(sourceAccount.getUser().getId(), targetAccount.getUser().getId());
            double transferCommission = sameUser
                ? 0
                : properties.getTransferCommission();
            int received = (int) Math.round(amount * (1 - transferCommission));
            int commission = amount - received;
            sourceAccount.setMoneyAmount(sourceAccount.getMoneyAmount() - amount);
            targetAccount.setMoneyAmount(targetAccount.getMoneyAmount() + received);
            return new TransferResult(
                sourceAccount.getUser().getId(),
                targetAccount.getUser().getId(),
                commission,
                received
            );
        });
    }

    public CloseResult close(int accountId) {
        requirePositive(accountId, "account id");
        return transactionHelper.executeInTransaction(session -> {
            Account accountToClose = findByIdOrThrow(accountId);
            User user = accountToClose.getUser();
            List<Account> userAccounts = user.getAccounts();
            Account targetAccount = userAccounts.stream()
                .filter(account -> account.getId() != accountId)
                .min(Comparator.comparing(Account::getId))
                .orElseThrow(() -> new BankException("cannot close account id=%d: it is the only account of user id=%d"
                    .formatted(accountToClose.getId(), user.getId())));
            int transferred = accountToClose.getMoneyAmount();
            targetAccount.setMoneyAmount(targetAccount.getMoneyAmount() + transferred);
            user.getAccounts().remove(accountToClose);
            return new CloseResult(targetAccount.getId(), transferred);
        });
    }

    private Account findByIdOrThrow(int accountId) {
        return transactionHelper.executeInTransaction(
            session -> Optional.ofNullable(session.find(Account.class, accountId))
                .orElseThrow(() -> new BankException("account with id=%d not found".formatted(accountId)))
        );
    }

    private void requirePositive(int value, String fieldName) {
        if (value <= 0) {
            throw new BankException(fieldName + " must be greater than zero");
        }
    }
}
