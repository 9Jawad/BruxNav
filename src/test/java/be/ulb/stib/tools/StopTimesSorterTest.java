package be.ulb.stib.tools;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class StopTimesSorterTest {

    @TempDir Path tmp;

    @Test
    void sortAndReplace() throws IOException {

        Path csv = UtilsForTest.copyToTemp("sort.csv", tmp);
        long nLines = Files.lines(csv).count();
        StopTimesSorter.sortAndReplace(csv);

        // TEST 1 : taille inchangée
        List<String> lines = Files.readAllLines(csv);
        assertEquals(nLines, lines.size());

        // TEST 2 : header inchangé
        String header = lines.getFirst();
        assertTrue(header.startsWith("trip_id"));

        // TEST 3 : vérifie trip_id groupé ET stop_sequence croissant
        String prevTrip = "";
        int    prevSeq  = -1;

        // System.out.println("\n=== .csv sorted ===");
        for (int i = 1; i < lines.size(); i++) {
            // System.out.println(lines.get(i));
            String[] parts = lines.get(i).split(",", 4);

            String trip = parts[0];
            int seq = Integer.parseInt(parts[3]);

            if (trip.equals(prevTrip)) assertTrue(seq >= prevSeq);              // croissant
            else { assertTrue(trip.compareTo(prevTrip) > 0); prevTrip = trip; } // groupé
            prevSeq = seq;
        }
        // System.out.println("===================\n");
    }
}
