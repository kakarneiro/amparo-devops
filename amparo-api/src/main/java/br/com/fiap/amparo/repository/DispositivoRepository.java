package br.com.fiap.amparo.repository;

import br.com.fiap.amparo.model.Dispositivo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DispositivoRepository extends JpaRepository<Dispositivo, Long> {

    Optional<Dispositivo> findByCodigoSerial(String codigoSerial);
}
