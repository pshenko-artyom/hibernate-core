package org.pshenko.springcore.operations.commands;

import org.pshenko.springcore.account.Account;
import org.pshenko.springcore.account.AccountService;
import org.pshenko.springcore.console.ConsoleInput;
import org.pshenko.springcore.operations.ConsoleOperationType;
import org.pshenko.springcore.operations.OperationCommand;
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
        int userId = input.readPositiveInt("Enter user id: ", "user id");
        Account account = accountService.create(userId);
        System.out.println("Account created: " + account);
    }

    @Override
    public ConsoleOperationType getType() {
        return ConsoleOperationType.ACCOUNT_CREATE;
    }
}
