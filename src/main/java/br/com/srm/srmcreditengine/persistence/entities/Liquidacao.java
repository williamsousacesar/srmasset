package br.com.srm.srmcreditengine.persistence.entities;

import br.com.srm.srmcreditengine.persistence.enums.Moeda;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "liquidacao")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Liquidacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recebivel_id", nullable = false, unique = true)
    private Recebivel recebivel;

    @Column(name = "valor_presente_moeda_titulo", nullable = false, precision = 19, scale = 2)
    private BigDecimal valorPresenteMoedaTitulo;

    @Enumerated(EnumType.STRING)
    @Column(name = "moeda_pagamento", nullable = false, length = 3)
    private Moeda moedaPagamento;

    @Column(name = "valor_presente_moeda_pagamento", nullable = false, precision = 19, scale = 2)
    private BigDecimal valorPresenteMoedaPagamento;

    @Column(name = "taxa_cambio_utilizada", precision = 19, scale = 8)
    private BigDecimal taxaCambioUtilizada;

    @Column(name = "spread_aplicado", nullable = false, precision = 9, scale = 6)
    private BigDecimal spreadAplicado;

    @Column(name = "taxa_base_aplicada", nullable = false, precision = 9, scale = 6)
    private BigDecimal taxaBaseAplicada;

    @Column(name = "data_liquidacao", nullable = false)
    private LocalDateTime dataLiquidacao;
}
