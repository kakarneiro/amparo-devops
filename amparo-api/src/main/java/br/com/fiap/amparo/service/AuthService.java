package br.com.fiap.amparo.service;

import br.com.fiap.amparo.dto.LoginRequest;
import br.com.fiap.amparo.dto.RegistroRequest;
import br.com.fiap.amparo.dto.TokenResponse;
import br.com.fiap.amparo.dto.UsuarioResponse;
import br.com.fiap.amparo.exception.BusinessException;
import br.com.fiap.amparo.model.Usuario;
import br.com.fiap.amparo.repository.UsuarioRepository;
import br.com.fiap.amparo.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Transactional
    public UsuarioResponse registrar(RegistroRequest req) {
        if (usuarioRepository.existsByEmail(req.email())) {
            throw new BusinessException("Ja existe usuario com o email " + req.email());
        }
        Usuario u = Usuario.builder()
                .nome(req.nome())
                .email(req.email().toLowerCase())
                .senha(passwordEncoder.encode(req.senha()))   // BCrypt
                .papel(req.papel())
                .ativo(true)
                .build();
        return UsuarioResponse.from(usuarioRepository.save(u));
    }

    /**
     * Fluxo de login:
     * 1. AuthenticationManager carrega o usuario pelo email (UserDetailsService)
     * 2. compara a senha informada com o hash BCrypt
     * 3. se bater, gera o JWT com email + papel
     */
    @Transactional(readOnly = true)
    public TokenResponse login(LoginRequest req) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.email().toLowerCase(), req.senha()));

        Usuario u = usuarioRepository.findByEmail(req.email().toLowerCase()).orElseThrow();
        String token = jwtService.gerar(u);
        return new TokenResponse(token, "Bearer", jwtService.getExpirationMs(), u.getNome(), u.getEmail(), u.getPapel());
    }
}
