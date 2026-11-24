package org.pshenko.hibernate.operations;

public interface OperationCommand {
    void execute();

    ConsoleOperationType getType();
}
