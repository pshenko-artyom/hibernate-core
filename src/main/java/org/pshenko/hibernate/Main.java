package org.pshenko.hibernate;

import org.pshenko.hibernate.config.ApplicationConfiguration;
import org.pshenko.hibernate.console.OperationsConsoleListener;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {
    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ApplicationConfiguration.class)) {
            OperationsConsoleListener listener = context.getBean(OperationsConsoleListener.class);
            listener.listen();
        }
    }
}
