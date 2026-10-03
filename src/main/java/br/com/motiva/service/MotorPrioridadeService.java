package br.com.motiva.service;

import br.com.motiva.exception.RegraNegocioException;
import br.com.motiva.model.NivelPrioridade;
import org.springframework.stereotype.Service;

@Service
public class MotorPrioridadeService {

    public static final double LIMITE_ATENCAO_CM = 25.0;
    public static final double LIMITE_CRITICO_CM = 50.0;
    public static final double LIMITE_URGENTE_CM = 80.0;

    public static final double LIMITE_INTERVENCAO_CM = 30.0;

    public NivelPrioridade classificar(Double nivelVegetacaoCm) {
        if (nivelVegetacaoCm == null) {
            throw new RegraNegocioException("Nível de vegetação não informado: não é possível classificar a prioridade");
        }
        if (nivelVegetacaoCm >= LIMITE_URGENTE_CM) {
            return NivelPrioridade.URGENTE;
        }
        if (nivelVegetacaoCm >= LIMITE_CRITICO_CM) {
            return NivelPrioridade.CRITICO;
        }
        if (nivelVegetacaoCm >= LIMITE_ATENCAO_CM) {
            return NivelPrioridade.ATENCAO;
        }
        return NivelPrioridade.NORMAL;
    }
}
