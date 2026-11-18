package br.com.fiap.amparo.hateoas;

import br.com.fiap.amparo.controller.CidadaoController;
import br.com.fiap.amparo.controller.EventoController;
import br.com.fiap.amparo.dto.EventoResponse;
import br.com.fiap.amparo.model.StatusEvento;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Links do evento. As acoes "escalar" e "alterar-status" so aparecem
 * enquanto o evento esta aberto: o cliente descobre o que pode fazer pelo proprio recurso.
 */
@Component
public class EventoModelAssembler implements RepresentationModelAssembler<EventoResponse, EntityModel<EventoResponse>> {

    @Override
    public EntityModel<EventoResponse> toModel(EventoResponse e) {
        EntityModel<EventoResponse> model = EntityModel.of(e,
                linkTo(methodOn(EventoController.class).buscar(e.id())).withSelfRel(),
                linkTo(methodOn(EventoController.class).listar(null, null)).withRel("eventos"),
                linkTo(methodOn(EventoController.class).atendimentos(e.id())).withRel("atendimentos"),
                linkTo(methodOn(CidadaoController.class).buscar(e.cidadaoId())).withRel("cidadao"));

        boolean encerrado = e.status() == StatusEvento.RESOLVIDO || e.status() == StatusEvento.FALSO_ALARME;
        if (!encerrado) {
            model.add(linkTo(methodOn(EventoController.class).escalar(e.id())).withRel("escalar"));
            model.add(linkTo(methodOn(EventoController.class).alterarStatus(e.id(), null)).withRel("alterar-status"));
        }
        return model;
    }
}
