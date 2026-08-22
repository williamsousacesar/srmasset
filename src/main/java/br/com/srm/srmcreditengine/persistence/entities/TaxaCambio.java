package br.com.srm.srmcreditengine.persistence.entities;

import br.com.srm.srmcreditengine.persistence.enums.Moeda;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "taxa_cambio", uniqueConstraints = @UniqueConstraint(columnNames = {"moeda_origem", "moeda_destino"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaxaCambio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "moeda_origem", nullable = false, length = 3)
    private Moeda moedaOrigem;

    @Enumerated(EnumType.STRING)
    @Column(name = "moeda_destino", nullable = false, length = 3)
    private Moeda moedaDestino;

    @Column(nullable = false, precision = 19, scale = 8)
    private BigDecimal valor;

    @Column(name = "data_atualizacao", nullable = false)
    private LocalDateTime dataAtualizacao;

}
