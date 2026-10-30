package grouph.panneorama.controleur;

import grouph.panneorama.modele.Equipement;
import grouph.panneorama.service.EquipementService;
import grouph.panneorama.service.IncidentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/equipements")
public class EquipementControleur {

    private final EquipementService equipementService;
    private final IncidentService incidentService;

    public EquipementControleur(EquipementService equipementService,
                                IncidentService incidentService) {
        this.equipementService = equipementService;
        this.incidentService = incidentService;
    }


    @GetMapping("/{id}")
    public String afficherFiche(@PathVariable Integer id, Model model) {
        Equipement equipement = equipementService.trouverEquipement(id);
        model.addAttribute("equipement", equipement);
        model.addAttribute("incidents", incidentService.listerParEquipement(id));
        return "equipements/fiche";
    }
}