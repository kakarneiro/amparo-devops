package br.com.fiap.amparo.dto;

/** Parametros opcionais de busca: GET /cidadaos?nome=&condicao=&ativo= */
public record CidadaoFiltro(String nome, String condicao, Boolean ativo) {}
