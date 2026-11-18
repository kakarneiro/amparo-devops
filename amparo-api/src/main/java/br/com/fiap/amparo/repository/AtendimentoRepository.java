package br.com.fiap.amparo.repository;

import br.com.fiap.amparo.model.Atendimento;
import br.com.fiap.amparo.model.ResultadoAtendimento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AtendimentoRepository extends JpaRepository<Atendimento, Long> {

    /** Query method derivado: linha do tempo de atendimentos de um evento. */
    List<Atendimento> findByEventoIdOrderByDataAcionamentoAsc(Long eventoId);

    /** Query method derivado: o atendimento que esta aguardando resposta agora. */
    Optional<Atendimento> findFirstByEventoIdAndResultado(Long eventoId, ResultadoAtendimento resultado);

    long countByEventoId(Long eventoId);
}
