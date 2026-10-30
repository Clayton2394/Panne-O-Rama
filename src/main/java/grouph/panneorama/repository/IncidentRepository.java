package grouph.panneorama.repository;

import grouph.panneorama.modele.Incident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IncidentRepository extends JpaRepository<Incident, Integer> {

    // Le détail affiche la station, l'équipement et le prestataire : on les charge en une seule requête.
    @Query("""
            SELECT i FROM Incident i
            JOIN FETCH i.equipement e
            JOIN FETCH e.station
            LEFT JOIN FETCH i.prestataire
            WHERE i.id = :incidentId
            """)
    Optional<Incident> trouverAvecEquipementEtPrestataire(@Param("incidentId") Integer incidentId);
    List<Incident> findByEquipementIdOrderByDateOuvertureDesc(Integer equipementId);
}
