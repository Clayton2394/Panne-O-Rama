package grouph.panneorama.service;

import grouph.panneorama.exception.EquipementIntrouvableException;
import grouph.panneorama.modele.Equipement;
import grouph.panneorama.repository.EquipementRepository;
import org.springframework.stereotype.Service;

@Service
public class EquipementService {
    private final EquipementRepository equipementRepository;

    public EquipementService(EquipementRepository equipementRepository) {
        this.equipementRepository = equipementRepository;
    }

    public Equipement trouverEquipement(Integer equipementId) {
        return equipementRepository.findById(equipementId)
                .orElseThrow(() -> new EquipementIntrouvableException(equipementId));
    }
}
