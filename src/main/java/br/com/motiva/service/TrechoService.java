package br.com.motiva.service;

import br.com.motiva.dto.TrechoDto;
import br.com.motiva.exception.RecursoNaoEncontradoException;
import br.com.motiva.exception.RegraNegocioException;
import br.com.motiva.model.TrechoRodovia;
import br.com.motiva.repository.IntervencaoRepository;
import br.com.motiva.repository.TrechoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class TrechoService {

    static final double CRESCIMENTO_UMIDO_CM = 8.0;
    static final double CRESCIMENTO_SECO_CM = 4.0;

    private final TrechoRepository repository;
    private final IntervencaoRepository intervencaoRepository;
    private final MotorPrioridadeService motor;

    private final Random random = new Random();

    public TrechoService(TrechoRepository repository, IntervencaoRepository intervencaoRepository,
                         MotorPrioridadeService motor) {
        this.repository = repository;
        this.intervencaoRepository = intervencaoRepository;
        this.motor = motor;
    }

    public List<TrechoDto.Response> listarTodos() {
        return repository.findAll().stream().map(this::paraResponse).toList();
    }

    public TrechoDto.Response buscarPorId(Long id) {
        return paraResponse(buscarEntidade(id));
    }

    public List<TrechoDto.Response> buscarPorVegetacaoMinima(Double minimo) {
        return repository.findByNivelVegetacaoEmCmGreaterThanEqual(minimo).stream().map(this::paraResponse).toList();
    }

    public List<TrechoDto.Response> buscarPorClima(String clima) {
        return repository.findByTipoClimaIgnoreCase(clima).stream().map(this::paraResponse).toList();
    }

    public TrechoDto.Response criar(TrechoDto.Request dados) {
        validar(dados);
        TrechoRodovia trecho = new TrechoRodovia();
        aplicar(trecho, dados);
        return paraResponse(repository.save(trecho));
    }

    public TrechoDto.Response atualizar(Long id, TrechoDto.Request dados) {
        TrechoRodovia trecho = buscarEntidade(id);
        validar(dados);
        aplicar(trecho, dados);
        return paraResponse(repository.save(trecho));
    }

    public void deletar(Long id) {
        if (!repository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Trecho não encontrado: " + id);
        }
        if (intervencaoRepository.existsByTrechoId(id)) {
            throw new RegraNegocioException("Trecho " + id + " possui intervenções registradas e não pode ser removido");
        }
        repository.deleteById(id);
    }

    public List<TrechoDto.Response> simularCrescimento() {
        List<TrechoRodovia> trechos = repository.findAll();
        for (TrechoRodovia trecho : trechos) {
            trecho.registrarCrescimento(calcularCrescimento(trecho));
        }
        return repository.saveAll(trechos).stream().map(this::paraResponse).toList();
    }

    private double calcularCrescimento(TrechoRodovia trecho) {
        if (!Boolean.TRUE.equals(trecho.getComSensor())) {
            return random.nextInt(1, 15);
        }
        if ("umido".equalsIgnoreCase(trecho.getTipoClima())) {
            return CRESCIMENTO_UMIDO_CM;
        }
        if ("seco".equalsIgnoreCase(trecho.getTipoClima())) {
            return CRESCIMENTO_SECO_CM;
        }
        throw new RegraNegocioException("Tipo de clima inválido: só pode ser 'umido' ou 'seco'");
    }

    private TrechoRodovia buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Trecho não encontrado: " + id));
    }

    private void validar(TrechoDto.Request dados) {
        if (dados.nomeTrecho() == null || dados.nomeTrecho().isBlank()) {
            throw new RegraNegocioException("Nome do trecho é obrigatório");
        }
        String clima = dados.tipoClima();
        if (clima == null || !(clima.equalsIgnoreCase("umido") || clima.equalsIgnoreCase("seco"))) {
            throw new RegraNegocioException("Tipo de clima deve ser 'umido' ou 'seco'");
        }
        Integer kmInicial = dados.quilometroInicial();
        Integer kmFinal = dados.quilometroFinal();
        if (kmInicial == null || kmFinal == null || kmFinal < kmInicial || kmInicial < 0 || kmFinal < 1) {
            throw new RegraNegocioException("Quilometragem inválida: o km final deve ser >= km inicial e maior que zero");
        }
        if (dados.nivelVegetacaoEmCm() == null || dados.nivelVegetacaoEmCm() < 0) {
            throw new RegraNegocioException("Nível de vegetação não pode ser negativo");
        }
    }

    private void aplicar(TrechoRodovia trecho, TrechoDto.Request dados) {
        trecho.setNomeTrecho(dados.nomeTrecho().trim());
        trecho.setQuilometroInicial(dados.quilometroInicial());
        trecho.setQuilometroFinal(dados.quilometroFinal());
        trecho.setNivelVegetacaoEmCm(dados.nivelVegetacaoEmCm());
        trecho.setTipoClima(dados.tipoClima().toLowerCase());
        trecho.setComSensor(Boolean.TRUE.equals(dados.comSensor()));
    }

    private TrechoDto.Response paraResponse(TrechoRodovia t) {
        return new TrechoDto.Response(t.getId(), t.getNomeTrecho(), t.getQuilometroInicial(),
                t.getQuilometroFinal(), t.getNivelVegetacaoEmCm(), t.getTipoClima(), t.getComSensor(),
                motor.classificar(t.getNivelVegetacaoEmCm()));
    }
}
