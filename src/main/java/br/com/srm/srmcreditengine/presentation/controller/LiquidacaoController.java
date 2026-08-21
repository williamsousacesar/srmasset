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
@RequestMapping("/api/liquidacoes")
@Tag(name = "Liquidacoes", description = "Liquidacao (antecipacao) de recebiveis")
public class LiquidacaoController {

    private final LiquidacaoService liquidacaoService;

    public LiquidacaoController(LiquidacaoService liquidacaoService) {

        this.liquidacaoService = liquidacaoService;
    }

    @PostMapping("/liquidar")
    @Operation(summary = "Liquida (antecipa) um recebivel, calculando o valor presente e aplicando conversao cambial quando necessario")
    public ResponseEntity<LiquidacaoRespostaDTO> liquidar(@Valid @RequestBody LiquidacaoRequisicaoDTO requisicao) {
        var liquidacao = liquidacaoService.liquidar(requisicao.recebivelId(), requisicao.moedaPagamento());
        return ResponseEntity.ok(LiquidacaoRespostaDTO.liquidacaoResposta(liquidacao));
    }

    @GetMapping("/recebivel/{recebivelId}")
    @Operation(summary = "Consulta a liquidacao registrada para um recebivel")
    public ResponseEntity<LiquidacaoRespostaDTO> buscarPorRecebivel(@PathVariable Long recebivelId) {
        var liquidacao = liquidacaoService.buscarPorRecebivel(recebivelId);
        return ResponseEntity.ok(LiquidacaoRespostaDTO.liquidacaoResposta(liquidacao));
    }
}
