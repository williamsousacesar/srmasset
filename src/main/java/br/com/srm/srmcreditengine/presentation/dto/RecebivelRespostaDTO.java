package br.com.srm.srmcreditengine.presentation.dto;

import br.com.srm.srmcreditengine.persistence.entities.Recebivel;
import br.com.srm.srmcreditengine.persistence.enums.Moeda;
import br.com.srm.srmcreditengine.persistence.enums.StatusRecebivel;
import br.com.srm.srmcreditengine.persistence.enums.TipoRecebivel;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record RecebivelRespostaDTO(
        Long id,
        Long cedenteId,
        String nomeCedente,
        TipoRecebivel tipo,
        BigDecimal valorFace,
        Moeda moedaTitulo,
        Integer prazoMeses,
        LocalDate dataEmissao,
        LocalDate dataVencimento,
        StatusRecebivel status,
        @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss") LocalDateTime dataInclusao,
        @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss") LocalDateTime dataUltimaAlteracao) {

    public static RecebivelRespostaDTO apartirDe(Recebivel recebivel) {
        return new RecebivelRespostaDTO(
                recebivel.getId(),
                recebivel.getCedente().getId(),
                recebivel.getCedente().getNome(),
                recebivel.getTipo(),
                recebivel.getValorFace(),
                recebivel.getMoedaTitulo(),
                recebivel.getPrazoMeses(),
                recebivel.getDataEmissao(),
                recebivel.getDataVencimento(),
                recebivel.getStatus(),
                recebivel.getDataInclusao(),
                recebivel.getDataUltimaAlteracao());
    }
}
