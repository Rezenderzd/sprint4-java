package br.com.motiva.factory;

import br.com.motiva.exception.RegraNegocioException;
import br.com.motiva.servico.Pulverizacao;
import br.com.motiva.servico.RocadaMecanizada;
import br.com.motiva.servico.ServicoIntervencao;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class IntervencaoFactoryTest {

    private final IntervencaoFactory factory = new IntervencaoFactory();

    @Test
    public void deveCriarRocadaMecanizadaQuandoClimaUmido() {
        ServicoIntervencao servico = factory.criarPorClima("umido");

        assertInstanceOf(RocadaMecanizada.class, servico);
        assertEquals("mecanizada", servico.tipoRocadaExigida());
    }

    @Test
    public void deveCriarPulverizacaoQuandoClimaSeco() {
        ServicoIntervencao servico = factory.criarPorClima("SECO");

        assertInstanceOf(Pulverizacao.class, servico);
        assertEquals("manual", servico.tipoRocadaExigida());
    }

    @Test
    public void deveLancarExcecaoQuandoClimaInvalido() {
        assertThrows(RegraNegocioException.class, () -> factory.criarPorClima("chuvoso"));
    }
}
