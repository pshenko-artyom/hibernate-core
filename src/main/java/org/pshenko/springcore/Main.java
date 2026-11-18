package org.pshenko.springcore;

import org.pshenko.springcore.config.ApplicationConfiguration;
import org.pshenko.springcore.console.OperationsConsoleListener;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {
    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ApplicationConfiguration.class)) {
            OperationsConsoleListener listener = context.getBean(OperationsConsoleListener.class);
            listener.listen();
        }
    }
}
