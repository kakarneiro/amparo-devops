package br.com.fiap.amparo.repository;

import br.com.fiap.amparo.model.Cuidador;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CuidadorRepository extends JpaRepository<Cuidador, Long> {

    /**
     * Query method derivado: cadeia de acionamento de um cidadao,
     * do cuidador de prioridade 1 em diante.
     */
    List<Cuidador> findByCidadaoIdOrderByPrioridadeAsc(Long cidadaoId);
}
