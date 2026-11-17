package org.pshenko.springcore.operations.commands;

import org.pshenko.springcore.operations.ConsoleOperationType;
import org.pshenko.springcore.operations.OperationCommand;
import org.pshenko.springcore.user.User;
import org.pshenko.springcore.user.UserService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ShowAllUsersCommand implements OperationCommand {
    private final UserService userService;

    public ShowAllUsersCommand(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void execute() {
        List<User> users = userService.findAll();
        users.forEach(System.out::println);
    }

    @Override
    public ConsoleOperationType getType() {
        return ConsoleOperationType.SHOW_ALL_USERS;
    }
}
