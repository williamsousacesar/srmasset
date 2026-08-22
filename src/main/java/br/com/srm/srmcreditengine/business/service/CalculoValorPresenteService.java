package br.com.srm.srmcreditengine.business.service;

import br.com.srm.srmcreditengine.business.exception.RegraDeNegocioException;
import br.com.srm.srmcreditengine.business.strategy.EstrategiaDePrecificacao;
import br.com.srm.srmcreditengine.persistence.enums.TipoRecebivel;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CalculoValorPresenteService {

    private static final MathContext CONTEXTO_MATEMATICO = new MathContext(12);

    private final Map<TipoRecebivel, EstrategiaDePrecificacao> estrategiasPorTipo;

    public CalculoValorPresenteService(List<EstrategiaDePrecificacao> estrategias) {
        this.estrategiasPorTipo = estrategias.stream()
                .collect(Collectors.toMap(EstrategiaDePrecificacao::obterTipoSuportado, Function.identity()));
    }

    public BigDecimal calcularValorPresente(BigDecimal valorFace, TipoRecebivel tipo,
            BigDecimal taxaBaseMensal, int prazoMeses) {

        EstrategiaDePrecificacao estrategia = obterEstrategia(tipo);
        BigDecimal spreadMensal = estrategia.obterSpreadMensal();
        BigDecimal taxaTotalMensal = taxaBaseMensal.add(spreadMensal);

        BigDecimal fatorDesconto = BigDecimal.ONE.add(taxaTotalMensal).pow(prazoMeses, CONTEXTO_MATEMATICO);

        return valorFace.divide(fatorDesconto, 2, RoundingMode.HALF_UP);
    }

    public BigDecimal obterSpreadDoTipo(TipoRecebivel tipo) {

        return obterEstrategia(tipo).obterSpreadMensal();
    }

    private EstrategiaDePrecificacao obterEstrategia(TipoRecebivel tipo) {
        EstrategiaDePrecificacao estrategia = estrategiasPorTipo.get(tipo);
        if (estrategia == null) {
            throw new RegraDeNegocioException(
                    "Nao ha estrategia de precificacao cadastrada para o tipo de recebivel: " + tipo);
        }
        return estrategia;
    }
}
