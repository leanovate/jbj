package de.leanovate.jbj.api;

/**
 * Base of all exceptions thrown by JBJ.
 */
public class JbjException extends RuntimeException {
    public JbjException(String message) {
        super(message);
    }

    public JbjException(String message, Throwable cause) {
        super(message, cause);
    }

    public JbjException(Throwable cause) {
        super(cause);
    }
}
