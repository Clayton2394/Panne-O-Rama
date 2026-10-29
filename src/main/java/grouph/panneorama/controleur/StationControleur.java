package grouph.panneorama.controleur;

import grouph.panneorama.service.EquipementService;
import grouph.panneorama.service.StationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/stations")
public class StationControleur {

    private final StationService stationService;
    private final EquipementService equipementService;

    public StationControleur(StationService stationService,
                             EquipementService equipementService) {
        this.stationService = stationService;
        this.equipementService = equipementService;
    }

    @GetMapping
    public String listerStations(Model model) {
        model.addAttribute("stations", stationService.listerStations());
        return "stations/liste";
    }

    @GetMapping("/{id}/equipements")
    public String listerEquipements(@PathVariable Integer id, Model model) {
        model.addAttribute("station", stationService.trouverStation(id));
        model.addAttribute("equipements", equipementService.listerParStation(id));
        return "stations/equipements";
    }
}
