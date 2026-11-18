package br.com.fiap.amparo.service;

import br.com.fiap.amparo.dto.DispositivoRequest;
import br.com.fiap.amparo.dto.DispositivoResponse;
import br.com.fiap.amparo.exception.ResourceNotFoundException;
import br.com.fiap.amparo.model.Dispositivo;
import br.com.fiap.amparo.repository.DispositivoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DispositivoService {

    private final DispositivoRepository repository;
    private final CidadaoService cidadaoService;

    @Transactional(readOnly = true)
    public Page<DispositivoResponse> listar(Pageable pageable) {
        return repository.findAll(pageable).map(DispositivoResponse::from);
    }

    @Transactional(readOnly = true)
    public DispositivoResponse buscar(Long id) {
        return DispositivoResponse.from(obter(id));
    }

    @Transactional
    public DispositivoResponse criar(DispositivoRequest req) {
        Dispositivo d = new Dispositivo();
        aplicar(d, req);
        return DispositivoResponse.from(repository.save(d));
    }

    @Transactional
    public DispositivoResponse atualizar(Long id, DispositivoRequest req) {
        Dispositivo d = obter(id);
        aplicar(d, req);
        return DispositivoResponse.from(repository.save(d));
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(obter(id));
    }

    @Transactional(readOnly = true)
    public Dispositivo obter(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dispositivo", id));
    }

    @Transactional(readOnly = true)
    public Dispositivo obterPorSerial(String codigoSerial) {
        return repository.findByCodigoSerial(codigoSerial)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Dispositivo com serial " + codigoSerial + " nao encontrado"));
    }

    private void aplicar(Dispositivo d, DispositivoRequest req) {
        d.setCodigoSerial(req.codigoSerial());
        d.setModelo(req.modelo());
        d.setAtivo(req.ativo() != null ? req.ativo() : Boolean.TRUE);
        d.setCidadao(cidadaoService.obter(req.cidadaoId()));
    }
}
