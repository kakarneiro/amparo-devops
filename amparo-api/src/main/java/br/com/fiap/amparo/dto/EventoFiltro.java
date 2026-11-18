package br.com.fiap.amparo.dto;

import br.com.fiap.amparo.model.StatusEvento;
import br.com.fiap.amparo.model.TipoEvento;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/** Parametros opcionais: GET /eventos?status=&tipo=&cidadaoId=&inicio=&fim= */
public record EventoFiltro(
        StatusEvento status,
        TipoEvento tipo,
        Long cidadaoId,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim
) {}
