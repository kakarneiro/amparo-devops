package br.com.fiap.amparo.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Pessoa responsavel por responder aos eventos do cidadao.
 * A prioridade define a ordem de acionamento (1 = primeiro a ser chamado).
 */
@Entity
@Table(name = "tb_cuidador")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cuidador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nome;

    @Column(nullable = false, length = 20)
    private String telefone;

    @Column(length = 120)
    private String email;

    /** Ex.: "Filha", "Vizinho", "Cuidador profissional" */
    @Column(length = 60)
    private String parentesco;

    @Column(nullable = false)
    private Integer prioridade;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cidadao_id", nullable = false)
    private Cidadao cidadao;
}
