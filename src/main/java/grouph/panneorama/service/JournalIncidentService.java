package grouph.panneorama.service;

import grouph.panneorama.modele.EvenementIncident;
import grouph.panneorama.modele.Incident;
import grouph.panneorama.modele.TypeEvenementIncident;
import grouph.panneorama.repository.EvenementIncidentRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class JournalIncidentService {
    private static final int NOMBRE_EVENEMENTS_RECENTS = 200;

    private final EvenementIncidentRepository evenementRepository;
    private final AuteurCourant auteurCourant;
    private final Clock horloge;

    public JournalIncidentService(EvenementIncidentRepository evenementRepository,
                                  AuteurCourant auteurCourant,
                                  Clock horloge) {
        this.evenementRepository = evenementRepository;
        this.auteurCourant = auteurCourant;
        this.horloge = horloge;
    }

    // Rejoint la transaction de la modification : le journal et le changement sont enregistrés ensemble ou pas du tout.
    @Transactional
    public void enregistrer(Incident incident, TypeEvenementIncident type, String detail) {
        EvenementIncident evenement = new EvenementIncident(
                incident, type, LocalDateTime.now(horloge), auteurCourant.identifiant(), detail);
        evenementRepository.save(evenement);
    }

    public List<EvenementIncident> consulterJournalDeLIncident(Integer incidentId) {
        return evenementRepository.findByIncidentIdOrderByDateEvenementAscIdAsc(incidentId);
    }

    public List<EvenementIncident> consulterEvenementsRecents() {
        return evenementRepository.findRecents(PageRequest.of(0, NOMBRE_EVENEMENTS_RECENTS));
    }
}