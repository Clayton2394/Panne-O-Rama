package grouph.panneorama.repository;

import grouph.panneorama.modele.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EvaluationRepository extends JpaRepository<Evaluation, Integer> {

    Optional<Evaluation> findByIncidentId(Integer incidentId);
}
