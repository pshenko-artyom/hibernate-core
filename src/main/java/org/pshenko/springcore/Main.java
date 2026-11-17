package org.pshenko.springcore;

import org.pshenko.springcore.config.ApplicationConfiguration;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context =
            new AnnotationConfigApplicationContext(ApplicationConfiguration.class);
//        OperationsConsoleListener listener = context.getBean(OperationsConsoleListener.class);
//        UserService userService = context.getBean(UserService.class);
//        userService.create("Artyom");
//        listener.listen();
    }
}
