package com.opymi.otamap.exception;

/**
 * Thrown when an application tries to read or write property but it isn't possible
 *
 * @author Antonino Verde
 * @since 1.0
 */
public class AccessPropertyException extends RuntimeException {

    public AccessPropertyException(String message, Throwable cause) {
        super(message, cause);
    }

}
