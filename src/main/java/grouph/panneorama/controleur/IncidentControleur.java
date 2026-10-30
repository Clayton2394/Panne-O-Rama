package grouph.panneorama.controleur;

import grouph.panneorama.formulaire.DeclarationIncidentFormulaire;
import grouph.panneorama.modele.Incident;
import grouph.panneorama.service.EquipementService;
import grouph.panneorama.service.IncidentService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class IncidentControleur {
    private final IncidentService incidentService;
    private final EquipementService equipementService;

    public IncidentControleur(IncidentService incidentService, EquipementService equipementService) {
        this.incidentService = incidentService;
        this.equipementService = equipementService;
    }

    @GetMapping("/equipements/{equipementId}/incidents/nouveau")
    public String afficherFormulaireDeclaration(@PathVariable Integer equipementId, Model model) {
        model.addAttribute("equipement", equipementService.trouverEquipement(equipementId));
        model.addAttribute("formulaire", new DeclarationIncidentFormulaire());
        return "declarationIncident";
    }

    @PostMapping("/equipements/{equipementId}/incidents")
    public String declarerIncident(@PathVariable Integer equipementId,
                                   @Valid @ModelAttribute("formulaire") DeclarationIncidentFormulaire formulaire,
                                   BindingResult resultatValidation,
                                   Model model) {
        if (resultatValidation.hasErrors()) {
            model.addAttribute("equipement", equipementService.trouverEquipement(equipementId));
            return "declarationIncident";
        }
        Incident incident = incidentService.declarerIncident(equipementId, formulaire.getDescription());
        return "redirect:/incidents/" + incident.getId();
    }

    @GetMapping("/incidents/{incidentId}")
    public String afficherDetailIncident(@PathVariable Integer incidentId, Model model) {
        Incident incident = incidentService.trouverIncident(incidentId);
        model.addAttribute("incident", incident);
        model.addAttribute("dureeOuverture", incidentService.calculerDureeOuverture(incident));
        incidentService.trouverEvaluation(incidentId)
                .ifPresent(evaluation -> model.addAttribute("evaluation", evaluation));
        return "detailIncident";
    }

    @GetMapping("/incidents/historique")
    public String afficherHistorique(Model model) {
        model.addAttribute("incidents", incidentService.listerHistoriqueResolus());
        return "historiqueIncidents";
    }
}
