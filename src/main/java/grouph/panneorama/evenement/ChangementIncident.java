package grouph.panneorama.evenement;

import grouph.panneorama.modele.Incident;
import grouph.panneorama.modele.TypeEvenementIncident;

public record ChangementIncident(Incident incident, TypeEvenementIncident type, String detail) {
}