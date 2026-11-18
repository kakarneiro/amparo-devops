package br.com.fiap.amparo.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Erros de seguranca acontecem nos filtros, antes do @RestControllerAdvice,
 * entao precisam de handlers proprios para responder no mesmo formato ApiError.
 */
public final class SecurityErrorHandlers {

    private SecurityErrorHandlers() {}

    /** Sem token ou token invalido em rota protegida -> 401 */
    public static AuthenticationEntryPoint entryPoint() {
        return (request, response, ex) ->
                escrever(response, request, 401, "Unauthorized", "Autenticacao necessaria: envie Authorization: Bearer <token>");
    }

    /** Autenticado, mas sem o papel exigido -> 403 */
    public static AccessDeniedHandler accessDenied() {
        return (request, response, ex) ->
                escrever(response, request, 403, "Forbidden", "Seu papel nao tem permissao para esta operacao");
    }

    private static void escrever(HttpServletResponse response, HttpServletRequest request,
                                 int status, String error, String message) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(String.format(
                "{\"timestamp\":\"%s\",\"status\":%d,\"error\":\"%s\",\"message\":\"%s\",\"path\":\"%s\"}",
                LocalDateTime.now(), status, error, message, request.getRequestURI()));
    }
}
