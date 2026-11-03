package grouph.panneorama.dto;

import java.util.List;

public record TableauDeBord(List<LigneTableauDeBord> lignes, IndicateursTableauDeBord indicateurs) {
}