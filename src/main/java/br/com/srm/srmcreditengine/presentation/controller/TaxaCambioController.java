package br.com.srm.srmcreditengine.presentation.controller;

import br.com.srm.srmcreditengine.business.service.TaxaCambioService;
import br.com.srm.srmcreditengine.persistence.enums.Moeda;
import br.com.srm.srmcreditengine.presentation.dto.TaxaCambioRequisicaoDTO;
import br.com.srm.srmcreditengine.presentation.dto.TaxaCambioRespostaDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/cambio")
@Tag(name = "Cambio", description = "Gestao de taxas de cambio")
public class TaxaCambioController {

    private final TaxaCambioService taxaCambioService;

    public TaxaCambioController(TaxaCambioService servicoDeCambio) {
        this.taxaCambioService = servicoDeCambio;
    }

    @GetMapping("/taxas/listar-tudo")
    @Operation(summary = "Lista todas as taxas de cambio cadastradas")
    public ResponseEntity<List<TaxaCambioRespostaDTO>> listarTaxas() {
        var taxas = taxaCambioService.listarTaxas().stream().map(TaxaCambioRespostaDTO::apartirDe).toList();
        return ResponseEntity.ok(taxas);
    }

    @GetMapping("/taxas/{moedaOrigem}/{moedaDestino}")
    @Operation(summary = "Consulta a taxa de cambio vigente entre duas moedas")
    public ResponseEntity<TaxaCambioRespostaDTO> obterTaxa(
            @PathVariable Moeda moedaOrigem, @PathVariable Moeda moedaDestino) {
        var taxa = taxaCambioService.obterTaxa(moedaOrigem, moedaDestino);
        return ResponseEntity.ok(TaxaCambioRespostaDTO.apartirDe(taxa));
    }

    @PutMapping("/taxas")
    @Operation(summary = "Atualiza (ou cadastra) manualmente uma taxa de cambio")
    public ResponseEntity<TaxaCambioRespostaDTO> atualizarManualmente(@Valid @RequestBody TaxaCambioRequisicaoDTO requisicao) {
        var taxa = taxaCambioService.atualizarTaxaManualmente(
                requisicao.moedaOrigem(), requisicao.moedaDestino(), requisicao.valor());
        return ResponseEntity.ok(TaxaCambioRespostaDTO.apartirDe(taxa));
    }

}
