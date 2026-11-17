package org.pshenko.springcore.console;

import org.pshenko.springcore.exception.BankException;
import org.pshenko.springcore.operations.ConsoleOperationType;
import org.pshenko.springcore.operations.OperationCommand;
import org.springframework.stereotype.Component;

import java.util.Arrays;
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
        String available = Arrays.stream(ConsoleOperationType.values())
            .map(Enum::name)
            .collect(Collectors.joining(", "));
        System.out.println("MiniBank started. Type EXIT to stop.");
        System.out.println("Available commands: " + available);
        while (true) {
            ConsoleOperationType type = input.readCommand();
            if (type == ConsoleOperationType.EXIT) {
                System.out.println("Minibank stopped");
                break;
            }

            try {
                commands.get(type).execute();
            } catch (BankException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
}
