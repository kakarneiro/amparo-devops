package br.com.fiap.amparo.hateoas;

import br.com.fiap.amparo.controller.CidadaoController;
import br.com.fiap.amparo.controller.LembreteController;
import br.com.fiap.amparo.dto.CidadaoResponse;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Enriquece o CidadaoResponse com links HATEOAS:
 * self, lista, recursos relacionados (cuidadores, eventos, lembretes) e acoes (atualizar, excluir).
 * Assim o app Mobile navega pela API sem montar URLs na mao.
 */
@Component
public class CidadaoModelAssembler implements RepresentationModelAssembler<CidadaoResponse, EntityModel<CidadaoResponse>> {

    @Override
    public EntityModel<CidadaoResponse> toModel(CidadaoResponse c) {
        return EntityModel.of(c,
                linkTo(methodOn(CidadaoController.class).buscar(c.id())).withSelfRel(),
                linkTo(methodOn(CidadaoController.class).listar(null, null)).withRel("cidadaos"),
                linkTo(methodOn(CidadaoController.class).cuidadores(c.id())).withRel("cuidadores"),
                linkTo(methodOn(CidadaoController.class).eventos(c.id(), null)).withRel("eventos"),
                linkTo(methodOn(LembreteController.class).ativosPorCidadao(c.id())).withRel("lembretes"),
                linkTo(methodOn(CidadaoController.class).atualizar(c.id(), null)).withRel("atualizar"),
                linkTo(methodOn(CidadaoController.class).excluir(c.id())).withRel("excluir"));
    }
}
