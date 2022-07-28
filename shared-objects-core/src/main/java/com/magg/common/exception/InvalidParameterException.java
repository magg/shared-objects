package com.magg.common.exception;

/**
 * Class Description goes here.
 */
public class InvalidParameterException extends RuntimeException {
    /**
     * Constructor.
     *
     * @param message Error message.
     * @param cause {@link Throwable}
     */
    public InvalidParameterException(String message, Throwable cause) {
        super(message, cause);
    }
}
