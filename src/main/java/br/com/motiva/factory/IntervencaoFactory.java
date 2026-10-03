package br.com.motiva.factory;

import br.com.motiva.exception.RegraNegocioException;
import br.com.motiva.servico.Pulverizacao;
import br.com.motiva.servico.RocadaMecanizada;
import br.com.motiva.servico.ServicoIntervencao;
import org.springframework.stereotype.Component;

@Component
public class IntervencaoFactory {

    public ServicoIntervencao criarPorClima(String tipoClima) {
        if ("umido".equalsIgnoreCase(tipoClima)) {
            return new RocadaMecanizada();
        }
        if ("seco".equalsIgnoreCase(tipoClima)) {
            return new Pulverizacao();
        }
        throw new RegraNegocioException("Tipo de clima inválido, não tem como saber qual serviço utilizar");
    }
}
