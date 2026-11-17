package org.pshenko.springcore.operations.commands;

import org.pshenko.springcore.account.AccountService;
import org.pshenko.springcore.console.ConsoleInput;
import org.pshenko.springcore.operations.ConsoleOperationType;
import org.pshenko.springcore.operations.OperationCommand;
import org.pshenko.springcore.user.User;
import org.pshenko.springcore.user.UserService;
import org.springframework.stereotype.Component;

@Component
public class UserCreateCommand implements OperationCommand {
    private final UserService userService;
    private final AccountService accountService;
    private final ConsoleInput input;

    public UserCreateCommand(
        UserService userService,
        AccountService accountService,
        ConsoleInput input
    ) {
        this.userService = userService;
        this.accountService = accountService;
        this.input = input;
    }

    @Override
    public void execute() {
        String login = input.readLogin();
        User user = userService.create(login);
        accountService.create(user.getId());
        System.out.println("User created: " + user);
    }

    @Override
    public ConsoleOperationType getType() {
        return ConsoleOperationType.USER_CREATE;
    }
}
