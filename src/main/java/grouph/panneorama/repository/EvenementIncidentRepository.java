package grouph.panneorama.repository;

import grouph.panneorama.modele.EvenementIncident;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EvenementIncidentRepository extends JpaRepository<EvenementIncident, Integer> {

    List<EvenementIncident> findByIncidentIdOrderByDateEvenementAscIdAsc(Integer incidentId);

    // Le journal global affiche l'équipement de chaque incident : on le charge dans la même requête.
    @Query("""
            SELECT e FROM EvenementIncident e
            JOIN FETCH e.incident i
            JOIN FETCH i.equipement
            ORDER BY e.dateEvenement DESC, e.id DESC
            """)
    List<EvenementIncident> findRecents(Pageable pageable);
}