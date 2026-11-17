package org.pshenko.springcore.operations;

public interface OperationCommand {
    void execute();

    ConsoleOperationType getType();
}
