package grouph.panneorama.modele;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

class NiveauAttentionTest {

    @Test
    void uneDureeNulleEstVerte() {
        assertThat(NiveauAttention.pourIncidentNonAffecte(Duration.ZERO)).isEqualTo(NiveauAttention.VERT);
    }

    @Test
    void justeAvant48HeuresEstVert() {
        assertThat(NiveauAttention.pourIncidentNonAffecte(Duration.ofHours(48).minusSeconds(1)))
                .isEqualTo(NiveauAttention.VERT);
    }

    @Test
    void justeAvant72HeuresEstOrange() {
        assertThat(NiveauAttention.pourIncidentNonAffecte(Duration.ofHours(72).minusSeconds(1)))
                .isEqualTo(NiveauAttention.ORANGE);
    }
}