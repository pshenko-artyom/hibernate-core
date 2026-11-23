package org.pshenko.hibernate.operations.commands;

import org.pshenko.hibernate.account.AccountService;
import org.pshenko.hibernate.console.ConsoleInput;
import org.pshenko.hibernate.operations.ConsoleOperationType;
import org.pshenko.hibernate.operations.OperationCommand;
import org.springframework.stereotype.Component;

@Component
public class AccountWithdrawCommand implements OperationCommand {
    private final AccountService accountService;
    private final ConsoleInput input;

    public AccountWithdrawCommand(AccountService accountService, ConsoleInput input) {
        this.accountService = accountService;
        this.input = input;
    }

    @Override
    public void execute() {
//        int accountId = input.readPositiveInt("Enter account id:", "account id");
//        int amount = input.readPositiveInt("Enter amount:", "amount");
//        Account account = accountService.withdraw(accountId, amount);
//        System.out.println("Withdrew " + amount + " from account " + accountId + ". New balance: " +
//            account.getMoneyAmount());
    }

    @Override
    public ConsoleOperationType getType() {
        return ConsoleOperationType.ACCOUNT_WITHDRAW;
    }
}
