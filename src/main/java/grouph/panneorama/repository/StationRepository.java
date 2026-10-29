package grouph.panneorama.repository;

import grouph.panneorama.modele.Station;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StationRepository extends JpaRepository<Station, Integer> {
    List<Station> findAllByOrderByNomAsc();
}
