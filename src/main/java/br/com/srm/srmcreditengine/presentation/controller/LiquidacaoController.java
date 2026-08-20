package br.com.srm.srmcreditengine.presentation.controller;

import br.com.srm.srmcreditengine.business.service.LiquidacaoService;
import br.com.srm.srmcreditengine.presentation.dto.LiquidacaoRequisicaoDTO;
import br.com.srm.srmcreditengine.presentation.dto.LiquidacaoRespostaDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recebiveis/{recebivelId}/liquidacoes")
@Tag(name = "Liquidacoes", description = "Liquidacao (antecipacao) de recebiveis")
public class LiquidacaoController {

    private final LiquidacaoService liquidacaoServico;

    public LiquidacaoController(LiquidacaoService liquidacaoServico) {

        this.liquidacaoServico = liquidacaoServico;
    }

    @PostMapping
    @Operation(summary = "Liquida (antecipa) um recebivel, calculando o valor presente e aplicando conversao cambial quando necessario")
    public ResponseEntity<LiquidacaoRespostaDTO> liquidar(
            @PathVariable Long recebivelId, @Valid @RequestBody LiquidacaoRequisicaoDTO requisicao) {
        var liquidacao = liquidacaoServico.liquidar(recebivelId, requisicao.moedaPagamento());
        return ResponseEntity.ok(LiquidacaoRespostaDTO.liquidacaoResposta(liquidacao));
    }

    @GetMapping
    @Operation(summary = "Consulta a liquidacao registrada para um recebivel")
    public ResponseEntity<LiquidacaoRespostaDTO> buscarPorRecebivel(@PathVariable Long recebivelId) {
        var liquidacao = liquidacaoServico.buscarPorRecebivel(recebivelId);
        return ResponseEntity.ok(LiquidacaoRespostaDTO.liquidacaoResposta(liquidacao));
    }
}
