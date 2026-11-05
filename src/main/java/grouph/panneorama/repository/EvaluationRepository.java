package grouph.panneorama.repository;

import grouph.panneorama.modele.Evaluation;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EvaluationRepository extends JpaRepository<Evaluation, Integer> {

    Optional<Evaluation> findByIncidentId(Integer incidentId);

    boolean existsByIncidentId(Integer incidentId);

    // PS1 : les plus récentes d'abord, l'identifiant départageant deux évaluations de même date.
    @Query("""
            SELECT e FROM Evaluation e
            WHERE e.incident.prestataire.id = :prestataireId
            ORDER BY e.dateEvaluation DESC, e.id DESC
            """)
    List<Evaluation> listerPlusRecentesDuPrestataire(@Param("prestataireId") Integer prestataireId, Limit limite);
}
