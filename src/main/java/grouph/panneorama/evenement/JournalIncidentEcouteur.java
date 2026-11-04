package grouph.panneorama.evenement;

import grouph.panneorama.service.JournalIncidentService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class JournalIncidentEcouteur {
    private final JournalIncidentService journalIncidentService;

    public JournalIncidentEcouteur(JournalIncidentService journalIncidentService) {
        this.journalIncidentService = journalIncidentService;
    }

    @EventListener
    public void surChangementIncident(ChangementIncident changement) {
        journalIncidentService.enregistrer(changement.incident(), changement.type(), changement.detail());
    }
}