package br.com.motiva.service;

import br.com.motiva.dto.EquipeDto;
import br.com.motiva.exception.RecursoNaoEncontradoException;
import br.com.motiva.exception.RegraNegocioException;
import br.com.motiva.model.EquipeManutencao;
import br.com.motiva.repository.EquipeRepository;
import br.com.motiva.repository.IntervencaoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EquipeService {

    private final EquipeRepository repository;
    private final IntervencaoRepository intervencaoRepository;

    public EquipeService(EquipeRepository repository, IntervencaoRepository intervencaoRepository) {
        this.repository = repository;
        this.intervencaoRepository = intervencaoRepository;
    }

    public List<EquipeDto.Response> listarTodos() {
        return repository.findAll().stream().map(this::paraResponse).toList();
    }

    public EquipeDto.Response buscarPorId(Long id) {
        return paraResponse(buscarEntidade(id));
    }

    public List<EquipeDto.Response> buscarPorRocada(String tipo) {
        return repository.findByTipoDeRocadaDeAtuacaoIgnoreCase(tipo).stream().map(this::paraResponse).toList();
    }

    public EquipeDto.Response criar(EquipeDto.Request dados) {
        validar(dados);
        EquipeManutencao equipe = new EquipeManutencao();
        aplicar(equipe, dados);
        return paraResponse(repository.save(equipe));
    }

    public EquipeDto.Response atualizar(Long id, EquipeDto.Request dados) {
        EquipeManutencao equipe = buscarEntidade(id);
        validar(dados);
        aplicar(equipe, dados);
        return paraResponse(repository.save(equipe));
    }

    public void deletar(Long id) {
        if (!repository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Equipe não encontrada: " + id);
        }
        if (intervencaoRepository.existsByEquipeId(id)) {
            throw new RegraNegocioException("Equipe " + id + " possui intervenções registradas e não pode ser removida");
        }
        repository.deleteById(id);
    }

    private EquipeManutencao buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Equipe não encontrada: " + id));
    }

    private void validar(EquipeDto.Request dados) {
        if (dados.nomeEquipe() == null || dados.nomeEquipe().isBlank()) {
            throw new RegraNegocioException("Nome da equipe é obrigatório");
        }
        if (dados.numeroFuncionarios() == null || dados.numeroFuncionarios() <= 0) {
            throw new RegraNegocioException("A equipe deve ter ao menos 1 funcionário");
        }
        String tipo = dados.tipoDeRocadaDeAtuacao();
        if (tipo == null || !(tipo.equalsIgnoreCase("manual") || tipo.equalsIgnoreCase("mecanizada"))) {
            throw new RegraNegocioException("Tipo de roçada deve ser 'manual' ou 'mecanizada'");
        }
    }

    private void aplicar(EquipeManutencao equipe, EquipeDto.Request dados) {
        equipe.setNomeEquipe(dados.nomeEquipe().trim());
        equipe.setNumeroFuncionarios(dados.numeroFuncionarios());
        equipe.setTipoDeRocadaDeAtuacao(dados.tipoDeRocadaDeAtuacao().toLowerCase());
    }

    private EquipeDto.Response paraResponse(EquipeManutencao e) {
        return new EquipeDto.Response(e.getId(), e.getNomeEquipe(), e.getNumeroFuncionarios(),
                e.getTipoDeRocadaDeAtuacao());
    }
}
