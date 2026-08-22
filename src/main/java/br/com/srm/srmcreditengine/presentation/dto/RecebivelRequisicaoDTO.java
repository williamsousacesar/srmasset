package br.com.srm.srmcreditengine.presentation.dto;

import br.com.srm.srmcreditengine.persistence.enums.Moeda;
import br.com.srm.srmcreditengine.persistence.enums.TipoRecebivel;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;


public record RecebivelRequisicaoDTO(
        @NotNull(message = "cedenteId e obrigatorio") Long cedenteId,
        @NotNull(message = "tipo e obrigatorio") TipoRecebivel tipo,
        @NotNull(message = "valorFace e obrigatorio") @DecimalMin(value = "0.01", message = "valorFace deve ser positivo") BigDecimal valorFace,
        @NotNull(message = "moedaTitulo e obrigatoria") Moeda moedaTitulo,
        @NotNull(message = "prazoMeses e obrigatorio") @Min(value = 1, message = "prazoMeses deve ser >= 1") Integer prazoMeses,
        @NotNull(message = "dataEmissao e obrigatoria") LocalDate dataEmissao,
        @NotNull(message = "dataVencimento e obrigatoria") @Future(message = "dataVencimento deve ser futura") LocalDate dataVencimento) {
}
