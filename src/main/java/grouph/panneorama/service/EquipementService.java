package grouph.panneorama.service;

import grouph.panneorama.exception.EquipementIntrouvableException;
import grouph.panneorama.modele.Equipement;
import grouph.panneorama.repository.EquipementRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EquipementService {
    private final EquipementRepository equipementRepository;
    private final StationService stationService;

    public EquipementService(EquipementRepository equipementRepository, StationService stationService) {
        this.equipementRepository = equipementRepository;
        this.stationService = stationService;

    }

    public Equipement trouverEquipement(Integer equipementId) {
        return equipementRepository.findById(equipementId)
                .orElseThrow(() -> new EquipementIntrouvableException(equipementId));
    }

    public List<Equipement> listerParStation(Integer stationId) {
        stationService.trouverStation(stationId);
        return equipementRepository.findByStationIdOrderByDescriptionAsc(stationId);
    }
}
