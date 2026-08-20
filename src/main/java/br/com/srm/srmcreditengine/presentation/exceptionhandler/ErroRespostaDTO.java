package br.com.srm.srmcreditengine.presentation.exceptionhandler;

import java.time.LocalDateTime;
import java.util.List;

public record ErroRespostaDTO(
        LocalDateTime instante,
        int status,
        String erro,
        String mensagem,
        List<String> detalhes) {
}
