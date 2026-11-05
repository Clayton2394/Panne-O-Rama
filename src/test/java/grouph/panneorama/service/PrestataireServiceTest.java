package grouph.panneorama.service;

import grouph.panneorama.modele.Evaluation;
import grouph.panneorama.modele.Prestataire;
import grouph.panneorama.modele.RegleSurveillance;
import grouph.panneorama.repository.EvaluationRepository;
import grouph.panneorama.repository.PrestataireRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Limit;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PrestataireServiceTest {
    private static final Integer PRESTATAIRE_ID = 1;

    private PrestataireRepository prestataireRepository;
    private EvaluationRepository evaluationRepository;
    private PrestataireService prestataireService;

    @BeforeEach
    void initialiser() {
        prestataireRepository = mock(PrestataireRepository.class);
        evaluationRepository = mock(EvaluationRepository.class);
        prestataireService = new PrestataireService(prestataireRepository, evaluationRepository);
    }

    @Test
    void seulsLesPrestatairesSousSurveillanceSontSignalesDansLaListeDAffectation() {
        Prestataire surveille = prestataireNumero(1);
        Prestataire bienNote = prestataireNumero(2);
        when(prestataireRepository.findAll()).thenReturn(List.of(surveille, bienNote));
        baseRenvoiePour(1, 2, 3, 3, 3, 3);
        baseRenvoiePour(2, 5, 5, 5, 5, 5);

        assertThat(prestataireService.listerIdsSousSurveillance()).containsExactly(1);
    }

    @Test
    void lesDernieresEvaluationsSontLimiteesALaFenetreDeSurveillance() {
        List<Evaluation> evaluations = List.of(mock(Evaluation.class));
        when(evaluationRepository.listerPlusRecentesDuPrestataire(PRESTATAIRE_ID,
                Limit.of(RegleSurveillance.TAILLE_FENETRE))).thenReturn(evaluations);

        assertThat(prestataireService.listerDernieresEvaluations(PRESTATAIRE_ID)).isSameAs(evaluations);
    }

    // Les exemples de l'énoncé, tels que la base les renvoie : les 5 dernières, de la plus récente à la plus ancienne.

    @Test
    void exemple3LaDerniereNoteFaitBasculerSousSurveillance() {
        // 3, 3, 3, 3, 3, 2 : les 5 dernières sont 3, 3, 3, 3, 2, moyenne 2,8.
        baseRenvoieDuPlusRecentAuPlusAncien(2, 3, 3, 3, 3);

        assertThat(prestataireService.estSousSurveillance(PRESTATAIRE_ID)).isTrue();
    }

    @Test
    void exemple4LaFenetreGlisseEtLePrestataireSortDeLaSurveillance() {
        // 3, 3, 3, 3, 3, 2, 5 : les 5 dernières sont 3, 3, 3, 2, 5, moyenne 3,2.
        baseRenvoieDuPlusRecentAuPlusAncien(5, 2, 3, 3, 3);

        assertThat(prestataireService.estSousSurveillance(PRESTATAIRE_ID)).isFalse();
    }

    @Test
    void exemple5UneMauvaiseNoteSortieDeLaFenetreNeComptePlus() {
        // 1, 5, 5, 5, 5, 5 : le 1 est hors des 5 dernières.
        baseRenvoieDuPlusRecentAuPlusAncien(5, 5, 5, 5, 5);

        assertThat(prestataireService.estSousSurveillance(PRESTATAIRE_ID)).isFalse();
    }

    @Test
    void exemple1MoinsDeCinqEvaluationsNeMettentJamaisSousSurveillance() {
        // 4, 1, 1, 1 : moyenne 1,75 mais seulement 4 évaluations (PS2).
        baseRenvoieDuPlusRecentAuPlusAncien(1, 1, 1, 4);

        assertThat(prestataireService.estSousSurveillance(PRESTATAIRE_ID)).isFalse();
    }

    private void baseRenvoieDuPlusRecentAuPlusAncien(Integer... notes) {
        baseRenvoiePour(PRESTATAIRE_ID, notes);
    }

    private void baseRenvoiePour(Integer prestataireId, Integer... notesDuPlusRecentAuPlusAncien) {
        List<Evaluation> evaluations = Arrays.stream(notesDuPlusRecentAuPlusAncien).map(this::evaluationNotee).toList();
        when(evaluationRepository.listerPlusRecentesDuPrestataire(eq(prestataireId), any(Limit.class)))
                .thenReturn(evaluations);
    }

    private Prestataire prestataireNumero(Integer id) {
        Prestataire prestataire = new Prestataire();
        prestataire.setId(id);
        return prestataire;
    }

    private Evaluation evaluationNotee(Integer note) {
        Evaluation evaluation = mock(Evaluation.class);
        when(evaluation.getNote()).thenReturn(note);
        return evaluation;
    }
}
