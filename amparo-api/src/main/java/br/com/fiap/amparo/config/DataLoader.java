package br.com.fiap.amparo.config;

import br.com.fiap.amparo.model.*;
import br.com.fiap.amparo.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Carga inicial para demonstracao. Roda nos perfis dev (H2) e docker (PostgreSQL em container)
 * e e idempotente: so insere se o banco estiver vazio, entao reiniciar o container nao duplica nada.
 * A senha dos usuarios e codificada com BCrypt em tempo de execucao.
 */
@Component
@Profile({"dev", "docker"})
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final CidadaoRepository cidadaoRepository;
    private final CuidadorRepository cuidadorRepository;
    private final DispositivoRepository dispositivoRepository;
    private final LembreteRepository lembreteRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (usuarioRepository.count() == 0) {
            usuario("Administrador", "admin@amparo.com", "admin123", Papel.ADMIN);
            usuario("Ana Paula (cuidadora)", "ana@amparo.com", "ana123", Papel.CUIDADOR);
        }

        if (cidadaoRepository.count() > 0) return;

        Cidadao maria = cidadaoRepository.save(Cidadao.builder()
                .nome("Maria das Dores").dataNascimento(LocalDate.of(1948, 3, 12))
                .telefone("11988880001").endereco("Rua das Flores, 120 - Sao Paulo")
                .condicao("Idosa, mobilidade reduzida").ativo(true).build());

        Cidadao jose = cidadaoRepository.save(Cidadao.builder()
                .nome("Jose Carlos Lima").dataNascimento(LocalDate.of(1952, 7, 30))
                .telefone("11988880002").endereco("Av. Brasil, 900 - Guarulhos")
                .condicao("Idoso com Alzheimer inicial").ativo(true).build());

        cuidador("Ana Paula", "11977770001", "ana@email.com", "Filha", 1, maria);
        cuidador("Seu Roberto", "11977770002", "roberto@email.com", "Vizinho", 2, maria);
        cuidador("Carla Lima", "11977770003", "carla@email.com", "Esposa", 1, jose);

        dispositivo("ESP32-0001", maria);
        dispositivo("ESP32-0002", jose);

        lembrete("Remedio da pressao", "Losartana 50mg", LocalTime.of(8, 0), maria);
        lembrete("Caminhada", "Caminhar 15 minutos no quintal", LocalTime.of(16, 0), jose);
    }

    private void usuario(String nome, String email, String senha, Papel papel) {
        usuarioRepository.save(Usuario.builder()
                .nome(nome).email(email).senha(passwordEncoder.encode(senha))
                .papel(papel).ativo(true).build());
    }

    private void cuidador(String nome, String tel, String email, String parentesco, int prioridade, Cidadao c) {
        cuidadorRepository.save(Cuidador.builder()
                .nome(nome).telefone(tel).email(email).parentesco(parentesco)
                .prioridade(prioridade).cidadao(c).build());
    }

    private void dispositivo(String serial, Cidadao c) {
        dispositivoRepository.save(Dispositivo.builder()
                .codigoSerial(serial).modelo("Amparo v1 - ESP32 + MPU6050").ativo(true).cidadao(c).build());
    }

    private void lembrete(String titulo, String descricao, LocalTime horario, Cidadao c) {
        lembreteRepository.save(Lembrete.builder()
                .titulo(titulo).descricao(descricao).horario(horario).ativo(true).cidadao(c).build());
    }
}
