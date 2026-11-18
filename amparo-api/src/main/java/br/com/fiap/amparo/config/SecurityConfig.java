package br.com.fiap.amparo.config;

import br.com.fiap.amparo.repository.UsuarioRepository;
import br.com.fiap.amparo.security.DeviceTokenFilter;
import br.com.fiap.amparo.security.JwtAuthFilter;
import br.com.fiap.amparo.security.SecurityErrorHandlers;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private static final String ADMIN = "ADMIN";
    private static final String CUIDADOR = "CUIDADOR";

    private final JwtAuthFilter jwtAuthFilter;
    private final DeviceTokenFilter deviceTokenFilter;
    private final UsuarioRepository usuarioRepository;

    @Value("${amparo.cors.allowed-origins}")
    private String allowedOrigins;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // API stateless com JWT: sem CSRF, sem sessao
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // console do H2 abre em iframe
                .headers(h -> h.frameOptions(f -> f.sameOrigin()))
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(SecurityErrorHandlers.entryPoint())
                        .accessDeniedHandler(SecurityErrorHandlers.accessDenied()))
                .authorizeHttpRequests(auth -> auth
                        // Publico
                        .requestMatchers("/auth/registro", "/auth/login").permitAll()
                        .requestMatchers("/h2/**").permitAll()
                        .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // ESP32: protegido pelo DeviceTokenFilter, nao por JWT
                        .requestMatchers(HttpMethod.POST, "/eventos").permitAll()

                        // Cadastros: so ADMIN escreve
                        .requestMatchers(HttpMethod.POST, "/cidadaos/**", "/cuidadores/**", "/dispositivos/**", "/lembretes/**").hasRole(ADMIN)
                        .requestMatchers(HttpMethod.PUT, "/cidadaos/**", "/cuidadores/**", "/dispositivos/**", "/lembretes/**").hasRole(ADMIN)
                        .requestMatchers(HttpMethod.DELETE, "/**").hasRole(ADMIN)

                        // Operacao: ADMIN e CUIDADOR
                        .requestMatchers(HttpMethod.POST, "/eventos/*/escalar", "/atendimentos/**").hasAnyRole(ADMIN, CUIDADOR)
                        .requestMatchers(HttpMethod.PUT, "/eventos/**", "/atendimentos/**").hasAnyRole(ADMIN, CUIDADOR)
                        .requestMatchers(HttpMethod.PATCH, "/eventos/**").hasAnyRole(ADMIN, CUIDADOR)

                        // Leitura: qualquer usuario logado
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(deviceTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /** CORS para o app Mobile (Flutter) e Swagger. */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();
        cfg.setAllowedOriginPatterns(Arrays.stream(allowedOrigins.split(",")).map(String::trim).toList());
        cfg.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        cfg.setAllowedHeaders(List.of("Authorization", "Content-Type", DeviceTokenFilter.HEADER));
        cfg.setExposedHeaders(List.of("Location"));
        cfg.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return email -> usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario nao encontrado: " + email));
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
