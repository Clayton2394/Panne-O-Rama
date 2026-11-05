package grouph.panneorama.service;

import grouph.panneorama.dto.PrestataireNote;
import grouph.panneorama.exception.PrestataireIntrouvableException;
import grouph.panneorama.modele.Evaluation;
import grouph.panneorama.modele.Prestataire;
import grouph.panneorama.modele.SurveillancePrestataire;
import grouph.panneorama.repository.EvaluationRepository;
import grouph.panneorama.repository.PrestataireRepository;
import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PrestataireService {
    private final PrestataireRepository prestataireRepository;
    private final EvaluationRepository evaluationRepository;

    public PrestataireService(PrestataireRepository prestataireRepository,
                              EvaluationRepository evaluationRepository) {
        this.prestataireRepository = prestataireRepository;
        this.evaluationRepository = evaluationRepository;
    }

    // F7 : le responsable choisit un prestataire en voyant la qualité de ses interventions passées (F10).
    public List<PrestataireNote> listerPrestatairesAvecNote() {
        return prestataireRepository.listerAvecNoteMoyenne();
    }

    public Prestataire trouverPrestataire(Integer prestataireId) {
        return prestataireRepository.findById(prestataireId)
                .orElseThrow(() -> new PrestataireIntrouvableException(prestataireId));
    }

    // PS1 : les évaluations des incidents affectés au prestataire, de la plus récente à la plus ancienne,
    // limitées à la fenêtre de surveillance.
    public List<Evaluation> listerDernieresEvaluations(Integer prestataireId) {
        return evaluationRepository.listerPlusRecentesDuPrestataire(prestataireId,
                Limit.of(SurveillancePrestataire.TAILLE_FENETRE));
    }
}
