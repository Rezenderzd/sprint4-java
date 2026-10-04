package br.com.motiva.repository;

import br.com.motiva.model.RankingTrecho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RankingTrechoRepository extends JpaRepository<RankingTrecho, Long> {

    List<RankingTrecho> findByRelatorioIdInOrderByTotalIntervencoesDesc(List<Long> relatorioIds);
}
