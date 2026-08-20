package br.com.srm.srmcreditengine.presentation.controller;

import br.com.srm.srmcreditengine.business.service.CedenteService;
import br.com.srm.srmcreditengine.presentation.dto.CedenteRequisicaoDTO;
import br.com.srm.srmcreditengine.presentation.dto.CedenteRespostaDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/cedentes")
@Tag(name = "Cedentes", description = "Cadastro e consulta de cedentes")
public class CedenteController {

    private final CedenteService cedenteServico;

    public CedenteController(CedenteService cedenteServico) {

        this.cedenteServico = cedenteServico;
    }

    @PostMapping
    @Operation(summary = "Cadastra um novo cedente")
    public ResponseEntity<CedenteRespostaDTO> cadastrar(@Valid @RequestBody CedenteRequisicaoDTO requisicao) {
        var cedente = cedenteServico.cadastrar(requisicao.nome(), requisicao.documento());
        var resposta = CedenteRespostaDTO.cedenteResposta(cedente);
        return ResponseEntity.created(URI.create("/api/cedentes/" + cedente.getId())).body(resposta);
    }

    @GetMapping
    @Operation(summary = "Lista todos os cedentes cadastrados")
    public ResponseEntity<List<CedenteRespostaDTO>> listarTodos() {
        var cedentes = cedenteServico.listarTodos().stream().map(CedenteRespostaDTO::cedenteResposta).toList();
        return ResponseEntity.ok(cedentes);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um cedente pelo id")
    public ResponseEntity<CedenteRespostaDTO> buscarPorId(@PathVariable Long id) {
        var cedente = cedenteServico.buscarPorId(id);
        return ResponseEntity.ok(CedenteRespostaDTO.cedenteResposta(cedente));
    }
}