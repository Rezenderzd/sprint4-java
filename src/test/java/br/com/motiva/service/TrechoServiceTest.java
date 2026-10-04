package br.com.motiva.service;

import br.com.motiva.dto.TrechoDto;
import br.com.motiva.exception.RecursoNaoEncontradoException;
import br.com.motiva.exception.RegraNegocioException;
import br.com.motiva.model.NivelPrioridade;
import br.com.motiva.model.TrechoRodovia;
import br.com.motiva.repository.IntervencaoRepository;
import br.com.motiva.repository.TrechoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TrechoServiceTest {

    @Mock
    private TrechoRepository repository;

    @Mock
    private IntervencaoRepository intervencaoRepository;

    @Spy
    private MotorPrioridadeService motor = new MotorPrioridadeService();

    @InjectMocks
    private TrechoService service;

    private TrechoDto.Request requestValido;

    @BeforeEach
    public void setUp() {
        requestValido = new TrechoDto.Request("Br", 10, 15, 20.0, "umido", false);
    }

    @Test
    public void deveCriarTrechoValido() {
        when(repository.save(any(TrechoRodovia.class))).thenAnswer(inv -> inv.getArgument(0));

        TrechoDto.Response resposta = service.criar(requestValido);

        assertEquals("Br", resposta.nomeTrecho());
        assertEquals(NivelPrioridade.NORMAL, resposta.prioridade());
        verify(repository).save(any(TrechoRodovia.class));
    }

    @Test
    public void deveLancarExcecaoQuandoKmFinalMenorQueInicial() {
        TrechoDto.Request invalido = new TrechoDto.Request("Br", 30, 15, 20.0, "seco", false);

        assertThrows(RegraNegocioException.class, () -> service.criar(invalido));

        verifyNoInteractions(repository);
    }

    @Test
    public void deveLancarExcecaoQuandoNivelVegetacaoNegativo() {
        TrechoDto.Request invalido = new TrechoDto.Request("Br", 10, 15, -1.0, "seco", false);

        RegraNegocioException erro = assertThrows(RegraNegocioException.class, () -> service.criar(invalido));

        assertEquals("Nível de vegetação não pode ser negativo", erro.getMessage());
        verifyNoInteractions(repository);
    }

    @Test
    public void deveLancarExcecaoQuandoClimaInvalido() {
        TrechoDto.Request invalido = new TrechoDto.Request("Br", 10, 15, 20.0, "chuvoso", false);

        assertThrows(RegraNegocioException.class, () -> service.criar(invalido));
        verifyNoInteractions(repository);
    }

    @Test
    public void deveBuscarTrechoPorId() {
        TrechoRodovia trecho = trechoComVegetacao(85.0);
        when(repository.findById(1L)).thenReturn(Optional.of(trecho));

        TrechoDto.Response resposta = service.buscarPorId(1L);

        assertEquals(NivelPrioridade.URGENTE, resposta.prioridade());
        verify(repository).findById(1L);
    }

    @Test
    public void deveLancarExcecaoQuandoIdInexistente() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        RecursoNaoEncontradoException erro =
                assertThrows(RecursoNaoEncontradoException.class, () -> service.buscarPorId(99L));

        assertEquals("Trecho não encontrado: 99", erro.getMessage());
    }

    @Test
    public void deveBuscarTrechosPorVegetacaoMinima() {
        when(repository.findByNivelVegetacaoEmCmGreaterThanEqual(30.0)).thenReturn(List.of(trechoComVegetacao(40.0)));

        List<TrechoDto.Response> resposta = service.buscarPorVegetacaoMinima(30.0);

        assertEquals(1, resposta.size());
        assertEquals(NivelPrioridade.ATENCAO, resposta.get(0).prioridade());
    }

    @Test
    public void deveLancarExcecaoAoAtualizarInexistente() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> service.atualizar(99L, requestValido));

        verify(repository, never()).save(any());
    }

    @Test
    public void deveDeletarTrechoExistente() {
        when(repository.existsById(1L)).thenReturn(true);

        service.deletar(1L);

        verify(repository).deleteById(1L);
    }

    @Test
    public void deveLancarExcecaoAoDeletarInexistente() {
        when(repository.existsById(99L)).thenReturn(false);

        assertThrows(RecursoNaoEncontradoException.class, () -> service.deletar(99L));

        verify(repository, never()).deleteById(any());
    }

    @Test
    public void deveImpedirDeletarTrechoComIntervencoes() {
        when(repository.existsById(1L)).thenReturn(true);
        when(intervencaoRepository.existsByTrechoId(1L)).thenReturn(true);

        assertThrows(RegraNegocioException.class, () -> service.deletar(1L));

        verify(repository, never()).deleteById(any());
    }

    @Test
    public void deveCrescer8cmNoTrechoComSensorEmClimaUmido() {
        TrechoRodovia trecho = trechoComVegetacao(20.0);
        trecho.setComSensor(true);
        trecho.setTipoClima("umido");
        when(repository.findAll()).thenReturn(List.of(trecho));

        service.simularCrescimento();

        assertEquals(28.0, trecho.getNivelVegetacaoEmCm(), 0.01);
        verify(repository).saveAll(any());
    }

    @Test
    public void deveCrescer4cmNoTrechoComSensorEmClimaSeco() {
        TrechoRodovia trecho = trechoComVegetacao(20.0);
        trecho.setComSensor(true);
        trecho.setTipoClima("seco");
        when(repository.findAll()).thenReturn(List.of(trecho));

        service.simularCrescimento();

        assertEquals(24.0, trecho.getNivelVegetacaoEmCm(), 0.01);
    }

    @Test
    public void deveCrescerEntre1E14cmNoTrechoSemSensor() {
        TrechoRodovia trecho = trechoComVegetacao(20.0);
        trecho.setComSensor(false);
        trecho.setTipoClima("seco");
        when(repository.findAll()).thenReturn(List.of(trecho));

        service.simularCrescimento();

        assertTrue(trecho.getNivelVegetacaoEmCm() >= 21.0 && trecho.getNivelVegetacaoEmCm() <= 34.0);
    }

    private TrechoRodovia trechoComVegetacao(double nivel) {
        TrechoRodovia trecho = new TrechoRodovia();
        trecho.setNomeTrecho("Br");
        trecho.setQuilometroInicial(10);
        trecho.setQuilometroFinal(15);
        trecho.setTipoClima("umido");
        trecho.setComSensor(false);
        trecho.setNivelVegetacaoEmCm(nivel);
        return trecho;
    }
}
