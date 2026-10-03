package br.com.motiva.repository;

import br.com.motiva.model.IntervencaoOperacional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IntervencaoRepository extends JpaRepository<IntervencaoOperacional, Long> {

    List<IntervencaoOperacional> findByNomeEquipe(String nomeEquipe);

    boolean existsByTrechoId(Long trechoId);

    boolean existsByEquipeId(Long equipeId);

    List<IntervencaoOperacional> findByTipoClimaIgnoreCase(String tipoClima);

    @Query("SELECT i.nomeEquipe, COUNT(i) FROM IntervencaoOperacional i "
            + "GROUP BY i.nomeEquipe ORDER BY COUNT(i) DESC")
    List<Object[]> rankingEquipes();

    @Query("SELECT i.nome, i.quilometroInicial, i.quilometroFinal, COUNT(i) FROM IntervencaoOperacional i "
            + "GROUP BY i.nome, i.quilometroInicial, i.quilometroFinal ORDER BY COUNT(i) DESC")
    List<Object[]> rankingTrechos();
}
