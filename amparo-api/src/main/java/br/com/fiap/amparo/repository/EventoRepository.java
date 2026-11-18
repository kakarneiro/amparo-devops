package br.com.fiap.amparo.repository;

import br.com.fiap.amparo.model.Evento;
import br.com.fiap.amparo.model.StatusEvento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EventoRepository extends JpaRepository<Evento, Long>, JpaSpecificationExecutor<Evento> {

    /**
     * @Query em JPQL: historico de eventos de um cidadao. Precisa de JPQL porque
     * o cidadao esta dois niveis acima (Evento -> Dispositivo -> Cidadao).
     */
    @Query("""
            select e from Evento e
            join e.dispositivo d
            join d.cidadao c
            where c.id = :cidadaoId
            order by e.dataHora desc
            """)
    Page<Evento> buscarPorCidadao(@Param("cidadaoId") Long cidadaoId, Pageable pageable);

    /** @Query em JPQL: quantos eventos de um cidadao ainda estao em determinado status. */
    @Query("""
            select count(e) from Evento e
            where e.dispositivo.cidadao.id = :cidadaoId
              and e.status = :status
            """)
    long contarPorCidadaoEStatus(@Param("cidadaoId") Long cidadaoId, @Param("status") StatusEvento status);
}
