package br.com.fiap.amparo.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Pessoa assistida pela rede de cuidado (idoso ou pessoa com deficiencia).
 */
@Entity
@Table(name = "tb_cidadao")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cidadao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nome;

    @Column(name = "data_nascimento", nullable = false)
    private LocalDate dataNascimento;

    @Column(length = 20)
    private String telefone;

    @Column(length = 200)
    private String endereco;

    /** Ex.: "Idoso", "PcD - mobilidade reduzida", "Idoso com Alzheimer" */
    @Column(length = 120)
    private String condicao;

    @Column(nullable = false)
    @Builder.Default
    private Boolean ativo = true;

    /**
     * Cascade ALL + orphanRemoval: os dispositivos e cuidadores pertencem ao
     * cidadao; ao remover o cidadao, eles vao junto.
     */
    @OneToMany(mappedBy = "cidadao", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Dispositivo> dispositivos = new ArrayList<>();

    @OneToMany(mappedBy = "cidadao", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Cuidador> cuidadores = new ArrayList<>();
}
