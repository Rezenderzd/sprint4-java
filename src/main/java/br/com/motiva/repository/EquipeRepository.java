package br.com.motiva.repository;

import br.com.motiva.model.EquipeManutencao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EquipeRepository extends JpaRepository<EquipeManutencao, Long> {

    List<EquipeManutencao> findByTipoDeRocadaDeAtuacaoIgnoreCase(String tipo);

    List<EquipeManutencao> findByNomeEquipeContainingIgnoreCase(String nome);
}
