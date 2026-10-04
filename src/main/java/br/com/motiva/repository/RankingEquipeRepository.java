package br.com.motiva.repository;

import br.com.motiva.model.RankingEquipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RankingEquipeRepository extends JpaRepository<RankingEquipe, Long> {

    List<RankingEquipe> findByRelatorioIdInOrderByTotalIntervencoesDesc(List<Long> relatorioIds);
}
