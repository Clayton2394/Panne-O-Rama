package grouph.panneorama.dto;

// Projection de la requête native : un prestataire et la synthèse de ses évaluations (F10).
public interface PrestataireNote {

    Integer getId();

    String getNom();

    String getContact();

    // Absente (null) tant que le prestataire n'a jamais été évalué.
    Double getNoteMoyenne();

    Long getNombreEvaluations();

    default boolean estEvalue() {
        return getNombreEvaluations() > 0;
    }
}
