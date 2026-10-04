package br.com.motiva.service;

import br.com.motiva.model.NivelPrioridade;
import org.junit.jupiter.api.Test;
import br.com.motiva.exception.RegraNegocioException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MotorPrioridadeServiceTest {

    private final MotorPrioridadeService motor = new MotorPrioridadeService();

    @Test
    public void deveClassificarUrgenteQuandoVegetacaoIgualOuAcimaDe80() {
        assertEquals(NivelPrioridade.URGENTE, motor.classificar(80.0));
        assertEquals(NivelPrioridade.URGENTE, motor.classificar(120.0));
    }

    @Test
    public void deveClassificarCriticoEntre50E79() {
        assertEquals(NivelPrioridade.CRITICO, motor.classificar(50.0));
        assertEquals(NivelPrioridade.CRITICO, motor.classificar(79.9));
    }

    @Test
    public void deveClassificarAtencaoEntre25E49() {
        assertEquals(NivelPrioridade.ATENCAO, motor.classificar(25.0));
        assertEquals(NivelPrioridade.ATENCAO, motor.classificar(49.9));
    }

    @Test
    public void deveClassificarNormalAbaixoDe25() {
        assertEquals(NivelPrioridade.NORMAL, motor.classificar(24.9));
        assertEquals(NivelPrioridade.NORMAL, motor.classificar(0.0));
    }

    @Test
    public void deveLancarExcecaoDeNegocioQuandoVegetacaoNula() {
        assertThrows(RegraNegocioException.class, () -> motor.classificar(null));
    }
}
