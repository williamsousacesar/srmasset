package br.com.srm.srmcreditengine.presentation.controller;

import br.com.srm.srmcreditengine.business.service.RecebivelService;
import br.com.srm.srmcreditengine.presentation.dto.RecebivelRequisicaoDTO;
import br.com.srm.srmcreditengine.presentation.dto.RecebivelRespostaDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/recebiveis")
@Tag(name = "Recebiveis", description = "Cadastro e consulta de recebiveis")
public class RecebivelController {

    private final RecebivelService recebivelServico;

    public RecebivelController(RecebivelService recebivelServico) {
        this.recebivelServico = recebivelServico;
    }

    @PostMapping("/cadastrar")
    @Operation(summary = "Cadastra um novo recebivel")
    public ResponseEntity<RecebivelRespostaDTO> cadastrar(@Valid @RequestBody RecebivelRequisicaoDTO requisicao) {
        var recebivel = recebivelServico.cadastrar(
                requisicao.cedenteId(),
                requisicao.tipo(),
                requisicao.valorFace(),
                requisicao.moedaTitulo(),
                requisicao.prazoMeses(),
                requisicao.dataEmissao(),
                requisicao.dataVencimento());
        var resposta = RecebivelRespostaDTO.apartirDe(recebivel);
        return ResponseEntity.created(URI.create("/api/recebiveis/" + recebivel.getId())).body(resposta);
    }

    @GetMapping("/listar-tudo")
    @Operation(summary = "Lista todos os recebiveis cadastrados")
    public ResponseEntity<List<RecebivelRespostaDTO>> listarTodos() {
        var recebiveis = recebivelServico.listarTodos().stream().map(RecebivelRespostaDTO::apartirDe).toList();
        return ResponseEntity.ok(recebiveis);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um recebivel pelo id")
    public ResponseEntity<RecebivelRespostaDTO> buscarPorId(@PathVariable Long id) {
        var recebivel = recebivelServico.buscarPorId(id);
        return ResponseEntity.ok(RecebivelRespostaDTO.apartirDe(recebivel));
    }
}
