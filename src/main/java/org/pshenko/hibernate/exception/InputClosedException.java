package org.pshenko.hibernate.exception;

public class InputClosedException extends RuntimeException {
    public InputClosedException() {
        super("Input closed");
    }
}
