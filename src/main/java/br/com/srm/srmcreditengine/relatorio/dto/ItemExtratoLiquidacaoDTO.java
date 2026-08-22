package br.com.srm.srmcreditengine.relatorio.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ItemExtratoLiquidacaoDTO(
        Long liquidacaoId,
        Long recebivelId,
        Long cedenteId,
        String nomeCedente,
        String tipoRecebivel,
        String moedaTitulo,
        String moedaPagamento,
        BigDecimal valorPresenteMoedaTitulo,
        BigDecimal valorPresenteMoedaPagamento,
        BigDecimal taxaCambioUtilizada,
        BigDecimal spreadAplicado,
        BigDecimal taxaBaseAplicada,
        LocalDateTime dataLiquidacao) {
}
