package grouph.panneorama.controleur;

import grouph.panneorama.dto.RapportImport;
import grouph.panneorama.service.ExportHistoriqueService;
import grouph.panneorama.service.ImportEquipementsService;
import grouph.panneorama.service.StationService;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;

// Lot alpha 9 : export de l'historique et import d'équipements au format CSV.
@Controller
public class DonneesCsvControleur {
    private static final MediaType TEXT_CSV = new MediaType("text", "csv", StandardCharsets.UTF_8);
    private static final String NOM_FICHIER_EXPORT = "historique-incidents.csv";

    private final ExportHistoriqueService exportHistoriqueService;
    private final ImportEquipementsService importEquipementsService;
    private final StationService stationService;

    public DonneesCsvControleur(ExportHistoriqueService exportHistoriqueService,
                                ImportEquipementsService importEquipementsService,
                                StationService stationService) {
        this.exportHistoriqueService = exportHistoriqueService;
        this.importEquipementsService = importEquipementsService;
        this.stationService = stationService;
    }

    @GetMapping("/incidents/historique/export")
    public ResponseEntity<String> exporterHistorique() {
        return ResponseEntity.ok()
                .contentType(TEXT_CSV)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(NOM_FICHIER_EXPORT).build().toString())
                .body(exportHistoriqueService.exporterHistoriqueCsv());
    }

    @GetMapping("/stations/{stationId}/equipements/import")
    public String afficherFormulaireImport(@PathVariable Integer stationId, Model model) {
        model.addAttribute("station", stationService.trouverStation(stationId));
        return "stations/importEquipements";
    }

    @PostMapping("/stations/{stationId}/equipements/import")
    public String importerEquipements(@PathVariable Integer stationId,
                                      @RequestParam("fichier") MultipartFile fichier,
                                      Model model,
                                      RedirectAttributes redirection) {
        RapportImport rapport = importer(stationId, fichier);
        if (!rapport.estReussi()) {
            model.addAttribute("station", stationService.trouverStation(stationId));
            model.addAttribute("erreurs", rapport.erreurs());
            return "stations/importEquipements";
        }
        redirection.addFlashAttribute("messageSucces",
                rapport.nombreImportes() + " équipement(s) importé(s) avec succès");
        return "redirect:/stations/" + stationId + "/equipements";
    }

    private RapportImport importer(Integer stationId, MultipartFile fichier) {
        if (fichier.isEmpty()) {
            return RapportImport.refuse(List.of("Veuillez choisir un fichier CSV non vide"));
        }
        try (Reader contenu = new InputStreamReader(fichier.getInputStream(), StandardCharsets.UTF_8)) {
            return importEquipementsService.importerEquipements(stationId, contenu);
        } catch (IOException exception) {
            return RapportImport.refuse(List.of("Le fichier n'a pas pu être lu"));
        }
    }
}
