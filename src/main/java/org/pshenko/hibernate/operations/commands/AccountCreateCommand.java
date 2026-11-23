package org.pshenko.hibernate.operations.commands;

import org.pshenko.hibernate.account.Account;
import org.pshenko.hibernate.account.AccountService;
import org.pshenko.hibernate.console.ConsoleInput;
import org.pshenko.hibernate.operations.ConsoleOperationType;
import org.pshenko.hibernate.operations.OperationCommand;
import org.springframework.stereotype.Component;

@Component
public class AccountCreateCommand implements OperationCommand {
    private final AccountService accountService;
    private final ConsoleInput input;

    public AccountCreateCommand(AccountService accountService, ConsoleInput input) {
        this.accountService = accountService;
        this.input = input;
    }

    @Override
    public void execute() {
        int userId = input.readPositiveInt("Enter user id:", "user id");
        Account account = accountService.create(userId);
        System.out.println("Account created: " + account);
    }

    @Override
    public ConsoleOperationType getType() {
        return ConsoleOperationType.ACCOUNT_CREATE;
    }
}
