package be.ulb.stib.data;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.lang.AutoCloseable;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;


public class CsvReader implements AutoCloseable {

    private final CSVReader csv;
    private final String[] headers;

    /**
     * Ouvre un fichier CSV en UTF‑8 et lit la ligne d’en‑tête.
     */
    public CsvReader(Path csvPath) throws IOException {
        Reader r = Files.newBufferedReader(csvPath, StandardCharsets.UTF_8);
        this.csv = new CSVReader(r);
        this.headers = next();
        if (headers == null) throw new IOException("Fichier CSV vide: " + csvPath);
    }

    /**
     * Renvoie l’en‑tête du fichier.
     */
    public String[] getHeaders() {
        return headers;
    }

    /**
     * Renvoie la prochaine ligne du CSV ou null si fin du fichier.
     */
    private String[] next() throws IOException {
        try {
            return csv.readNext();
        } catch (CsvValidationException e) {
            throw new IOException(e);
        }
    }

    /**
     * Parcourt toutes les lignes du CSV et applique une action.
     */
    public void forEach(Consumer<String[]> action) throws IOException {
        String[] row;
        while ((row = next()) != null) {
            action.accept(row);
        }
        close();
    }

    @Override
    public void close() throws IOException {
        csv.close();
    }
}
