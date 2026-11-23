package org.pshenko.hibernate.account;

import org.pshenko.hibernate.exception.BankException;
import org.pshenko.hibernate.user.User;
import org.pshenko.hibernate.util.TransactionHelper;
import org.springframework.stereotype.Service;

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
        validateAmount(amount);
        return transactionHelper.executeInTransaction(session -> {
            Account account = findById(accountId);
            int existing = account.getMoneyAmount();
            account.setMoneyAmount(existing + amount);
            return account;
        });
    }

    public Account withdraw(int accountId, int amount) {
        validateAmount(amount);
        return transactionHelper.executeInTransaction(session -> {
            Account account = findById(accountId);
            int existing = account.getMoneyAmount();
            if (existing < amount) {
                throw new BankException("insufficient funds on account id=%d, moneyAmount=%d, attempted withdraw=%d"
                    .formatted(accountId, existing, amount));
            }
            account.setMoneyAmount(existing - amount);
            return account;
        });
    }

    //    public TransferResult transfer(int fromAccountId, int toAccountId, int amount) {
//        validateAmount(amount);
//        if (fromAccountId == toAccountId) {
//            throw new BankException("cannot transfer to the same account id=%d".formatted(fromAccountId));
//        }
//        Account fromAccount = getAccountOrThrow(fromAccountId);
//        Account toAccount = getAccountOrThrow(toAccountId);
//        if (fromAccount.getMoneyAmount() < amount) {
//            throw new BankException("insufficient funds on source account id=%d, moneyAmount=%d, attempted transfer=%d"
//                .formatted(fromAccountId, fromAccount.getMoneyAmount(), amount));
//        }
//        boolean sameUser = fromAccount.getUserId() == toAccount.getUserId();
//        double transferCommission = sameUser
//            ? 0
//            : properties.getTransferCommission();
//        int received = (int) Math.round(amount * (1 - transferCommission));
//        int commission = amount - received;
//        fromAccount.setMoneyAmount(fromAccount.getMoneyAmount() - amount);
//        toAccount.setMoneyAmount(toAccount.getMoneyAmount() + received);
//        return new TransferResult(fromAccountId, toAccountId, commission, received);
//    }
//
//    public CloseResult close(int accountId) {
//        Account accountToClose = getAccountOrThrow(accountId);
//        User user = userService.findById(accountToClose.getUserId())
//            .orElseThrow(() -> new BankException("user with id=%d not found".formatted(accountToClose.getUserId())));
//        List<Account> userAccounts = user.getAccounts();
//        Account targetAccount = userAccounts.stream()
//            .filter(account -> account.getId() != accountId)
//            .min(Comparator.comparing(Account::getId))
//            .orElseThrow(() -> new BankException("cannot close account id=%d: it is the only account of user id=%d"
//                .formatted(accountToClose.getId(), user.getId())));
//        int transferred = accountToClose.getMoneyAmount();
//        targetAccount.setMoneyAmount(targetAccount.getMoneyAmount() + transferred);
//        userAccounts.remove(accountToClose);
//        accounts.remove(accountToClose.getId());
//        return new CloseResult(targetAccount.getId(), transferred);
//    }
    
    public Account findById(int accountId) {
        return transactionHelper.executeInTransaction(session -> {
            return Optional.ofNullable(session.find(Account.class, accountId))
                .orElseThrow(() -> new BankException("account with id=%d not found".formatted(accountId)));
        });
    }

    private void validateAmount(int amount) {
        if (amount <= 0) {
            throw new BankException("amount must be greater than zero");
        }
    }
}
