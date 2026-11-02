package grouph.panneorama.controleur;

import grouph.panneorama.dto.IncidentDTO;
import grouph.panneorama.modele.StatutIncident;
import grouph.panneorama.service.IncidentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/incidents")
@Tag(name = "Incidents", description = "Consultation et résolution des incidents")
public class IncidentAPIControleur {
    private final IncidentService incidentService;

    public IncidentAPIControleur(IncidentService incidentService) {
        this.incidentService = incidentService;
    }

    @GetMapping
    @Operation(summary = "Lister les incidents, avec un filtre facultatif sur le statut")
    public List<IncidentDTO> lister(@RequestParam(required = false) StatutIncident statut) {
        return incidentService.listerIncidents(statut).stream()
                .map(IncidentDTO::depuis)
                .toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulter un incident")
    public IncidentDTO consulter(@PathVariable Integer id) {
        return IncidentDTO.depuis(incidentService.trouverIncident(id));
    }

    @PatchMapping("/{id}/resolution")
    @Operation(summary = "Déclarer un incident comme résolu")
    public IncidentDTO resoudre(@PathVariable Integer id) {
        return IncidentDTO.depuis(incidentService.resoudreIncident(id));
    }
}