package br.com.srm.srmcreditengine.relatorio.dto;

import java.time.LocalDate;

/**
 * Filtros aceitos pela consulta de Extrato de Liquidacao.
 */
public record FiltroExtratoLiquidacaoDTO(
        LocalDate dataInicio,
        LocalDate dataFim,
        Long cedenteId,
        String moeda,
        int pagina,
        int tamanho) {
}
