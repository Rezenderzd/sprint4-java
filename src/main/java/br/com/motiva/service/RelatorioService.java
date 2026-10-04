package br.com.motiva.service;

import br.com.motiva.dto.RelatorioDto;
import br.com.motiva.exception.RecursoNaoEncontradoException;
import br.com.motiva.exception.RegraNegocioException;
import br.com.motiva.model.RankingEquipe;
import br.com.motiva.model.RankingTrecho;
import br.com.motiva.model.RelatorioPrioridade;
import br.com.motiva.model.TrechoRodovia;
import br.com.motiva.repository.EquipeRepository;
import br.com.motiva.repository.IntervencaoRepository;
import br.com.motiva.repository.RankingEquipeRepository;
import br.com.motiva.repository.RankingTrechoRepository;
import br.com.motiva.repository.RelatorioRepository;
import br.com.motiva.repository.TrechoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RelatorioService {

    private final RelatorioRepository repository;
    private final RankingEquipeRepository rankingEquipeRepository;
    private final RankingTrechoRepository rankingTrechoRepository;
    private final TrechoRepository trechoRepository;
    private final EquipeRepository equipeRepository;
    private final IntervencaoRepository intervencaoRepository;
    private final MotorPrioridadeService motor;

    public RelatorioService(RelatorioRepository repository, RankingEquipeRepository rankingEquipeRepository,
                            RankingTrechoRepository rankingTrechoRepository, TrechoRepository trechoRepository,
                            EquipeRepository equipeRepository, IntervencaoRepository intervencaoRepository,
                            MotorPrioridadeService motor) {
        this.repository = repository;
        this.rankingEquipeRepository = rankingEquipeRepository;
        this.rankingTrechoRepository = rankingTrechoRepository;
        this.trechoRepository = trechoRepository;
        this.equipeRepository = equipeRepository;
        this.intervencaoRepository = intervencaoRepository;
        this.motor = motor;
    }

    @Transactional
    public RelatorioDto.Response gerar() {
        List<TrechoRodovia> trechos = trechoRepository.findAll();

        RelatorioPrioridade relatorio = new RelatorioPrioridade();
        relatorio.setDataGeracao(LocalDateTime.now());
        relatorio.setTotalEquipes((int) equipeRepository.count());
        relatorio.setTotalTrechos(trechos.size());

        int comSensor = (int) trechoRepository.countByComSensor(true);
        relatorio.setTrechosComSensor(comSensor);
        relatorio.setTrechosSemSensor(trechos.size() - comSensor);

        int urgente = 0;
        int critico = 0;
        int atencao = 0;
        int normal = 0;
        for (TrechoRodovia trecho : trechos) {
            switch (motor.classificar(trecho.getNivelVegetacaoEmCm())) {
                case URGENTE -> urgente++;
                case CRITICO -> critico++;
                case ATENCAO -> atencao++;
                case NORMAL -> normal++;
            }
        }
        relatorio.setTrechosUrgente(urgente);
        relatorio.setTrechosCritico(critico);
        relatorio.setTrechosAtencao(atencao);
        relatorio.setTrechosNormal(normal);

        RelatorioPrioridade salvo = repository.save(relatorio);

        List<RankingEquipe> rankingEquipes = intervencaoRepository.rankingEquipes().stream()
                .map(linha -> new RankingEquipe(salvo.getId(), (String) linha[0], ((Number) linha[1]).longValue()))
                .toList();
        rankingEquipeRepository.saveAll(rankingEquipes);

        List<RankingTrecho> rankingTrechos = intervencaoRepository.rankingTrechos().stream()
                .map(linha -> new RankingTrecho(salvo.getId(), (String) linha[0],
                        ((Number) linha[1]).intValue(), ((Number) linha[2]).intValue(),
                        ((Number) linha[3]).longValue()))
                .toList();
        rankingTrechoRepository.saveAll(rankingTrechos);

        return montarRespostas(List.of(salvo)).get(0);
    }

    public List<RelatorioDto.Response> listarHistorico() {
        return montarRespostas(repository.findAllByOrderByDataGeracaoDesc());
    }

    public RelatorioDto.Response buscarPorId(Long id) {
        RelatorioPrioridade relatorio = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Relatório não encontrado: " + id));
        return montarRespostas(List.of(relatorio)).get(0);
    }

    public List<RelatorioDto.Response> listarPorPeriodo(LocalDate inicio, LocalDate fim) {
        if (inicio.isAfter(fim)) {
            throw new RegraNegocioException("A data inicial não pode ser posterior à data final");
        }
        LocalDateTime de = inicio.atStartOfDay();
        LocalDateTime ate = fim.atTime(23, 59, 59);
        return montarRespostas(repository.findByDataGeracaoBetweenOrderByDataGeracaoDesc(de, ate));
    }

    private List<RelatorioDto.Response> montarRespostas(List<RelatorioPrioridade> relatorios) {
        if (relatorios.isEmpty()) {
            return List.of();
        }
        List<Long> ids = relatorios.stream().map(RelatorioPrioridade::getId).toList();

        Map<Long, List<RelatorioDto.RankingEquipeItem>> equipesPorRelatorio =
                rankingEquipeRepository.findByRelatorioIdInOrderByTotalIntervencoesDesc(ids).stream()
                        .collect(Collectors.groupingBy(RankingEquipe::getRelatorioId,
                                Collectors.mapping(e -> new RelatorioDto.RankingEquipeItem(
                                        e.getNomeEquipe(), e.getTotalIntervencoes()), Collectors.toList())));

        Map<Long, List<RelatorioDto.RankingTrechoItem>> trechosPorRelatorio =
                rankingTrechoRepository.findByRelatorioIdInOrderByTotalIntervencoesDesc(ids).stream()
                        .collect(Collectors.groupingBy(RankingTrecho::getRelatorioId,
                                Collectors.mapping(t -> new RelatorioDto.RankingTrechoItem(
                                        t.getNome(), t.getQuilometroInicial(), t.getQuilometroFinal(),
                                        t.getTotalIntervencoes()), Collectors.toList())));

        return relatorios.stream()
                .map(r -> new RelatorioDto.Response(r.getId(), r.getDataGeracao(), r.getTotalEquipes(),
                        r.getTotalTrechos(), r.getTrechosComSensor(), r.getTrechosSemSensor(),
                        r.getTrechosUrgente(), r.getTrechosCritico(), r.getTrechosAtencao(), r.getTrechosNormal(),
                        equipesPorRelatorio.getOrDefault(r.getId(), List.of()),
                        trechosPorRelatorio.getOrDefault(r.getId(), List.of())))
                .toList();
    }
}
