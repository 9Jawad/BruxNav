package be.ulb.stib.tools;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;


public final class StopTimesSorter {

    public static void sortAndReplace(Path stopTimesCsv) throws IOException {

        Path tmpFile = Files.createTempFile(stopTimesCsv.getParent(), "stop_times_sorted", ".csv");

        // stream pour minimiser l'utilisation de la mémoire
        try (Stream<String> lines = Files.lines(stopTimesCsv);
             BufferedWriter writer = Files.newBufferedWriter(tmpFile)) {

            Iterator<String> iterator = lines.iterator();
            if (!iterator.hasNext()) throw new IOException("Erreur fichier vide");

            // écriture header
            String header = iterator.next();
            writer.write(header);
            writer.newLine();

            // traite les données (sans header)
            Stream<String> dataLines = StreamSupport.stream(
                    Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED),false );

            dataLines
                    .sorted(StopTimesSorter::compareLines)
                    .forEach(line -> {
                        try {
                            writer.write(line);
                            writer.newLine();
                        } catch (IOException e) { throw new UncheckedIOException("Erreur lors de l'écriture", e); }
                    });
        }
        // remplace le fichier original par le fichier temporaire
        Files.move(tmpFile, stopTimesCsv, StandardCopyOption.REPLACE_EXISTING);
    }

    /* ------------- helpers ------------- */

    private static int compareLines(String lineA, String lineB) {
        String[] A = lineA.split(",", 4);
        String[] B = lineB.split(",", 4);

        // compare trip_id
        int tripComparison = A[0].compareTo(B[0]);

        // compare stop_sequence
        if (tripComparison != 0) return tripComparison;
        else return Integer.compare(Integer.parseInt(A[3]), Integer.parseInt(B[3]));
    }
}
