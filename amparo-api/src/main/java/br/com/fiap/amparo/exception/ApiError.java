package br.com.fiap.amparo.exception;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Formato padrao de erro da API. Todo erro (400, 404, 409, 500) responde assim.
 */
public record ApiError(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path,
        List<FieldError> fields
) {
    public record FieldError(String field, String message) {}
}
