package org.pshenko.hibernate.operations.commands;

import org.pshenko.hibernate.console.ConsoleInput;
import org.pshenko.hibernate.operations.ConsoleOperationType;
import org.pshenko.hibernate.operations.OperationCommand;
import org.pshenko.hibernate.user.User;
import org.pshenko.hibernate.user.UserService;
import org.springframework.stereotype.Component;

@Component
public class UserCreateCommand implements OperationCommand {
    private final UserService userService;
    private final ConsoleInput input;

    public UserCreateCommand(UserService userService, ConsoleInput input) {
        this.userService = userService;
        this.input = input;
    }

    @Override
    public void execute() {
        String login = input.readLogin();
        User user = userService.create(login);
        System.out.println("User created: " + user);
    }

    @Override
    public ConsoleOperationType getType() {
        return ConsoleOperationType.USER_CREATE;
    }
}
