package br.com.fiap.amparo.specification;

import br.com.fiap.amparo.model.Cidadao;
import org.springframework.data.jpa.domain.Specification;

/**
 * Filtros dinamicos: cada metodo devolve uma Specification que so entra no
 * WHERE se o parametro foi informado. Combinam-se com .and().
 */
public final class CidadaoSpecification {

    private CidadaoSpecification() {}

    public static Specification<Cidadao> nomeContem(String nome) {
        return (root, query, cb) -> nome == null || nome.isBlank()
                ? null
                : cb.like(cb.lower(root.get("nome")), "%" + nome.toLowerCase() + "%");
    }

    public static Specification<Cidadao> condicaoContem(String condicao) {
        return (root, query, cb) -> condicao == null || condicao.isBlank()
                ? null
                : cb.like(cb.lower(root.get("condicao")), "%" + condicao.toLowerCase() + "%");
    }

    public static Specification<Cidadao> ativo(Boolean ativo) {
        return (root, query, cb) -> ativo == null ? null : cb.equal(root.get("ativo"), ativo);
    }
}
