package br.com.srm.srmcreditengine.presentation.dto;

import jakarta.validation.constraints.NotBlank;

public record CedenteRequisicaoDTO(
        @NotBlank(message = "nome e obrigatorio") String nome,
        @NotBlank(message = "documento e obrigatorio") String documento) {
}
