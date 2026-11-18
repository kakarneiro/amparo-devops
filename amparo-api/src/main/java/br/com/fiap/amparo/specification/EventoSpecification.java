package br.com.fiap.amparo.specification;

import br.com.fiap.amparo.model.Evento;
import br.com.fiap.amparo.model.StatusEvento;
import br.com.fiap.amparo.model.TipoEvento;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public final class EventoSpecification {

    private EventoSpecification() {}

    public static Specification<Evento> status(StatusEvento status) {
        return (root, query, cb) -> status == null ? null : cb.equal(root.get("status"), status);
    }

    public static Specification<Evento> tipo(TipoEvento tipo) {
        return (root, query, cb) -> tipo == null ? null : cb.equal(root.get("tipo"), tipo);
    }

    public static Specification<Evento> cidadao(Long cidadaoId) {
        return (root, query, cb) -> cidadaoId == null
                ? null
                : cb.equal(root.get("dispositivo").get("cidadao").get("id"), cidadaoId);
    }

    public static Specification<Evento> aPartirDe(LocalDateTime inicio) {
        return (root, query, cb) -> inicio == null
                ? null
                : cb.greaterThanOrEqualTo(root.get("dataHora"), inicio);
    }

    public static Specification<Evento> ate(LocalDateTime fim) {
        return (root, query, cb) -> fim == null
                ? null
                : cb.lessThanOrEqualTo(root.get("dataHora"), fim);
    }
}
