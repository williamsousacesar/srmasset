package br.com.srm.srmcreditengine.persistence.entities;

import br.com.srm.srmcreditengine.persistence.enums.Moeda;
import br.com.srm.srmcreditengine.persistence.enums.StatusRecebivel;
import br.com.srm.srmcreditengine.persistence.enums.TipoRecebivel;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "recebivel")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Recebivel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cedente_id", nullable = false)
    private Cedente cedente;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipoRecebivel tipo;

    @Column(name = "valor_face", nullable = false, precision = 19, scale = 2)
    private BigDecimal valorFace;

    @Enumerated(EnumType.STRING)
    @Column(name = "moeda_titulo", nullable = false, length = 3)
    private Moeda moedaTitulo;

    @Column(name = "prazo_meses", nullable = false)
    private Integer prazoMeses;

    @Column(name = "data_emissao", nullable = false)
    private LocalDate dataEmissao;

    @Column(name = "data_vencimento", nullable = false)
    private LocalDate dataVencimento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusRecebivel status;

    @Version
    @Column(name = "versao")
    private Long versao;
}
