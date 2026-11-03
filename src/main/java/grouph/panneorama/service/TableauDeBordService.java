package grouph.panneorama.service;

import grouph.panneorama.dto.IndicateursTableauDeBord;
import grouph.panneorama.dto.LigneTableauDeBord;
import grouph.panneorama.dto.TableauDeBord;
import grouph.panneorama.modele.NiveauAttention;
import grouph.panneorama.repository.IncidentRepository;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.util.Comparator;
import java.util.List;

@Service
public class TableauDeBordService {
    private static final Comparator<LigneTableauDeBord> DU_PLUS_URGENT_AU_MOINS_URGENT =
            Comparator.comparingInt((LigneTableauDeBord ligne) -> ligne.niveau().getPriorite()).reversed()
                    .thenComparing(LigneTableauDeBord::dureeOuverture, Comparator.reverseOrder());

    private final IncidentRepository incidentRepository;
    private final Clock horloge;

    public TableauDeBordService(IncidentRepository incidentRepository, Clock horloge) {
        this.incidentRepository = incidentRepository;
        this.horloge = horloge;
    }

    public TableauDeBord construire(Integer stationId, NiveauAttention niveauFiltre) {
        List<LigneTableauDeBord> toutesLesLignes = incidentRepository.findActifsAvecDetails().stream()
                .map(incident -> LigneTableauDeBord.depuis(incident, horloge))
                .toList();

        List<LigneTableauDeBord> lignesAffichees = toutesLesLignes.stream()
                .filter(ligne -> stationId == null || ligne.stationId().equals(stationId))
                .filter(ligne -> niveauFiltre == null || ligne.niveau() == niveauFiltre)
                .sorted(DU_PLUS_URGENT_AU_MOINS_URGENT)
                .toList();

        return new TableauDeBord(lignesAffichees, IndicateursTableauDeBord.depuis(toutesLesLignes));
    }
}