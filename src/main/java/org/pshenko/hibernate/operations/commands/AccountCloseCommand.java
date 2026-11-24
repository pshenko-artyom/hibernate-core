package org.pshenko.hibernate.operations.commands;

import org.pshenko.hibernate.account.AccountService;
import org.pshenko.hibernate.account.CloseResult;
import org.pshenko.hibernate.console.ConsoleInput;
import org.pshenko.hibernate.operations.ConsoleOperationType;
import org.pshenko.hibernate.operations.OperationCommand;
import org.springframework.stereotype.Component;

@Component
public class AccountCloseCommand implements OperationCommand {
    private final AccountService accountService;
    private final ConsoleInput input;

    public AccountCloseCommand(AccountService accountService, ConsoleInput input) {
        this.accountService = accountService;
        this.input = input;
    }

    @Override
    public void execute() {
        int accountId = input.readPositiveInt("Enter account id to close:", "account id");
        CloseResult result = accountService.close(accountId);
        System.out.println("Account " + accountId + " closed. "
            + "Remaining balance " + result.transferredAmount()
            + " transferred to account " + result.targetAccountId() + ".");
    }

    @Override
    public ConsoleOperationType getType() {
        return ConsoleOperationType.ACCOUNT_CLOSE;
    }
}
