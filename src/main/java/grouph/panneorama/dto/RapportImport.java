package grouph.panneorama.dto;

import java.util.List;

// Lot alpha 9 : l'import est tout ou rien, soit des équipements importés, soit la liste des erreurs à corriger.
public record RapportImport(int nombreImportes, List<String> erreurs) {

    public static RapportImport reussi(int nombreImportes) {
        return new RapportImport(nombreImportes, List.of());
    }

    public static RapportImport refuse(List<String> erreurs) {
        return new RapportImport(0, List.copyOf(erreurs));
    }

    public boolean estReussi() {
        return erreurs.isEmpty();
    }
}
