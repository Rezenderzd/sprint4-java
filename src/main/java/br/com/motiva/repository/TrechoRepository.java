package br.com.motiva.repository;

import br.com.motiva.model.TrechoRodovia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrechoRepository extends JpaRepository<TrechoRodovia, Long> {

    List<TrechoRodovia> findByNivelVegetacaoEmCmGreaterThanEqual(Double minimo);

    List<TrechoRodovia> findByTipoClimaIgnoreCase(String tipoClima);

    long countByComSensor(Boolean comSensor);
}
