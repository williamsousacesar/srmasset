package br.com.srm.srmcreditengine.relatorio.controller;

import br.com.srm.srmcreditengine.relatorio.dto.ExtratoLiquidacaoRespostaDTO;
import br.com.srm.srmcreditengine.relatorio.dto.FiltroExtratoLiquidacaoDTO;
import br.com.srm.srmcreditengine.relatorio.repository.ExtratoLiquidacaoRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/relatorios")
@Tag(name = "Relatorios", description = "Consultas analiticas de liquidacao")
public class ExtratoLiquidacaoController {

    private final ExtratoLiquidacaoRepository extratoLiquidacaoRepositoy;

    public ExtratoLiquidacaoController(ExtratoLiquidacaoRepository extratoLiquidacaoRepositorio) {
        this.extratoLiquidacaoRepositoy = extratoLiquidacaoRepositorio;
    }

    @GetMapping("/extrato-liquidacao")
    @Operation(summary = "Extrato de Liquidacao",
            description = "Consulta liquidacoes com filtros por periodo, cedente e moeda, com paginacao otimizada via SQL nativo")
    public ResponseEntity<ExtratoLiquidacaoRespostaDTO> consultarExtrato(
            @Parameter(description = "Data inicial (inclusive)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @Parameter(description = "Data final (inclusive)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @Parameter(description = "Id do cedente") @RequestParam(required = false) Long cedenteId,
            @Parameter(description = "Moeda de pagamento (ex: BRL, USD)") @RequestParam(required = false) String moeda,
            @Parameter(description = "Numero da pagina (0-based)") @RequestParam(defaultValue = "0") int pagina,
            @Parameter(description = "Tamanho da pagina") @RequestParam(defaultValue = "20") int tamanho) {

        FiltroExtratoLiquidacaoDTO filtro = new FiltroExtratoLiquidacaoDTO(
                dataInicio, dataFim, cedenteId, moeda, Math.max(pagina, 0), Math.max(tamanho, 1));

        var conteudo = extratoLiquidacaoRepositoy.buscarExtrato(filtro);
        long totalDeElementos = extratoLiquidacaoRepositoy.contarExtrato(filtro);
        int totalDePaginas = (int) Math.ceil((double) totalDeElementos / filtro.tamanho());

        var resposta = new ExtratoLiquidacaoRespostaDTO(
                conteudo, filtro.pagina(), filtro.tamanho(), totalDeElementos, totalDePaginas);

        return ResponseEntity.ok(resposta);
    }
}
