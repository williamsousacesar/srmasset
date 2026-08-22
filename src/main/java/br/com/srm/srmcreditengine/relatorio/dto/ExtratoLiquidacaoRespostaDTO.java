package br.com.srm.srmcreditengine.relatorio.dto;

import java.util.List;

/**
 * Envelope paginado do Extrato de Liquidacao.
 */
public record ExtratoLiquidacaoRespostaDTO(
        List<ItemExtratoLiquidacaoDTO> conteudo,
        int pagina,
        int tamanho,
        long totalDeElementos,
        int totalDePaginas) {
}
