package com.opymi.otamap.exception;

/**
 * Thrown when an application tries to instantiate an object but default constructor doesn't exists for the type
 *
 * @author Antonino Verde
 * @since 1.0
 */
public class CreateInstanceException extends RuntimeException {

    public CreateInstanceException(String message, Throwable cause) {
        super(message, cause);
    }

}
