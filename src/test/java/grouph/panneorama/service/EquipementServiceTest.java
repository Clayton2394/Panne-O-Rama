package grouph.panneorama.service;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import grouph.panneorama.exception.EquipementIntrouvableException;
import grouph.panneorama.modele.Equipement;
import grouph.panneorama.repository.EquipementRepository;

class EquipementServiceTest {
    private EquipementRepository equipementRepository;
    private EquipementService equipementService;

    @BeforeEach
    void initialiser() {
        equipementRepository = mock(EquipementRepository.class);
        equipementService = new EquipementService(equipementRepository, mock(StationService.class));
    }

    @Test
    void trouverEquipement_inexistant_leveUneException() {
        when(equipementRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(EquipementIntrouvableException.class,
                () -> equipementService.trouverEquipement(999));
    }

    @Test
    void trouverEquipement_existant_renvoieLEquipement() {
        Equipement equipement = new Equipement();
        when(equipementRepository.findById(1)).thenReturn(Optional.of(equipement));

        assertSame(equipement, equipementService.trouverEquipement(1));
    }
}
