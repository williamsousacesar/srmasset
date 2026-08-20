package br.com.srm.srmcreditengine.presentation.dto;

import br.com.srm.srmcreditengine.persistence.entities.Recebivel;
import br.com.srm.srmcreditengine.persistence.enums.Moeda;
import br.com.srm.srmcreditengine.persistence.enums.StatusRecebivel;
import br.com.srm.srmcreditengine.persistence.enums.TipoRecebivel;

import java.math.BigDecimal;
import java.time.LocalDate;

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
        StatusRecebivel status) {

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
                recebivel.getStatus());
    }
}
