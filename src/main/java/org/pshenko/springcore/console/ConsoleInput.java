package org.pshenko.springcore.console;

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
            System.out.print("\nEnter command: ");
            String raw = scanner.nextLine().trim();
            try {
                return ConsoleOperationType.valueOf(raw.toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("Error: unknown command \"" + raw + "\"");
            }
        }
    }

    public String readLogin() {
        while (true) {
            System.out.print("Enter login:");
            String login = scanner.nextLine().trim();
            if (!login.isEmpty()) {
                return login;
            }
            System.out.println("login cannot be empty");
        }
    }

    public int readPositiveInt(String prompt, String parameterName) {
        while (true) {
            System.out.println(prompt);
            String line = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(line);
                if (value <= 0) {
                    System.out.println("please enter a positive " + parameterName);
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("please enter a positive " + parameterName);
            }
        }
    }
}
