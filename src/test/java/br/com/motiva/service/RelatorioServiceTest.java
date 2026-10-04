package br.com.motiva.service;

import br.com.motiva.dto.RelatorioDto;
import br.com.motiva.exception.RegraNegocioException;
import br.com.motiva.model.RankingEquipe;
import br.com.motiva.model.RelatorioPrioridade;
import br.com.motiva.model.TrechoRodovia;
import br.com.motiva.repository.EquipeRepository;
import br.com.motiva.repository.IntervencaoRepository;
import br.com.motiva.repository.RankingEquipeRepository;
import br.com.motiva.repository.RankingTrechoRepository;
import br.com.motiva.repository.RelatorioRepository;
import br.com.motiva.repository.TrechoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RelatorioServiceTest {

    @Mock
    private RelatorioRepository repository;
    @Mock
    private RankingEquipeRepository rankingEquipeRepository;
    @Mock
    private RankingTrechoRepository rankingTrechoRepository;
    @Mock
    private TrechoRepository trechoRepository;
    @Mock
    private EquipeRepository equipeRepository;
    @Mock
    private IntervencaoRepository intervencaoRepository;

    @Spy
    private MotorPrioridadeService motor = new MotorPrioridadeService();

    @InjectMocks
    private RelatorioService service;

    @Test
    public void deveGerarRelatorioComContagemPorPrioridade() {
        when(trechoRepository.findAll()).thenReturn(List.of(
                trecho(85.0), trecho(55.0), trecho(30.0), trecho(10.0)));
        when(trechoRepository.countByComSensor(true)).thenReturn(1L);
        when(equipeRepository.count()).thenReturn(4L);
        when(intervencaoRepository.rankingEquipes())
                .thenReturn(Collections.singletonList(new Object[]{"Equipe Alpha", 3L}));
        when(repository.save(any(RelatorioPrioridade.class))).thenAnswer(inv -> {
            RelatorioPrioridade r = inv.getArgument(0);
            r.setId(1L);
            return r;
        });

        RelatorioDto.Response resposta = service.gerar();

        assertEquals(1L, resposta.id());
        assertEquals(4, resposta.totalTrechos());
        assertEquals(4, resposta.totalEquipes());
        assertEquals(1, resposta.trechosComSensor());
        assertEquals(3, resposta.trechosSemSensor());
        assertEquals(1, resposta.trechosUrgente());
        assertEquals(1, resposta.trechosCritico());
        assertEquals(1, resposta.trechosAtencao());
        assertEquals(1, resposta.trechosNormal());
        verify(repository).save(any(RelatorioPrioridade.class));
        verify(rankingEquipeRepository).saveAll(any());
    }

    @Test
    public void deveLancarExcecaoQuandoDataInicialDepoisDaFinal() {
        LocalDate inicio = LocalDate.of(2026, 9, 30);
        LocalDate fim = LocalDate.of(2026, 9, 1);

        assertThrows(RegraNegocioException.class, () -> service.listarPorPeriodo(inicio, fim));

        verifyNoInteractions(repository);
    }

    private TrechoRodovia trecho(double nivel) {
        TrechoRodovia t = new TrechoRodovia();
        t.setNivelVegetacaoEmCm(nivel);
        return t;
    }

    @Test
    public void deveBuscarOsRankingsDeTodosOsRelatoriosEmUmaUnicaConsulta() {
        when(repository.findAllByOrderByDataGeracaoDesc())
                .thenReturn(List.of(relatorio(3L), relatorio(2L), relatorio(1L)));
        when(rankingEquipeRepository.findByRelatorioIdInOrderByTotalIntervencoesDesc(List.of(3L, 2L, 1L)))
                .thenReturn(List.of(new RankingEquipe(3L, "Equipe Alpha", 5L), new RankingEquipe(1L, "Equipe Delta", 2L)));
        when(rankingTrechoRepository.findByRelatorioIdInOrderByTotalIntervencoesDesc(List.of(3L, 2L, 1L)))
                .thenReturn(List.of());

        List<RelatorioDto.Response> resposta = service.listarHistorico();

        assertEquals(3, resposta.size());
        verify(rankingEquipeRepository, times(1)).findByRelatorioIdInOrderByTotalIntervencoesDesc(any());
        verify(rankingTrechoRepository, times(1)).findByRelatorioIdInOrderByTotalIntervencoesDesc(any());
    }

    @Test
    public void deveSepararOsRankingsPorRelatorioAoMontarOHistorico() {
        when(repository.findAllByOrderByDataGeracaoDesc())
                .thenReturn(List.of(relatorio(3L), relatorio(2L), relatorio(1L)));
        when(rankingEquipeRepository.findByRelatorioIdInOrderByTotalIntervencoesDesc(List.of(3L, 2L, 1L)))
                .thenReturn(List.of(new RankingEquipe(3L, "Equipe Alpha", 5L), new RankingEquipe(1L, "Equipe Delta", 2L)));

        List<RelatorioDto.Response> resposta = service.listarHistorico();

        assertEquals("Equipe Alpha", resposta.get(0).rankingEquipes().get(0).nomeEquipe());
        assertTrue(resposta.get(1).rankingEquipes().isEmpty());
        assertEquals("Equipe Delta", resposta.get(2).rankingEquipes().get(0).nomeEquipe());
    }

    private RelatorioPrioridade relatorio(Long id) {
        RelatorioPrioridade r = new RelatorioPrioridade();
        r.setId(id);
        return r;
    }
}
