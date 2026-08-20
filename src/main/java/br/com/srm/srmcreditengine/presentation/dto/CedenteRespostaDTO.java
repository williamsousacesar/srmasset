package br.com.srm.srmcreditengine.presentation.dto;

import br.com.srm.srmcreditengine.persistence.entities.Cedente;

public record CedenteRespostaDTO(Long id, String nome, String documento) {

    public static CedenteRespostaDTO cedenteResposta(Cedente cedente) {
        return new CedenteRespostaDTO(cedente.getId(), cedente.getNome(), cedente.getDocumento());
    }
}
