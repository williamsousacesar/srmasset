package br.com.srm.srmcreditengine.business.service;

import br.com.srm.srmcreditengine.business.exception.RecebivelJaLiquidadoException;
import br.com.srm.srmcreditengine.business.exception.RecursoNaoEncontradoException;
import br.com.srm.srmcreditengine.persistence.entities.Liquidacao;
import br.com.srm.srmcreditengine.persistence.entities.Recebivel;
import br.com.srm.srmcreditengine.persistence.entities.TaxaCambio;
import br.com.srm.srmcreditengine.persistence.enums.Moeda;
import br.com.srm.srmcreditengine.persistence.enums.StatusRecebivel;
import br.com.srm.srmcreditengine.persistence.repository.LiquidacaoRepository;
import br.com.srm.srmcreditengine.persistence.repository.RecebivelRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class LiquidacaoService {

    private final RecebivelRepository recebivelRepository;
    private final LiquidacaoRepository liquidacaoRepository;
    private final CalculoValorPresenteService motorDePrecificacaoService;
    private final TaxaCambioService taxaCambioService;
    private final ProvedorDeTaxaBase provedorDeTaxaBase;

    public LiquidacaoService(RecebivelRepository recebivelRepository, LiquidacaoRepository liquidacaoRepository,
                             CalculoValorPresenteService motorDePrecificacaoService, TaxaCambioService taxaCambioService,
                             ProvedorDeTaxaBase provedorDeTaxaBase) {
        this.recebivelRepository = recebivelRepository;
        this.liquidacaoRepository = liquidacaoRepository;
        this.motorDePrecificacaoService = motorDePrecificacaoService;
        this.taxaCambioService = taxaCambioService;
        this.provedorDeTaxaBase = provedorDeTaxaBase;
    }


    @Transactional(propagation = Propagation.REQUIRED, isolation = org.springframework.transaction.annotation.Isolation.READ_COMMITTED)
    public Liquidacao liquidar(Long recebivelId, Moeda moedaPagamento) {
        Recebivel recebivel = recebivelRepository.buscarComBloqueioPorId(recebivelId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Recebivel nao encontrado: id " + recebivelId));

        if (recebivel.getStatus() == StatusRecebivel.LIQUIDADO) {
            throw new RecebivelJaLiquidadoException(recebivelId);
        }

        BigDecimal taxaBaseMensal = provedorDeTaxaBase.obterTaxaBaseMensal();
        int prazoMeses = calcularPrazoRestanteEmMeses(recebivel);

        BigDecimal calcularValorPresente = motorDePrecificacaoService.calcularValorPresente(
                recebivel.getValorFace(), recebivel.getTipo(), taxaBaseMensal, prazoMeses);

        BigDecimal valorPresenteMoedaPagamento = calcularValorPresente;
        BigDecimal taxaCambioUtilizada = BigDecimal.ONE;

        boolean crossCurrency = recebivel.getMoedaTitulo() != moedaPagamento;
        if (crossCurrency) {
            TaxaCambio taxaCambio = taxaCambioService.obterTaxa(recebivel.getMoedaTitulo(), moedaPagamento);
             taxaCambioUtilizada = taxaCambio.getValor();
             valorPresenteMoedaPagamento = calcularValorPresente
                    .multiply(taxaCambioUtilizada)
                    .setScale(2, RoundingMode.HALF_UP);
        }

        recebivel.setStatus(StatusRecebivel.LIQUIDADO);
        recebivelRepository.save(recebivel);

        Liquidacao liquidacao = Liquidacao.builder()
                .recebivel(recebivel)
                .valorPresenteMoedaTitulo(calcularValorPresente)
                .moedaPagamento(moedaPagamento)
                .valorPresenteMoedaPagamento(valorPresenteMoedaPagamento)
                .taxaCambioUtilizada(taxaCambioUtilizada)
                .spreadAplicado(motorDePrecificacaoService.obterSpreadDoTipo(recebivel.getTipo()))
                .taxaBaseAplicada(taxaBaseMensal)
                .dataLiquidacao(LocalDateTime.now())
                .build();

        return liquidacaoRepository.save(liquidacao);
    }

    @Transactional(readOnly = true)
    public Liquidacao buscarPorRecebivel(Long recebivelId) {
        return liquidacaoRepository.findByRecebivelId(recebivelId)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Nao ha liquidacao registrada para o recebivel de id " + recebivelId));
    }

    private int calcularPrazoRestanteEmMeses(Recebivel recebivel) {
        long diasRestantes = ChronoUnit.DAYS.between(java.time.LocalDate.now(), recebivel.getDataVencimento());
        long mesesRestantes = Math.max(1, Math.round(diasRestantes / 30.0));
        return (int) mesesRestantes;
    }
}
