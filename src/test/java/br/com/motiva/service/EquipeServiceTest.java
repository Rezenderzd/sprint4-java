package br.com.motiva.service;

import br.com.motiva.dto.EquipeDto;
import br.com.motiva.exception.RegraNegocioException;
import br.com.motiva.model.EquipeManutencao;
import br.com.motiva.repository.EquipeRepository;
import br.com.motiva.repository.IntervencaoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EquipeServiceTest {

    @Mock
    private EquipeRepository repository;

    @Mock
    private IntervencaoRepository intervencaoRepository;

    @InjectMocks
    private EquipeService service;

    @Test
    public void deveCriarEquipeValida() {
        when(repository.save(any(EquipeManutencao.class))).thenAnswer(inv -> inv.getArgument(0));

        EquipeDto.Response resposta = service.criar(new EquipeDto.Request("Equipe Alpha", 5, "MANUAL"));

        assertEquals("Equipe Alpha", resposta.nomeEquipe());
        assertEquals("manual", resposta.tipoDeRocadaDeAtuacao());
        verify(repository).save(any(EquipeManutencao.class));
    }

    @Test
    public void deveLancarExcecaoQuandoTipoDeRocadaInvalido() {
        EquipeDto.Request invalida = new EquipeDto.Request("Equipe Alpha", 5, "aerea");

        assertThrows(RegraNegocioException.class, () -> service.criar(invalida));

        verifyNoInteractions(repository);
    }

    @Test
    public void deveLancarExcecaoQuandoSemFuncionarios() {
        EquipeDto.Request invalida = new EquipeDto.Request("Equipe Alpha", 0, "manual");

        assertThrows(RegraNegocioException.class, () -> service.criar(invalida));

        verifyNoInteractions(repository);
    }

    @Test
    public void deveImpedirDeletarEquipeComIntervencoes() {
        when(repository.existsById(1L)).thenReturn(true);
        when(intervencaoRepository.existsByEquipeId(1L)).thenReturn(true);

        assertThrows(RegraNegocioException.class, () -> service.deletar(1L));

        verify(repository, never()).deleteById(any());
    }
}
