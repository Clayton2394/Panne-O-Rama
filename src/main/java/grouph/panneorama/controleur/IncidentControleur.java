package grouph.panneorama.controleur;

import grouph.panneorama.formulaire.AffectationPrestataireFormulaire;
import grouph.panneorama.formulaire.DeclarationIncidentFormulaire;
import grouph.panneorama.formulaire.DescriptionIncidentFormulaire;
import grouph.panneorama.formulaire.EvaluationFormulaire;
import grouph.panneorama.modele.Evaluation;
import grouph.panneorama.modele.Incident;
import grouph.panneorama.service.EquipementService;
import grouph.panneorama.service.IncidentService;
import grouph.panneorama.service.PrestataireService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class IncidentControleur {
    private final IncidentService incidentService;
    private final EquipementService equipementService;
    private final PrestataireService prestataireService;

    public IncidentControleur(IncidentService incidentService,
                              EquipementService equipementService,
                              PrestataireService prestataireService) {
        this.incidentService = incidentService;
        this.equipementService = equipementService;
        this.prestataireService = prestataireService;
    }

    @GetMapping("/equipements/{equipementId}/incidents/nouveau")
    public String afficherFormulaireDeclaration(@PathVariable Integer equipementId, Model model) {
        model.addAttribute("equipement", equipementService.trouverEquipement(equipementId));
        model.addAttribute("formulaire", new DescriptionIncidentFormulaire());
        return "declarationIncident";
    }

    @PostMapping("/equipements/{equipementId}/incidents")
    public String declarerIncident(@PathVariable Integer equipementId,
                                   @Valid @ModelAttribute("formulaire") DescriptionIncidentFormulaire formulaire,
                                   BindingResult resultatValidation,
                                   Model model) {
        if (resultatValidation.hasErrors()) {
            model.addAttribute("equipement", equipementService.trouverEquipement(equipementId));
            return "declarationIncident";
        }
        Incident incident = incidentService.declarerIncident(equipementId, formulaire.getDescription());
        return "redirect:/incidents/" + incident.getId();
    }

    // F4 depuis l'accueil ou le bandeau : l'équipement est choisi dans le formulaire.
    @GetMapping("/incidents/nouveau")
    public String afficherFormulaireNouvelIncident(Model model) {
        model.addAttribute("equipementsParStation", equipementService.listerRegroupesParStation());
        model.addAttribute("formulaire", new DeclarationIncidentFormulaire());
        return "nouvelIncident";
    }

    @PostMapping("/incidents")
    public String declarerNouvelIncident(@Valid @ModelAttribute("formulaire") DeclarationIncidentFormulaire formulaire,
                                         BindingResult resultatValidation,
                                         Model model) {
        if (resultatValidation.hasErrors()) {
            model.addAttribute("equipementsParStation", equipementService.listerRegroupesParStation());
            return "nouvelIncident";
        }
        Incident incident = incidentService.declarerIncident(formulaire.getEquipementId(), formulaire.getDescription());
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

    @GetMapping("/incidents/{incidentId}/modification")
    public String afficherFormulaireModification(@PathVariable Integer incidentId, Model model) {
        Incident incident = incidentService.trouverIncidentModifiable(incidentId);
        DescriptionIncidentFormulaire formulaire = new DescriptionIncidentFormulaire();
        formulaire.setDescription(incident.getDescription());
        model.addAttribute("incident", incident);
        model.addAttribute("formulaire", formulaire);
        return "modificationIncident";
    }

    @PatchMapping("/incidents/{incidentId}")
    public String modifierIncident(@PathVariable Integer incidentId,
                                   @Valid @ModelAttribute("formulaire") DescriptionIncidentFormulaire formulaire,
                                   BindingResult resultatValidation,
                                   Model model) {
        if (resultatValidation.hasErrors()) {
            model.addAttribute("incident", incidentService.trouverIncidentModifiable(incidentId));
            return "modificationIncident";
        }
        incidentService.modifierDescription(incidentId, formulaire.getDescription());
        return "redirect:/incidents/" + incidentId;
    }

    @GetMapping("/incidents/{incidentId}/affectation")
    public String afficherFormulaireAffectation(@PathVariable Integer incidentId, Model model) {
        Incident incident = incidentService.trouverIncidentAffectable(incidentId);
        AffectationPrestataireFormulaire formulaire = new AffectationPrestataireFormulaire();
        if (incident.getPrestataire() != null) {
            formulaire.setPrestataireId(incident.getPrestataire().getId());
        }
        model.addAttribute("incident", incident);
        model.addAttribute("prestataires", prestataireService.listerPrestatairesAvecNote());
        model.addAttribute("formulaire", formulaire);
        return "affectationIncident";
    }

    @PatchMapping("/incidents/{incidentId}/prestataire")
    public String affecterPrestataire(@PathVariable Integer incidentId,
                                      @Valid @ModelAttribute("formulaire") AffectationPrestataireFormulaire formulaire,
                                      BindingResult resultatValidation,
                                      Model model) {
        if (resultatValidation.hasErrors()) {
            model.addAttribute("incident", incidentService.trouverIncidentAffectable(incidentId));
            model.addAttribute("prestataires", prestataireService.listerPrestatairesAvecNote());
            return "affectationIncident";
        }
        incidentService.affecterPrestataire(incidentId, formulaire.getPrestataireId());
        return "redirect:/incidents/" + incidentId;
    }

    @PatchMapping("/incidents/{incidentId}/resolution")
    public String resoudreIncident(@PathVariable Integer incidentId) {
        incidentService.resoudreIncident(incidentId);
        return "redirect:/incidents/" + incidentId;
    }

    @GetMapping("/incidents/{incidentId}/evaluation")
    public String afficherFormulaireEvaluation(@PathVariable Integer incidentId, Model model) {
        model.addAttribute("incident", incidentService.trouverIncidentEvaluable(incidentId));
        model.addAttribute("notesPossibles", Evaluation.notesPossibles());
        model.addAttribute("formulaire", new EvaluationFormulaire());
        return "evaluationIncident";
    }

    @PostMapping("/incidents/{incidentId}/evaluation")
    public String evaluerPrestataire(@PathVariable Integer incidentId,
                                     @Valid @ModelAttribute("formulaire") EvaluationFormulaire formulaire,
                                     BindingResult resultatValidation,
                                     Model model) {
        if (resultatValidation.hasErrors()) {
            model.addAttribute("incident", incidentService.trouverIncidentEvaluable(incidentId));
            model.addAttribute("notesPossibles", Evaluation.notesPossibles());
            return "evaluationIncident";
        }
        incidentService.evaluerPrestataire(incidentId, formulaire.getNote(), formulaire.getCommentaire());
        return "redirect:/incidents/" + incidentId;
    }

    @GetMapping("/incidents/historique")
    public String afficherHistorique(Model model) {
        model.addAttribute("incidents", incidentService.listerHistoriqueResolus());
        return "historiqueIncidents";
    }
}
