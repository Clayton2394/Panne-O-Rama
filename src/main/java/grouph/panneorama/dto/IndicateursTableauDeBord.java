package grouph.panneorama.dto;

import grouph.panneorama.modele.NiveauAttention;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record IndicateursTableauDeBord(long total, long rouges, long oranges, long verts, long prisEnCharge) {

    public static IndicateursTableauDeBord depuis(List<LigneTableauDeBord> lignes) {
        Map<NiveauAttention, Long> parNiveau = lignes.stream()
                .collect(Collectors.groupingBy(LigneTableauDeBord::niveau, Collectors.counting()));
        return new IndicateursTableauDeBord(
                lignes.size(),
                parNiveau.getOrDefault(NiveauAttention.ROUGE, 0L),
                parNiveau.getOrDefault(NiveauAttention.ORANGE, 0L),
                parNiveau.getOrDefault(NiveauAttention.VERT, 0L),
                parNiveau.getOrDefault(NiveauAttention.PRIS_EN_CHARGE, 0L));
    }
}