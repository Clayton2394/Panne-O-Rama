package grouph.panneorama.repository;

import grouph.panneorama.dto.PrestataireNote;
import grouph.panneorama.modele.Prestataire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PrestataireRepository extends JpaRepository<Prestataire, Integer> {

    // Seule requête SQL native du projet (§7.6) : agrégation des notes sur trois tables, calculée par la base.
    // Les LEFT JOIN conservent les prestataires jamais évalués (note moyenne nulle, zéro évaluation).
    @Query(value = """
            SELECT p.id AS id,
                   p.nom AS nom,
                   p.contact AS contact,
                   AVG(e.note) AS noteMoyenne,
                   COUNT(e.id) AS nombreEvaluations
            FROM prestataire p
            LEFT JOIN incident i ON i.prestataire_id = p.id
            LEFT JOIN evaluation e ON e.incident_id = i.id
            GROUP BY p.id, p.nom, p.contact
            ORDER BY p.nom
            """, nativeQuery = true)
    List<PrestataireNote> listerAvecNoteMoyenne();
}
