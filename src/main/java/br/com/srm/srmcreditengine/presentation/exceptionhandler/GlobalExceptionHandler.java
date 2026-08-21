package br.com.srm.srmcreditengine.presentation.exceptionhandler;

import br.com.srm.srmcreditengine.business.exception.RecebivelJaLiquidadoException;
import br.com.srm.srmcreditengine.business.exception.RecursoNaoEncontradoException;
import br.com.srm.srmcreditengine.business.exception.RegraDeNegocioException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErroRespostaDTO> tratarRecursoNaoEncontrado(RecursoNaoEncontradoException ex) {
        return construirResposta(HttpStatus.NOT_FOUND, ex.getMessage(), List.of());
    }

    @ExceptionHandler(RecebivelJaLiquidadoException.class)
    public ResponseEntity<ErroRespostaDTO> tratarRecebivelJaLiquidado(RecebivelJaLiquidadoException ex) {
        return construirResposta(HttpStatus.CONFLICT, ex.getMessage(), List.of());
    }

    @ExceptionHandler(RegraDeNegocioException.class)
    public ResponseEntity<ErroRespostaDTO> tratarRegraDeNegocio(RegraDeNegocioException ex) {
        return construirResposta(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), List.of());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroRespostaDTO> tratarValidacao(MethodArgumentNotValidException ex) {
        List<String> detalhes = ex.getBindingResult().getFieldErrors().stream()
                .map(erro -> erro.getField() + ": " + erro.getDefaultMessage())
                .toList();
        return construirResposta(HttpStatus.BAD_REQUEST, "Dados invalidos na requisicao", detalhes);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroRespostaDTO> tratarErroInesperado(Exception ex) {
        LOGGER.error("Erro inesperado ao processar a requisicao", ex);
        return construirResposta(HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocorreu um erro inesperado. Tente novamente mais tarde.", List.of());
    }

    private ResponseEntity<ErroRespostaDTO> construirResposta(HttpStatus status, String mensagem, List<String> detalhes) {
        ErroRespostaDTO corpo = new ErroRespostaDTO(
                LocalDateTime.now(), status.value(), status.getReasonPhrase(), mensagem, detalhes);
        return ResponseEntity.status(status).body(corpo);
    }
}
