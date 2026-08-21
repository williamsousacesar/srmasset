package br.com.srm.srmcreditengine.presentation.dto;

import br.com.srm.srmcreditengine.persistence.enums.Moeda;
import jakarta.validation.constraints.NotNull;

public record LiquidacaoRequisicaoDTO(
        @NotNull(message = "recebivelId e obrigatorio")
        Long recebivelId,
        @NotNull(message = "moedaPagamento e obrigatoria")
        Moeda moedaPagamento) {
}
