package grouph.panneorama.dto;

import grouph.panneorama.modele.Equipement;
import grouph.panneorama.modele.Incident;
import grouph.panneorama.modele.Prestataire;
import grouph.panneorama.modele.StatutIncident;

import java.time.LocalDateTime;

public record IncidentDTO(Integer id,
                          String description,
                          LocalDateTime dateOuverture,
                          LocalDateTime dateResolution,
                          StatutIncident statut,
                          Integer equipementId,
                          String equipementReference,
                          String stationNom,
                          String prestataireNom) {

    public static IncidentDTO depuis(Incident incident) {
        Equipement equipement = incident.getEquipement();
        Prestataire prestataire = incident.getPrestataire();
        return new IncidentDTO(
                incident.getId(),
                incident.getDescription(),
                incident.getDateOuverture(),
                incident.getDateResolution(),
                incident.getStatut(),
                equipement.getId(),
                equipement.getReference(),
                equipement.getStation().getNom(),
                prestataire != null ? prestataire.getNom() : null);
    }
}