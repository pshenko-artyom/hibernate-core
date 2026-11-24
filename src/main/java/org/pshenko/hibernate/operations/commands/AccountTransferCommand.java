package org.pshenko.hibernate.operations.commands;

import org.pshenko.hibernate.account.AccountService;
import org.pshenko.hibernate.account.TransferResult;
import org.pshenko.hibernate.console.ConsoleInput;
import org.pshenko.hibernate.operations.ConsoleOperationType;
import org.pshenko.hibernate.operations.OperationCommand;
import org.springframework.stereotype.Component;

@Component
public class AccountTransferCommand implements OperationCommand {
    private final AccountService accountService;
    private final ConsoleInput input;

    public AccountTransferCommand(AccountService accountService, ConsoleInput input) {
        this.accountService = accountService;
        this.input = input;
    }

    @Override
    public void execute() {
        int from = input.readPositiveInt("Enter source account id:", "source account id");
        int to = input.readPositiveInt("Enter target account id:", "target account id");
        int amount = input.readPositiveInt("Enter amount:", "amount");
        TransferResult transfer = accountService.transfer(from, to, amount);

        if (transfer.isSameUser()) {
            System.out.println("Transfer completed from account " + from
                + " to account " + to
                + ". Amount: " + amount
                + " (no commission, same user)");
        } else {
            System.out.println("Transfer completed from account " + from
                + " to account " + to
                + ". Amount: " + amount
                + ", commission: " + transfer.commission()
                + ", recipient received: " + transfer.amountAfterCommission());
        }
    }

    @Override
    public ConsoleOperationType getType() {
        return ConsoleOperationType.ACCOUNT_TRANSFER;
    }
}
