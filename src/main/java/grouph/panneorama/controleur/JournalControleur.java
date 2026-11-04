package grouph.panneorama.controleur;

import grouph.panneorama.modele.Incident;
import grouph.panneorama.service.IncidentService;
import grouph.panneorama.service.JournalIncidentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class JournalControleur {
    private final JournalIncidentService journalIncidentService;
    private final IncidentService incidentService;

    public JournalControleur(JournalIncidentService journalIncidentService, IncidentService incidentService) {
        this.journalIncidentService = journalIncidentService;
        this.incidentService = incidentService;
    }

    @GetMapping("/journal")
    public String afficherJournalGlobal(Model model) {
        model.addAttribute("titre", "Journal des incidents");
        model.addAttribute("evenements", journalIncidentService.consulterEvenementsRecents());
        return "journal";
    }

    @GetMapping("/incidents/{incidentId}/journal")
    public String afficherJournalDeLIncident(@PathVariable Integer incidentId, Model model) {
        Incident incident = incidentService.trouverIncident(incidentId);
        model.addAttribute("titre", "Journal de l'incident n° " + incident.getId());
        model.addAttribute("incident", incident);
        model.addAttribute("evenements", journalIncidentService.consulterJournalDeLIncident(incidentId));
        return "journal";
    }
}