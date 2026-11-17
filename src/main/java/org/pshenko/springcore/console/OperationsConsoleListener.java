package org.pshenko.springcore.console;

import org.pshenko.springcore.operations.ConsoleOperationType;
import org.pshenko.springcore.operations.OperationCommand;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class OperationsConsoleListener {
    private final ConsoleInput input;
    private final Map<ConsoleOperationType, OperationCommand> commands;

    public OperationsConsoleListener(ConsoleInput input, List<OperationCommand> commandList) {
        this.input = input;
        commands = commandList.stream()
            .collect(Collectors.toMap(OperationCommand::getType, Function.identity()));
    }

    public void listen() {
        System.out.println("MiniBand started. Type EXIT to stop");
        while (true) {
            ConsoleOperationType type = input.readCommand();
            if (type == ConsoleOperationType.EXIT) {
                break;
            }
            commands.get(type).execute();
        }
    }
}
