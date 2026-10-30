package grouph.panneorama.service;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import grouph.panneorama.exception.EquipementIntrouvableException;
import grouph.panneorama.modele.Equipement;
import grouph.panneorama.repository.EquipementRepository;

class EquipementServiceTest {
    private EquipementRepository equipementRepository;
    private EquipementService equipementService;

    @Test
    void trouverEquipement_inexistant_leveUneException() {
        when(equipementRepository.chargerAvecStation(999)).thenReturn(Optional.empty());

        assertThrows(EquipementIntrouvableException.class,
                () -> equipementService.trouverEquipement(999));
    }

    @Test
    void trouverEquipement_existant_renvoieLEquipement() {
        Equipement equipement = new Equipement();
        when(equipementRepository.chargerAvecStation(1)).thenReturn(Optional.of(equipement));

        assertSame(equipement, equipementService.trouverEquipement(1));
    }
}


