package grouph.panneorama.service;

import grouph.panneorama.dto.PrestataireNote;
import grouph.panneorama.exception.PrestataireIntrouvableException;
import grouph.panneorama.modele.Prestataire;
import grouph.panneorama.repository.PrestataireRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PrestataireService {
    private final PrestataireRepository prestataireRepository;

    public PrestataireService(PrestataireRepository prestataireRepository) {
        this.prestataireRepository = prestataireRepository;
    }

    // F7 : le responsable choisit un prestataire en voyant la qualité de ses interventions passées (F10).
    public List<PrestataireNote> listerPrestatairesAvecNote() {
        return prestataireRepository.listerAvecNoteMoyenne();
    }

    public Prestataire trouverPrestataire(Integer prestataireId) {
        return prestataireRepository.findById(prestataireId)
                .orElseThrow(() -> new PrestataireIntrouvableException(prestataireId));
    }
}
