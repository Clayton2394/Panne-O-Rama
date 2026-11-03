package grouph.panneorama.controleur;

import grouph.panneorama.modele.NiveauAttention;
import grouph.panneorama.service.StationService;
import grouph.panneorama.service.TableauDeBordService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class TableauDeBordControleur {
    private final TableauDeBordService tableauDeBordService;
    private final StationService stationService;

    public TableauDeBordControleur(TableauDeBordService tableauDeBordService, StationService stationService) {
        this.tableauDeBordService = tableauDeBordService;
        this.stationService = stationService;
    }

    @GetMapping("/tableau-de-bord")
    public String afficher(@RequestParam(required = false) Integer stationId,
                           @RequestParam(required = false) NiveauAttention niveau,
                           Model model) {
        model.addAttribute("tableau", tableauDeBordService.construire(stationId, niveau));
        model.addAttribute("stations", stationService.listerStations());
        model.addAttribute("niveaux", NiveauAttention.values());
        model.addAttribute("stationChoisie", stationId);
        model.addAttribute("niveauChoisi", niveau);
        return "tableauDeBord";
    }
}