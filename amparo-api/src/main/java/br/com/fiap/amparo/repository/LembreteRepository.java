package br.com.fiap.amparo.repository;

import br.com.fiap.amparo.model.Lembrete;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LembreteRepository extends JpaRepository<Lembrete, Long> {

    /** Query method derivado: lembretes ativos de um cidadao em ordem de horario. */
    List<Lembrete> findByCidadaoIdAndAtivoTrueOrderByHorarioAsc(Long cidadaoId);
}
