package grouph.panneorama.service;

import grouph.panneorama.evenement.ChangementIncident;
import grouph.panneorama.exception.IncidentDejaEvalueException;
import grouph.panneorama.exception.IncidentIntrouvableException;
import grouph.panneorama.modele.Equipement;
import grouph.panneorama.modele.Evaluation;
import grouph.panneorama.modele.Incident;
import grouph.panneorama.modele.Prestataire;
import grouph.panneorama.modele.StatutIncident;
import grouph.panneorama.modele.TypeEvenementIncident;
import grouph.panneorama.repository.EvaluationRepository;
import grouph.panneorama.repository.IncidentRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class IncidentService {
    private final IncidentRepository incidentRepository;
    private final EvaluationRepository evaluationRepository;
    private final EquipementService equipementService;
    private final PrestataireService prestataireService;
    private final Clock horloge;
    private final ApplicationEventPublisher evenements;

    public IncidentService(IncidentRepository incidentRepository,
                           EvaluationRepository evaluationRepository,
                           EquipementService equipementService,
                           PrestataireService prestataireService,
                           Clock horloge,
                           ApplicationEventPublisher evenements) {
        this.incidentRepository = incidentRepository;
        this.evaluationRepository = evaluationRepository;
        this.equipementService = equipementService;
        this.prestataireService = prestataireService;
        this.horloge = horloge;
        this.evenements = evenements;
    }

    @Transactional
    public Incident declarerIncident(Integer equipementId, String description) {
        Equipement equipement = equipementService.trouverEquipement(equipementId);
        Incident incident = new Incident(equipement, description, LocalDateTime.now(horloge));
        Incident enregistre = incidentRepository.save(incident);
        publier(enregistre, TypeEvenementIncident.DECLARATION,
                "Incident déclaré sur l'équipement " + equipement.getReference());
        return enregistre;
    }

    public Incident trouverIncident(Integer incidentId) {
        return incidentRepository.trouverAvecEquipementEtPrestataire(incidentId)
                .orElseThrow(() -> new IncidentIntrouvableException(incidentId));
    }

    @PreAuthorize("hasRole('RESPONSABLE')")
    public Incident trouverIncidentModifiable(Integer incidentId) {
        Incident incident = trouverIncident(incidentId);
        incident.verifierModifiable();
        return incident;
    }

    @Transactional
    @PreAuthorize("hasRole('RESPONSABLE')")
    public Incident modifierDescription(Integer incidentId, String nouvelleDescription) {
        Incident incident = trouverIncident(incidentId);
        incident.modifierDescription(nouvelleDescription);
        publier(incident, TypeEvenementIncident.MODIFICATION, "Description modifiée");
        return incident;
    }

    @PreAuthorize("hasRole('RESPONSABLE')")
    public Incident trouverIncidentAffectable(Integer incidentId) {
        Incident incident = trouverIncident(incidentId);
        incident.verifierAffectable();
        return incident;
    }

    @Transactional
    @PreAuthorize("hasRole('RESPONSABLE')")
    public Incident affecterPrestataire(Integer incidentId, Integer prestataireId) {
        Incident incident = trouverIncident(incidentId);
        Prestataire prestataire = prestataireService.trouverPrestataire(prestataireId);
        Prestataire precedent = incident.getPrestataire();
        incident.affecterPrestataire(prestataire);
        if (precedent == null) {
            publier(incident, TypeEvenementIncident.AFFECTATION,
                    "Affecté au prestataire " + prestataire.getNom());
        } else {
            publier(incident, TypeEvenementIncident.REAFFECTATION,
                    "Réaffecté de " + precedent.getNom() + " à " + prestataire.getNom());
        }
        return incident;
    }

    @PreAuthorize("hasRole('RESPONSABLE')")
    public Incident trouverIncidentEvaluable(Integer incidentId) {
        Incident incident = trouverIncident(incidentId);
        incident.verifierEvaluable();
        if (evaluationRepository.existsByIncidentId(incidentId)) {
            throw new IncidentDejaEvalueException(incidentId);
        }
        return incident;
    }

    @Transactional
    @PreAuthorize("hasRole('RESPONSABLE')")
    public Evaluation evaluerPrestataire(Integer incidentId, Integer note, String commentaire) {
        Incident incident = trouverIncidentEvaluable(incidentId);
        Evaluation evaluation = evaluationRepository.save(
                new Evaluation(incident, note, commentaire, LocalDateTime.now(horloge)));
        publier(incident, TypeEvenementIncident.EVALUATION,
                "Prestataire " + incident.getPrestataire().getNom() + " noté " + note + " / " + Evaluation.NOTE_MAXIMALE);
        return evaluation;
    }

    public Optional<Evaluation> trouverEvaluation(Integer incidentId) {
        return evaluationRepository.findByIncidentId(incidentId);
    }

    public Duration calculerDureeOuverture(Incident incident) {
        return incident.dureeOuverture(horloge);
    }

    public List<Incident> listerParEquipement(Integer equipementId) {
        return incidentRepository.findByEquipementIdOrderByDateOuvertureDesc(equipementId);
    }

    public List<Incident> listerHistoriqueResolus() {
        return incidentRepository.findHistoriqueResolus();
    }

    public List<Incident> listerIncidents(StatutIncident statut) {
        return statut == null
                ? incidentRepository.findTousAvecDetails()
                : incidentRepository.findParStatutAvecDetails(statut);
    }

    @Transactional
    @PreAuthorize("hasRole('RESPONSABLE')")
    public Incident resoudreIncident(Integer incidentId) {
        Incident incident = trouverIncident(incidentId);
        incident.resoudre(LocalDateTime.now(horloge));
        publier(incident, TypeEvenementIncident.RESOLUTION, "Incident résolu");
        return incident;
    }

    private void publier(Incident incident, TypeEvenementIncident type, String detail) {
        evenements.publishEvent(new ChangementIncident(incident, type, detail));
    }
}