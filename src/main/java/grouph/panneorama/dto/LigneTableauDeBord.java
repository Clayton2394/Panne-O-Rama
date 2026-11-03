package grouph.panneorama.dto;

import grouph.panneorama.modele.Equipement;
import grouph.panneorama.modele.Incident;
import grouph.panneorama.modele.NiveauAttention;
import grouph.panneorama.modele.Prestataire;

import java.time.Clock;
import java.time.Duration;

public record LigneTableauDeBord(Integer incidentId,
                                 String description,
                                 Integer stationId,
                                 String stationNom,
                                 Integer equipementId,
                                 String equipementReference,
                                 String prestataireNom,
                                 NiveauAttention niveau,
                                 Duration dureeOuverture) {

    public static LigneTableauDeBord depuis(Incident incident, Clock horloge) {
        Equipement equipement = incident.getEquipement();
        Prestataire prestataire = incident.getPrestataire();
        return new LigneTableauDeBord(
                incident.getId(),
                incident.getDescription(),
                equipement.getStation().getId(),
                equipement.getStation().getNom(),
                equipement.getId(),
                equipement.getReference(),
                prestataire != null ? prestataire.getNom() : null,
                incident.niveauAttention(horloge),
                incident.dureeOuverture(horloge));
    }
}