package org.pshenko.springcore.operations.commands;

import org.pshenko.springcore.account.Account;
import org.pshenko.springcore.account.AccountService;
import org.pshenko.springcore.console.ConsoleInput;
import org.pshenko.springcore.operations.ConsoleOperationType;
import org.pshenko.springcore.operations.OperationCommand;
import org.springframework.stereotype.Component;

@Component
public class AccountDepositCommand implements OperationCommand {
    private final AccountService accountService;
    private final ConsoleInput input;

    public AccountDepositCommand(AccountService accountService, ConsoleInput input) {
        this.accountService = accountService;
        this.input = input;
    }

    @Override
    public void execute() {
        int accountId = input.readPositiveInt("Enter account id:", "account id");
        int amount = input.readPositiveInt("Enter amount:", "amount");
        Account account = accountService.deposit(accountId, amount);
        System.out.println("Deposited " + amount
            + " to account " + accountId
            + ". New balance: " + account.getMoneyAmount());
    }

    @Override
    public ConsoleOperationType getType() {
        return ConsoleOperationType.ACCOUNT_DEPOSIT;
    }
}
