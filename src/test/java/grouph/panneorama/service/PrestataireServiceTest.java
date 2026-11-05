package grouph.panneorama.service;

import grouph.panneorama.modele.Evaluation;
import grouph.panneorama.modele.SurveillancePrestataire;
import grouph.panneorama.repository.EvaluationRepository;
import grouph.panneorama.repository.PrestataireRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Limit;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PrestataireServiceTest {
    private static final Integer PRESTATAIRE_ID = 1;

    private EvaluationRepository evaluationRepository;
    private PrestataireService prestataireService;

    @BeforeEach
    void initialiser() {
        evaluationRepository = mock(EvaluationRepository.class);
        prestataireService = new PrestataireService(mock(PrestataireRepository.class), evaluationRepository);
    }

    @Test
    void lesDernieresEvaluationsSontLimiteesALaFenetreDeSurveillance() {
        List<Evaluation> evaluations = List.of(mock(Evaluation.class));
        when(evaluationRepository.listerPlusRecentesDuPrestataire(PRESTATAIRE_ID,
                Limit.of(SurveillancePrestataire.TAILLE_FENETRE))).thenReturn(evaluations);

        assertThat(prestataireService.listerDernieresEvaluations(PRESTATAIRE_ID)).isSameAs(evaluations);
    }
}
