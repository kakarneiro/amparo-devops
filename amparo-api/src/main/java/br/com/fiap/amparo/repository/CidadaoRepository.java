package br.com.fiap.amparo.repository;

import br.com.fiap.amparo.model.Cidadao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CidadaoRepository extends JpaRepository<Cidadao, Long>, JpaSpecificationExecutor<Cidadao> {

    /** Query method derivado: busca por parte do nome, sem diferenciar maiusculas. */
    Page<Cidadao> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
}
