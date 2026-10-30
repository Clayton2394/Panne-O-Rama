package grouph.panneorama.service;

import grouph.panneorama.exception.IncidentIntrouvableException;
import grouph.panneorama.modele.Equipement;
import grouph.panneorama.modele.Evaluation;
import grouph.panneorama.modele.Incident;
import grouph.panneorama.repository.EvaluationRepository;
import grouph.panneorama.repository.IncidentRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class IncidentService {
    private final IncidentRepository incidentRepository;
    private final EvaluationRepository evaluationRepository;
    private final EquipementService equipementService;
    private final Clock horloge;

    public IncidentService(IncidentRepository incidentRepository,
                           EvaluationRepository evaluationRepository,
                           EquipementService equipementService,
                           Clock horloge) {
        this.incidentRepository = incidentRepository;
        this.evaluationRepository = evaluationRepository;
        this.equipementService = equipementService;
        this.horloge = horloge;
    }

    public Incident declarerIncident(Integer equipementId, String description) {
        Equipement equipement = equipementService.trouverEquipement(equipementId);
        Incident incident = new Incident(equipement, description, LocalDateTime.now(horloge));
        return incidentRepository.save(incident);
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
        return incident;
    }

    public Optional<Evaluation> trouverEvaluation(Integer incidentId) {
        return evaluationRepository.findByIncidentId(incidentId);
    }

    public Duration calculerDureeOuverture(Incident incident) {
        return incident.dureeOuverture(horloge);
    }
}
