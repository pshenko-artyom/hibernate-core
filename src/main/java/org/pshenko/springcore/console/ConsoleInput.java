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
        System.out.println("Enter command:");
        String input = scanner.nextLine().trim().toUpperCase();
        return ConsoleOperationType.valueOf(input);
    }

    public String readLogin() {
        while (true) {
            System.out.println("Enter login:");
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
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("please enter a positive " + parameterName);
            }
        }
    }
}
