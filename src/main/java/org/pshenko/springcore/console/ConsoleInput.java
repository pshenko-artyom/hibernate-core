package org.pshenko.springcore.console;

import org.pshenko.springcore.exception.InputClosedException;
import org.pshenko.springcore.operations.ConsoleOperationType;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class ConsoleInput {
    private final Scanner scanner;

    public ConsoleInput(Scanner scanner) {
        this.scanner = scanner;
    }

    public ConsoleOperationType readCommand() {
        while (true) {
            System.out.println("\nEnter command:");
            String raw = nextLine().trim();
            try {
                return ConsoleOperationType.valueOf(raw.toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("Error: unknown command \"" + raw + "\"");
            }
        }
    }

    public String readLogin() {
        while (true) {
            System.out.println("Enter login:");
            String login = nextLine().trim();
            if (!login.isEmpty()) {
                return login;
            }
            System.out.println("Error: login cannot be empty");
        }
    }

    public int readPositiveInt(String prompt, String parameterName) {
        while (true) {
            System.out.println(prompt);
            String line = nextLine().trim();
            try {
                int value = Integer.parseInt(line);
                if (value <= 0) {
                    System.out.println("Error: please enter a positive " + parameterName);
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Error: please enter a positive " + parameterName);
            }
        }
    }

    private String nextLine() {
        if (!scanner.hasNextLine()) {
            throw new InputClosedException();
        }
        return scanner.nextLine();
    }
}
