package br.com.srm.srmcreditengine.presentation.dto;

import br.com.srm.srmcreditengine.persistence.entities.Liquidacao;
import br.com.srm.srmcreditengine.persistence.enums.Moeda;

import java.math.BigDecimal;
import java.time.LocalDateTime;


public record LiquidacaoRespostaDTO(
        Long id,
        Long recebivelId,
        BigDecimal valorPresenteMoedaTitulo,
        Moeda moedaPagamento,
        BigDecimal valorPresenteMoedaPagamento,
        BigDecimal taxaCambioUtilizada,
        BigDecimal spreadAplicado,
        BigDecimal taxaBaseAplicada,
        LocalDateTime dataLiquidacao) {

    public static LiquidacaoRespostaDTO liquidacaoResposta(Liquidacao liquidacao) {
        return new LiquidacaoRespostaDTO(
                liquidacao.getId(),
                liquidacao.getRecebivel().getId(),
                liquidacao.getValorPresenteMoedaTitulo(),
                liquidacao.getMoedaPagamento(),
                liquidacao.getValorPresenteMoedaPagamento(),
                liquidacao.getTaxaCambioUtilizada(),
                liquidacao.getSpreadAplicado(),
                liquidacao.getTaxaBaseAplicada(),
                liquidacao.getDataLiquidacao());
    }
}
