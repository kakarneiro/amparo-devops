package br.com.fiap.amparo.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Protege o endpoint que o ESP32 chama (POST /eventos).
 * O dispositivo nao faz login: ele envia um token fixo no header X-Device-Token.
 * Qualquer outra rota passa direto por este filtro.
 */
@Component
public class DeviceTokenFilter extends OncePerRequestFilter {

    public static final String HEADER = "X-Device-Token";

    private final String deviceToken;

    public DeviceTokenFilter(@Value("${amparo.device-token}") String deviceToken) {
        this.deviceToken = deviceToken;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // So filtra POST /eventos (exatamente, sem sub-rotas como /eventos/1/escalar)
        return !("POST".equalsIgnoreCase(request.getMethod()) && "/eventos".equals(request.getRequestURI()));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String enviado = request.getHeader(HEADER);
        if (enviado == null || !enviado.equals(deviceToken)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write("""
                    {"status":401,"error":"Unauthorized","message":"Header X-Device-Token ausente ou invalido","path":"/eventos"}
                    """);
            return;
        }
        chain.doFilter(request, response);
    }
}
