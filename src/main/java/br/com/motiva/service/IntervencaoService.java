package br.com.motiva.service;

import br.com.motiva.dto.IntervencaoDto;
import br.com.motiva.exception.RecursoNaoEncontradoException;
import br.com.motiva.exception.RegraNegocioException;
import br.com.motiva.factory.IntervencaoFactory;
import br.com.motiva.model.EquipeManutencao;
import br.com.motiva.model.IntervencaoOperacional;
import br.com.motiva.model.TrechoRodovia;
import br.com.motiva.repository.EquipeRepository;
import br.com.motiva.repository.IntervencaoRepository;
import br.com.motiva.repository.TrechoRepository;
import br.com.motiva.servico.ServicoIntervencao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class IntervencaoService {

    private final IntervencaoRepository repository;
    private final TrechoRepository trechoRepository;
    private final EquipeRepository equipeRepository;
    private final IntervencaoFactory factory;
    private final Random random = new Random();

    public IntervencaoService(IntervencaoRepository repository, TrechoRepository trechoRepository,
                              EquipeRepository equipeRepository, IntervencaoFactory factory) {
        this.repository = repository;
        this.trechoRepository = trechoRepository;
        this.equipeRepository = equipeRepository;
        this.factory = factory;
    }

    public List<IntervencaoDto.Response> listarTodos() {
        return repository.findAll().stream().map(this::paraResponse).toList();
    }

    public IntervencaoDto.Response buscarPorId(Long id) {
        return paraResponse(buscarEntidade(id));
    }

    public List<IntervencaoDto.Response> buscarPorEquipe(String nomeEquipe) {
        return repository.findByNomeEquipe(nomeEquipe).stream().map(this::paraResponse).toList();
    }

    @Transactional
    public IntervencaoDto.Response criar(IntervencaoDto.Request dados) {
        TrechoRodovia trecho = buscarTrecho(dados.trechoId());
        EquipeManutencao equipe = buscarEquipe(dados.equipeId());
        ServicoIntervencao servico = factory.criarPorClima(trecho.getTipoClima());
        validarCompatibilidade(servico, equipe);
        return paraResponse(executar(trecho, equipe));
    }

    @Transactional
    public IntervencaoDto.Response atualizar(Long id, IntervencaoDto.Request dados) {
        IntervencaoOperacional intervencao = buscarEntidade(id);
        TrechoRodovia novoTrecho = buscarTrecho(dados.trechoId());
        EquipeManutencao novaEquipe = buscarEquipe(dados.equipeId());
        validarCompatibilidade(factory.criarPorClima(novoTrecho.getTipoClima()), novaEquipe);

        boolean trocouDeTrecho = !novoTrecho.getId().equals(intervencao.getTrechoId());
        if (trocouDeTrecho) {
            desfazerCorte(intervencao);
            intervencao.setNivelVegetacaoAntesCm(novoTrecho.getNivelVegetacaoEmCm());
            intervencao.setDataGeracao(LocalDateTime.now());
            cortar(novoTrecho);
        }
        preencher(intervencao, novoTrecho, novaEquipe);
        return paraResponse(repository.save(intervencao));
    }

    public void deletar(Long id) {
        if (!repository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Intervenção não encontrada: " + id);
        }
        repository.deleteById(id);
    }

    @Transactional
    public IntervencaoDto.GeracaoResultado gerarIntervencoes() {
        List<IntervencaoDto.Response> criadas = new ArrayList<>();
        List<String> mensagens = new ArrayList<>();
        List<String> avisos = new ArrayList<>();

        List<TrechoRodovia> trechos =
                trechoRepository.findByNivelVegetacaoEmCmGreaterThanEqual(MotorPrioridadeService.LIMITE_INTERVENCAO_CM);

        for (TrechoRodovia trecho : trechos) {
            ServicoIntervencao servico;
            try {
                servico = factory.criarPorClima(trecho.getTipoClima());
            } catch (RegraNegocioException e) {
                avisos.add("Trecho " + trecho.getNomeTrecho() + ": " + e.getMessage());
                continue;
            }

            List<EquipeManutencao> compativeis =
                    equipeRepository.findByTipoDeRocadaDeAtuacaoIgnoreCase(servico.tipoRocadaExigida());
            if (compativeis.isEmpty()) {
                avisos.add("Nenhuma equipe '" + servico.tipoRocadaExigida()
                        + "' disponível para o trecho " + trecho.getNomeTrecho());
                continue;
            }

            EquipeManutencao equipe = compativeis.get(random.nextInt(compativeis.size()));
            mensagens.add(servico.descreverServico(trecho, equipe));
            criadas.add(paraResponse(executar(trecho, equipe)));
        }

        if (criadas.isEmpty() && avisos.isEmpty()) {
            avisos.add("Não houve a necessidade de nenhuma intervenção.");
        }
        return new IntervencaoDto.GeracaoResultado(criadas, mensagens, avisos);
    }

    private IntervencaoOperacional executar(TrechoRodovia trecho, EquipeManutencao equipe) {
        IntervencaoOperacional intervencao = new IntervencaoOperacional();
        preencher(intervencao, trecho, equipe);
        intervencao.setNivelVegetacaoAntesCm(trecho.getNivelVegetacaoEmCm());
        intervencao.setDataGeracao(LocalDateTime.now());
        IntervencaoOperacional salva = repository.save(intervencao);
        cortar(trecho);
        return salva;
    }

    private void cortar(TrechoRodovia trecho) {
        trecho.realizarCorteVegetacao();
        trechoRepository.save(trecho);
    }

    private void desfazerCorte(IntervencaoOperacional intervencao) {
        Double alturaAntes = intervencao.getNivelVegetacaoAntesCm();
        if (alturaAntes == null) {
            return;
        }
        trechoRepository.findById(intervencao.getTrechoId()).ifPresent(antigo -> {
            boolean continuaCortado = antigo.getNivelVegetacaoEmCm() != null
                    && Double.compare(antigo.getNivelVegetacaoEmCm(), TrechoRodovia.ALTURA_POS_CORTE) == 0;
            if (continuaCortado) {
                antigo.setNivelVegetacaoEmCm(alturaAntes);
                trechoRepository.save(antigo);
            }
        });
    }

    private void preencher(IntervencaoOperacional intervencao, TrechoRodovia trecho, EquipeManutencao equipe) {
        intervencao.setTrechoId(trecho.getId());
        intervencao.setEquipeId(equipe.getId());
        intervencao.setNome(trecho.getNomeTrecho());
        intervencao.setQuilometroInicial(trecho.getQuilometroInicial());
        intervencao.setQuilometroFinal(trecho.getQuilometroFinal());
        intervencao.setTipoClima(trecho.getTipoClima());
        intervencao.setNomeEquipe(equipe.getNomeEquipe());
    }

    private void validarCompatibilidade(ServicoIntervencao servico, EquipeManutencao equipe) {
        if (!servico.tipoRocadaExigida().equalsIgnoreCase(equipe.getTipoDeRocadaDeAtuacao())) {
            throw new RegraNegocioException("Este serviço exige equipe de roçada '" + servico.tipoRocadaExigida()
                    + "', mas a equipe " + equipe.getNomeEquipe() + " atua com '"
                    + equipe.getTipoDeRocadaDeAtuacao() + "'");
        }
    }

    private IntervencaoOperacional buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Intervenção não encontrada: " + id));
    }

    private TrechoRodovia buscarTrecho(Long id) {
        return trechoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Trecho não encontrado: " + id));
    }

    private EquipeManutencao buscarEquipe(Long id) {
        return equipeRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Equipe não encontrada: " + id));
    }

    private IntervencaoDto.Response paraResponse(IntervencaoOperacional i) {
        return new IntervencaoDto.Response(i.getId(), i.getTrechoId(), i.getEquipeId(),
                i.getNome(), i.getQuilometroInicial(), i.getQuilometroFinal(), i.getTipoClima(), i.getNomeEquipe(),
                tipoServicoDe(i.getTipoClima()), i.getNivelVegetacaoAntesCm(), i.getDataGeracao());
    }

    private String tipoServicoDe(String tipoClima) {
        try {
            return factory.criarPorClima(tipoClima).tipoServico();
        } catch (RegraNegocioException e) {
            return "DESCONHECIDO";
        }
    }
}
