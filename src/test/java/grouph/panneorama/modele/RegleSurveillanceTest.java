package grouph.panneorama.modele;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import static org.assertj.core.api.Assertions.within;
import static org.assertj.core.api.Assertions.assertThat;

class RegleSurveillanceTest {

    private List<Integer> plusRecentEnPremier(Integer... notesDuPlusAncienAuPlusRecent) {
        List<Integer> notes = new ArrayList<>(List.of(notesDuPlusAncienAuPlusRecent));
        Collections.reverse(notes);
        return notes;
    }

    @Test
    void sansAucuneEvaluationLaMoyenneNEstPasCalculee() {
        assertThat(RegleSurveillance.moyenne(List.of())).isEmpty();
        assertThat(RegleSurveillance.estSousSurveillance(List.of())).isFalse();
    }

    @Test
    void avecQuatreEvaluationsLaMoyenneNEstPasCalculee() {
        List<Integer> notes = List.of(1, 1, 1, 4);

        assertThat(RegleSurveillance.moyenne(notes)).isEmpty();
    }

    @Test
    void quatreEvaluationsTresMauvaisesNeMettentJamaisSousSurveillance() {
        List<Integer> notes = List.of(1, 1, 1, 1);

        assertThat(RegleSurveillance.estSousSurveillance(notes)).isFalse();
    }

    @Test
    void avecCinqEvaluationsLaMoyenneEstCalculee() {
        List<Integer> notes = List.of(5, 5, 5, 5, 5);

        assertThat(RegleSurveillance.moyenne(notes)).isPresent();
    }

    @Test
    void uneMoyenneDeTroisExactementNEstPasSousSurveillance() {
        List<Integer> notes = plusRecentEnPremier(3, 3, 3, 3, 3);

        assertThat(RegleSurveillance.moyenne(notes)).contains(3.0);
        assertThat(RegleSurveillance.estSousSurveillance(notes)).isFalse();
    }

    @Test
    void uneMoyenneDeDeuxVirguleHuitEstSousSurveillance() {
        List<Integer> notes = plusRecentEnPremier(2, 3, 3, 3, 3);

        assertThat(RegleSurveillance.moyenne(notes).orElseThrow()).isCloseTo(2.8, within(0.001));
        assertThat(RegleSurveillance.estSousSurveillance(notes)).isTrue();
    }

    @Test
    void uneMoyenneDeTroisVirguleQuatreNEstPasSousSurveillance() {
        List<Integer> notes = plusRecentEnPremier(1, 1, 5, 5, 5);

        assertThat(RegleSurveillance.moyenne(notes).orElseThrow()).isCloseTo(3.4, within(0.001));
        assertThat(RegleSurveillance.estSousSurveillance(notes)).isFalse();
    }

    @Test
    void leSeuilSeJoueALUniteDeSommeDePres() {
        // somme 14 -> 2,8 : surveillé ; somme 15 -> 3,0 : non ; somme 16 -> 3,2 : non
        assertThat(RegleSurveillance.estSousSurveillance(plusRecentEnPremier(2, 3, 3, 3, 3))).isTrue();
        assertThat(RegleSurveillance.estSousSurveillance(plusRecentEnPremier(3, 3, 3, 3, 3))).isFalse();
        assertThat(RegleSurveillance.estSousSurveillance(plusRecentEnPremier(3, 3, 3, 3, 4))).isFalse();
    }
}