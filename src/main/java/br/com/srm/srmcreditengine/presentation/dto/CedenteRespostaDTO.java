package br.com.srm.srmcreditengine.presentation.dto;

import br.com.srm.srmcreditengine.persistence.entities.Cedente;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record CedenteRespostaDTO(Long id, String nome, String documento,
                                  @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss") LocalDateTime dataInclusao) {

    public static CedenteRespostaDTO cedenteResposta(Cedente cedente) {
        return new CedenteRespostaDTO(cedente.getId(), cedente.getNome(), cedente.getDocumento(), cedente.getDataInclusao());
    }
}
