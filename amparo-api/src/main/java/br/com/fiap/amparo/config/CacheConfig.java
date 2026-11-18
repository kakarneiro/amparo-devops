package br.com.fiap.amparo.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

/**
 * Habilita o Spring Cache. Os caches ficam em memoria (ConcurrentMap),
 * suficiente para uma instancia; em producao trocaria por Redis sem mudar o codigo.
 *
 * O que e cacheado e por que (ver README):
 *  - cidadaos            : GET /cidadaos/{id} - lido pelo app a cada abertura de tela
 *  - cuidadoresPorCidadao: cadeia de acionamento - consultada a cada evento de emergencia
 *  - lembretesPorCidadao : lista exibida na home do app, muda raramente
 */
@Configuration
@EnableCaching
public class CacheConfig {
}
