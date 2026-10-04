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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class IntervencaoServiceTest {

    @Mock
    private IntervencaoRepository repository;

    @Mock
    private TrechoRepository trechoRepository;

    @Mock
    private EquipeRepository equipeRepository;

    @Spy
    private IntervencaoFactory factory = new IntervencaoFactory();

    @InjectMocks
    private IntervencaoService service;

    private TrechoRodovia trechoUmido;
    private EquipeManutencao equipeMecanizada;
    private EquipeManutencao equipeManual;

    @BeforeEach
    public void setUp() {
        trechoUmido = new TrechoRodovia();
        trechoUmido.setId(1L);
        trechoUmido.setNomeTrecho("Br");
        trechoUmido.setQuilometroInicial(10);
        trechoUmido.setQuilometroFinal(15);
        trechoUmido.setTipoClima("umido");
        trechoUmido.setNivelVegetacaoEmCm(40.0);
        trechoUmido.setComSensor(false);

        equipeMecanizada = new EquipeManutencao();
        equipeMecanizada.setId(2L);
        equipeMecanizada.setNomeEquipe("Equipe Delta");
        equipeMecanizada.setTipoDeRocadaDeAtuacao("mecanizada");

        equipeManual = new EquipeManutencao();
        equipeManual.setNomeEquipe("Equipe Alpha");
        equipeManual.setTipoDeRocadaDeAtuacao("manual");
    }

    @Test
    public void deveCriarRocadaMecanizadaECortarAVegetacao() {
        when(trechoRepository.findById(1L)).thenReturn(Optional.of(trechoUmido));
        when(equipeRepository.findById(2L)).thenReturn(Optional.of(equipeMecanizada));
        when(repository.save(any(IntervencaoOperacional.class))).thenAnswer(inv -> inv.getArgument(0));

        IntervencaoDto.Response resposta = service.criar(new IntervencaoDto.Request(1L, 2L));

        assertEquals("ROCADA_MECANIZADA", resposta.tipoServico());
        assertEquals("Equipe Delta", resposta.nomeEquipe());
        assertEquals(1L, resposta.trechoId());
        assertEquals(2L, resposta.equipeId());
        assertEquals(TrechoRodovia.ALTURA_POS_CORTE, trechoUmido.getNivelVegetacaoEmCm(), 0.01);
        verify(trechoRepository).save(trechoUmido);
    }

    @Test
    public void deveLancarExcecaoQuandoEquipeIncompativelComOClima() {
        when(trechoRepository.findById(1L)).thenReturn(Optional.of(trechoUmido));
        when(equipeRepository.findById(3L)).thenReturn(Optional.of(equipeManual));

        assertThrows(RegraNegocioException.class, () -> service.criar(new IntervencaoDto.Request(1L, 3L)));

        verify(repository, never()).save(any());
    }

    @Test
    public void deveLancarExcecaoQuandoTrechoInexistente() {
        when(trechoRepository.findById(99L)).thenReturn(Optional.empty());

        RecursoNaoEncontradoException erro = assertThrows(RecursoNaoEncontradoException.class,
                () -> service.criar(new IntervencaoDto.Request(99L, 2L)));

        assertEquals("Trecho não encontrado: 99", erro.getMessage());
    }

    @Test
    public void deveLancarExcecaoAoDeletarIntervencaoInexistente() {
        when(repository.existsById(99L)).thenReturn(false);

        assertThrows(RecursoNaoEncontradoException.class, () -> service.deletar(99L));

        verify(repository, never()).deleteById(any());
    }

    @Test
    public void deveGerarIntervencaoParaTrechoAcimaDoLimite() {
        when(trechoRepository.findByNivelVegetacaoEmCmGreaterThanEqual(30.0)).thenReturn(List.of(trechoUmido));
        when(equipeRepository.findByTipoDeRocadaDeAtuacaoIgnoreCase("mecanizada"))
                .thenReturn(List.of(equipeMecanizada));
        when(repository.save(any(IntervencaoOperacional.class))).thenAnswer(inv -> inv.getArgument(0));

        IntervencaoDto.GeracaoResultado resultado = service.gerarIntervencoes();

        assertEquals(1, resultado.intervencoes().size());
        assertEquals(1, resultado.mensagens().size());
        assertTrue(resultado.mensagens().get(0).contains("serviço de tratores"));
        assertTrue(resultado.avisos().isEmpty());
    }

    @Test
    public void deveAvisarQuandoNaoHaEquipeCompativel() {
        when(trechoRepository.findByNivelVegetacaoEmCmGreaterThanEqual(30.0)).thenReturn(List.of(trechoUmido));
        when(equipeRepository.findByTipoDeRocadaDeAtuacaoIgnoreCase("mecanizada")).thenReturn(List.of());

        IntervencaoDto.GeracaoResultado resultado = service.gerarIntervencoes();

        assertTrue(resultado.intervencoes().isEmpty());
        assertEquals(1, resultado.avisos().size());
        verify(repository, never()).save(any());
    }

    @Test
    public void deveAvisarQuandoNenhumTrechoPrecisaDeIntervencao() {
        when(trechoRepository.findByNivelVegetacaoEmCmGreaterThanEqual(30.0)).thenReturn(List.of());

        IntervencaoDto.GeracaoResultado resultado = service.gerarIntervencoes();

        assertEquals("Não houve a necessidade de nenhuma intervenção.", resultado.avisos().get(0));
    }

    @Test
    public void deveCortarONovoTrechoQuandoOPutTrocaDeTrecho() {
        TrechoRodovia antigo = trecho(7L, 5.0);
        IntervencaoOperacional existente = intervencaoExistente(antigo, equipeMecanizada, 60.0, LocalDateTime.now());
        when(repository.findById(10L)).thenReturn(Optional.of(existente));
        when(trechoRepository.findById(1L)).thenReturn(Optional.of(trechoUmido));
        when(trechoRepository.findById(7L)).thenReturn(Optional.of(antigo));
        when(equipeRepository.findById(2L)).thenReturn(Optional.of(equipeMecanizada));
        when(repository.save(any(IntervencaoOperacional.class))).thenAnswer(inv -> inv.getArgument(0));

        IntervencaoDto.Response resposta = service.atualizar(10L, new IntervencaoDto.Request(1L, 2L));

        assertEquals(TrechoRodovia.ALTURA_POS_CORTE, trechoUmido.getNivelVegetacaoEmCm(), 0.01);
        assertEquals(40.0, resposta.nivelVegetacaoAntesCm(), 0.01);
        assertEquals(1L, resposta.trechoId());
    }

    @Test
    public void deveDesfazerOCorteDoTrechoAntigoQuandoOPutTrocaDeTrecho() {
        TrechoRodovia antigo = trecho(7L, 5.0);
        IntervencaoOperacional existente = intervencaoExistente(antigo, equipeMecanizada, 60.0, LocalDateTime.now());
        when(repository.findById(10L)).thenReturn(Optional.of(existente));
        when(trechoRepository.findById(1L)).thenReturn(Optional.of(trechoUmido));
        when(trechoRepository.findById(7L)).thenReturn(Optional.of(antigo));
        when(equipeRepository.findById(2L)).thenReturn(Optional.of(equipeMecanizada));
        when(repository.save(any(IntervencaoOperacional.class))).thenAnswer(inv -> inv.getArgument(0));

        service.atualizar(10L, new IntervencaoDto.Request(1L, 2L));

        assertEquals(60.0, antigo.getNivelVegetacaoEmCm(), 0.01);
        verify(trechoRepository).save(antigo);
    }

    @Test
    public void deveRenovarADataQuandoOPutTrocaDeTrecho() {
        LocalDateTime ontem = LocalDateTime.now().minusDays(1);
        TrechoRodovia antigo = trecho(7L, 5.0);
        IntervencaoOperacional existente = intervencaoExistente(antigo, equipeMecanizada, 60.0, ontem);
        when(repository.findById(10L)).thenReturn(Optional.of(existente));
        when(trechoRepository.findById(1L)).thenReturn(Optional.of(trechoUmido));
        when(trechoRepository.findById(7L)).thenReturn(Optional.of(antigo));
        when(equipeRepository.findById(2L)).thenReturn(Optional.of(equipeMecanizada));
        when(repository.save(any(IntervencaoOperacional.class))).thenAnswer(inv -> inv.getArgument(0));

        IntervencaoDto.Response resposta = service.atualizar(10L, new IntervencaoDto.Request(1L, 2L));

        assertTrue(resposta.dataGeracao().isAfter(ontem));
    }

    @Test
    public void naoDeveRestaurarTrechoAntigoQuandoAGramaJaCresceuDepoisDoCorte() {
        TrechoRodovia antigo = trecho(7L, 12.0);
        IntervencaoOperacional existente = intervencaoExistente(antigo, equipeMecanizada, 60.0, LocalDateTime.now());
        when(repository.findById(10L)).thenReturn(Optional.of(existente));
        when(trechoRepository.findById(1L)).thenReturn(Optional.of(trechoUmido));
        when(trechoRepository.findById(7L)).thenReturn(Optional.of(antigo));
        when(equipeRepository.findById(2L)).thenReturn(Optional.of(equipeMecanizada));
        when(repository.save(any(IntervencaoOperacional.class))).thenAnswer(inv -> inv.getArgument(0));

        service.atualizar(10L, new IntervencaoDto.Request(1L, 2L));

        assertEquals(12.0, antigo.getNivelVegetacaoEmCm(), 0.01);
        verify(trechoRepository, never()).save(antigo);
    }

    @Test
    public void deveTrocarSoAEquipeSemCortarNemMudarADataQuandoOTrechoNaoMuda() {
        LocalDateTime dataOriginal = LocalDateTime.of(2026, 9, 1, 10, 0);
        IntervencaoOperacional existente = intervencaoExistente(trechoUmido, equipeMecanizada, 40.0, dataOriginal);
        EquipeManutencao outraMecanizada = new EquipeManutencao();
        outraMecanizada.setId(4L);
        outraMecanizada.setNomeEquipe("Equipe Zeta");
        outraMecanizada.setTipoDeRocadaDeAtuacao("mecanizada");
        when(repository.findById(10L)).thenReturn(Optional.of(existente));
        when(trechoRepository.findById(1L)).thenReturn(Optional.of(trechoUmido));
        when(equipeRepository.findById(4L)).thenReturn(Optional.of(outraMecanizada));
        when(repository.save(any(IntervencaoOperacional.class))).thenAnswer(inv -> inv.getArgument(0));

        IntervencaoDto.Response resposta = service.atualizar(10L, new IntervencaoDto.Request(1L, 4L));

        assertEquals("Equipe Zeta", resposta.nomeEquipe());
        assertEquals(dataOriginal, resposta.dataGeracao());
        assertEquals(40.0, trechoUmido.getNivelVegetacaoEmCm(), 0.01);
        verify(trechoRepository, never()).save(any());
    }

    @Test
    public void deveLancarExcecaoQuandoOPutUsaEquipeIncompativelSemGravarNada() {
        IntervencaoOperacional existente = intervencaoExistente(trechoUmido, equipeMecanizada, 40.0, LocalDateTime.now());
        when(repository.findById(10L)).thenReturn(Optional.of(existente));
        when(trechoRepository.findById(1L)).thenReturn(Optional.of(trechoUmido));
        when(equipeRepository.findById(3L)).thenReturn(Optional.of(equipeManual));

        assertThrows(RegraNegocioException.class, () -> service.atualizar(10L, new IntervencaoDto.Request(1L, 3L)));
        verify(repository, never()).save(any());
        verify(trechoRepository, never()).save(any());
    }

    private TrechoRodovia trecho(Long id, double nivel) {
        TrechoRodovia t = new TrechoRodovia();
        t.setId(id);
        t.setNomeTrecho("Rodo Anel");
        t.setQuilometroInicial(20);
        t.setQuilometroFinal(30);
        t.setTipoClima("umido");
        t.setNivelVegetacaoEmCm(nivel);
        t.setComSensor(false);
        return t;
    }

    private IntervencaoOperacional intervencaoExistente(TrechoRodovia trecho, EquipeManutencao equipe,
                                                        double nivelAntes, LocalDateTime data) {
        IntervencaoOperacional i = new IntervencaoOperacional();
        i.setId(10L);
        i.setTrechoId(trecho.getId());
        i.setEquipeId(equipe.getId());
        i.setNome(trecho.getNomeTrecho());
        i.setQuilometroInicial(trecho.getQuilometroInicial());
        i.setQuilometroFinal(trecho.getQuilometroFinal());
        i.setTipoClima(trecho.getTipoClima());
        i.setNomeEquipe(equipe.getNomeEquipe());
        i.setNivelVegetacaoAntesCm(nivelAntes);
        i.setDataGeracao(data);
        return i;
    }
}
