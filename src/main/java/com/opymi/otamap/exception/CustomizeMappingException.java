package com.opymi.otamap.exception;

/**
 * Thrown when an application tries to customize a field's mapping but the field doesn't exist
 *
 * @author Antonino Verde
 * @since 1.0
 */
public class CustomizeMappingException extends RuntimeException {

    public CustomizeMappingException(String message) {
        super(message);
    }

}
