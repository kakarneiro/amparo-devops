package br.com.fiap.amparo.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Acionamento de um cuidador para um evento. Um evento pode gerar varios
 * atendimentos (cascata: cuidador 1 nao respondeu -> escala para o 2).
 */
@Entity
@Table(name = "tb_atendimento")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Atendimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "evento_id", nullable = false)
    private Evento evento;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cuidador_id", nullable = false)
    private Cuidador cuidador;

    @Column(name = "data_acionamento", nullable = false)
    @Builder.Default
    private LocalDateTime dataAcionamento = LocalDateTime.now();

    @Column(name = "data_resposta")
    private LocalDateTime dataResposta;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private ResultadoAtendimento resultado = ResultadoAtendimento.PENDENTE;

    @Column(length = 255)
    private String observacao;
}
