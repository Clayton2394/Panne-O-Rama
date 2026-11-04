package grouph.panneorama.service;

import org.apache.commons.csv.CSVFormat;

// Lot alpha 9 : un seul format CSV pour l'import et l'export, lisible directement par Excel en français.
final class FormatCsv {
    static final char SEPARATEUR = ';';
    static final String MARQUE_UTF8 = "﻿";

    static final CSVFormat ECRITURE = CSVFormat.DEFAULT.builder()
            .setDelimiter(SEPARATEUR)
            .get();

    static final CSVFormat LECTURE = CSVFormat.DEFAULT.builder()
            .setDelimiter(SEPARATEUR)
            .setHeader()
            .setSkipHeaderRecord(true)
            .setIgnoreHeaderCase(true)
            .setIgnoreEmptyLines(true)
            .setTrim(true)
            .get();

    private FormatCsv() {
    }
}
