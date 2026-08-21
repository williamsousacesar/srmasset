package br.com.srm.srmcreditengine.persistence.entities;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

@Entity
@Table(name = "cedente")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cedente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true, length = 20)
    private String documento;

    @Column(name = "data_inclusao", updatable = false)
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime dataInclusao;

    @PrePersist
    private void prePersist() {
        this.dataInclusao = LocalDateTime.now();
    }
}
