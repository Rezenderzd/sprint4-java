package br.com.motiva.repository;

import br.com.motiva.model.RelatorioPrioridade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RelatorioRepository extends JpaRepository<RelatorioPrioridade, Long> {

    List<RelatorioPrioridade> findAllByOrderByDataGeracaoDesc();

    List<RelatorioPrioridade> findByDataGeracaoBetweenOrderByDataGeracaoDesc(LocalDateTime inicio, LocalDateTime fim);
}
