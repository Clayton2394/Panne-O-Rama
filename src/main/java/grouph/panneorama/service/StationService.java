package grouph.panneorama.service;


import grouph.panneorama.exception.StationIntrouvableException;
import grouph.panneorama.modele.Station;
import grouph.panneorama.repository.StationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StationService {

    private final StationRepository stationRepository;

    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    public List<Station> listerStations() {
        return stationRepository.findAllByOrderByNomAsc();
    }

    public Station trouverStation(Integer id) {
        return stationRepository.findById(id)
                .orElseThrow(() -> new StationIntrouvableException(id));
    }
}
