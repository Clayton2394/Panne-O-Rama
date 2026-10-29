package grouph.panneorama.repository;

import grouph.panneorama.modele.Equipement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EquipementRepository extends JpaRepository<Equipement, Integer> {
    List<Equipement> findByStationIdOrderByDescriptionAsc(Integer stationId);

    @Query("SELECT e FROM Equipement e JOIN FETCH e.station WHERE e.id = :id")
    Optional<Equipement> chargerAvecStation(@Param("id") Integer id);
}
