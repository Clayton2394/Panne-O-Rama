package grouph.panneorama.service;

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

    public List<Prestataire> listerPrestataires() {
        return prestataireRepository.findAllByOrderByNomAsc();
    }

    public Prestataire trouverPrestataire(Integer prestataireId) {
        return prestataireRepository.findById(prestataireId)
                .orElseThrow(() -> new PrestataireIntrouvableException(prestataireId));
    }
}
