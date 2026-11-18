package br.com.fiap.amparo.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

/**
 * Lembrete recorrente do cidadao (remedio, consulta, exercicio).
 */
@Entity
@Table(name = "tb_lembrete")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lembrete {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String titulo;

    @Column(length = 255)
    private String descricao;

    @Column(nullable = false)
    private LocalTime horario;

    @Column(nullable = false)
    @Builder.Default
    private Boolean ativo = true;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cidadao_id", nullable = false)
    private Cidadao cidadao;
}
