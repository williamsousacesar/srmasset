package br.com.srm.srmcreditengine.presentation.dto;

import br.com.srm.srmcreditengine.persistence.enums.Moeda;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * DTO de requisicao para atualizacao manual de uma taxa de cambio.
 */
public record TaxaCambioRequisicaoDTO(
        @NotNull(message = "moedaOrigem e obrigatoria") Moeda moedaOrigem,
        @NotNull(message = "moedaDestino e obrigatoria") Moeda moedaDestino,
        @NotNull(message = "valor e obrigatorio") @DecimalMin(value = "0.0", inclusive = false, message = "valor deve ser positivo") BigDecimal valor) {
}
