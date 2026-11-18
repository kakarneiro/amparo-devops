package br.com.fiap.amparo.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Dispositivo IoT (ESP32) usado pelo cidadao: botao de emergencia + acelerometro.
 */
@Entity
@Table(name = "tb_dispositivo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Dispositivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo_serial", nullable = false, unique = true, length = 60)
    private String codigoSerial;

    @Column(length = 60)
    private String modelo;

    @Column(nullable = false)
    @Builder.Default
    private Boolean ativo = true;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cidadao_id", nullable = false)
    private Cidadao cidadao;
}
