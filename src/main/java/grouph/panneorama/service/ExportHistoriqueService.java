package grouph.panneorama.service;

import grouph.panneorama.modele.Incident;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.time.Clock;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Service
public class ExportHistoriqueService {
    private static final DateTimeFormatter FORMAT_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final List<String> EN_TETES = List.of("Numéro", "Station", "Référence équipement",
            "Type équipement", "Description", "Ouvert le", "Résolu le", "Durée (heures)", "Prestataire");

    private final IncidentService incidentService;
    private final Clock horloge;

    public ExportHistoriqueService(IncidentService incidentService, Clock horloge) {
        this.incidentService = incidentService;
        this.horloge = horloge;
    }

    // La marque UTF-8 en tête permet à Excel d'afficher correctement les accents.
    public String exporterHistoriqueCsv() {
        StringWriter contenu = new StringWriter().append(FormatCsv.MARQUE_UTF8);
        try (CSVPrinter imprimante = new CSVPrinter(contenu, FormatCsv.ECRITURE)) {
            imprimante.printRecord(EN_TETES);
            for (Incident incident : incidentService.listerHistoriqueResolus()) {
                imprimante.printRecord(ligne(incident));
            }
        } catch (IOException exception) {
            throw new UncheckedIOException("Écriture de l'export CSV impossible", exception);
        }
        return contenu.toString();
    }

    private List<Object> ligne(Incident incident) {
        return Arrays.asList(
                incident.getId(),
                incident.getEquipement().getStation().getNom(),
                incident.getEquipement().getReference(),
                incident.getEquipement().getType(),
                incident.getDescription(),
                FORMAT_DATE.format(incident.getDateOuverture()),
                FORMAT_DATE.format(incident.getDateResolution()),
                incident.dureeOuverture(horloge).toHours(),
                incident.getPrestataire() != null ? incident.getPrestataire().getNom() : "");
    }
}
