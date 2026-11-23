package org.pshenko.hibernate.operations.commands;

import org.pshenko.hibernate.operations.ConsoleOperationType;
import org.pshenko.hibernate.operations.OperationCommand;
import org.pshenko.hibernate.user.User;
import org.pshenko.hibernate.user.UserService;
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
