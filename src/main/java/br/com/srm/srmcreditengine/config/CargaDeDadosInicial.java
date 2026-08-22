package br.com.srm.srmcreditengine.config;

import br.com.srm.srmcreditengine.persistence.entities.Cedente;
import br.com.srm.srmcreditengine.persistence.entities.Recebivel;
import br.com.srm.srmcreditengine.persistence.entities.TaxaCambio;
import br.com.srm.srmcreditengine.persistence.enums.Moeda;
import br.com.srm.srmcreditengine.persistence.enums.StatusRecebivel;
import br.com.srm.srmcreditengine.persistence.enums.TipoRecebivel;
import br.com.srm.srmcreditengine.persistence.repository.CedenteRepository;
import br.com.srm.srmcreditengine.persistence.repository.RecebivelRepository;
import br.com.srm.srmcreditengine.persistence.repository.TaxaCambioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Configuration
public class CargaDeDadosInicial {

    @Bean
    public CommandLineRunner popularDadosDeExemplo(
            TaxaCambioRepository taxaCambioRepositorio,
            CedenteRepository cedenteRepositorio,
            RecebivelRepository recebivelRepositorio) {

        return args -> {
            if (taxaCambioRepositorio.count() == 0) {
                taxaCambioRepositorio.save(TaxaCambio.builder()
                        .moedaOrigem(Moeda.BRL)
                        .moedaDestino(Moeda.USD)
                        .valor(new BigDecimal("0.18500000"))
                        .dataAtualizacao(LocalDateTime.now())
                        //.origemAtualizacao("CARGA_INICIAL")
                        .build());
                taxaCambioRepositorio.save(TaxaCambio.builder()
                        .moedaOrigem(Moeda.USD)
                        .moedaDestino(Moeda.BRL)
                        .valor(new BigDecimal("5.40000000"))
                        .dataAtualizacao(LocalDateTime.now())
                        //.origemAtualizacao("CARGA_INICIAL")
                        .build());
            }

            if (cedenteRepositorio.count() == 0) {
                Cedente cedente = cedenteRepositorio.save(Cedente.builder()
                        .nome("Comercial Exemplo Ltda")
                        .documento("12.345.678/0001-90")
                        .dataInclusao(LocalDateTime.now())
                        .build());

                recebivelRepositorio.save(Recebivel.builder()
                        .cedente(cedente)
                        .tipo(TipoRecebivel.DUPLICATA_MERCANTIL)
                        .valorFace(new BigDecimal("10000.00"))
                        .moedaTitulo(Moeda.BRL)
                        .prazoMeses(3)
                        .dataEmissao(LocalDate.now())
                        .dataVencimento(LocalDate.now().plusMonths(3))
                        .status(StatusRecebivel.PENDENTE)
                        .build());

                recebivelRepositorio.save(Recebivel.builder()
                        .cedente(cedente)
                        .tipo(TipoRecebivel.CHEQUE_PRE_DATADO)
                        .valorFace(new BigDecimal("5000.00"))
                        .moedaTitulo(Moeda.BRL)
                        .prazoMeses(2)
                        .dataEmissao(LocalDate.now())
                        .dataVencimento(LocalDate.now().plusMonths(2))
                        .status(StatusRecebivel.PENDENTE)
                        .build());
            }
        };
    }
}
