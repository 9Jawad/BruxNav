package be.ulb.stib.data;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;


class CsvReaderTest {

    @TempDir Path tmp;  // répertoire temporaire

    // ---------- Test 1 : lecture header ----------
    @Test
    void readHeader() throws IOException {
        Path csv = Resources.copyToTemp("mini.csv", tmp);
        try (CsvReader reader = new CsvReader(csv)) {
            assertArrayEquals(new String[]{"id","name","type"}, reader.getHeaders());
        }
    }

    // ---------- Test 2 : lecture lignes ----------
    @Test
    void forEachCheckLines() throws IOException {
        Path csv = Resources.copyToTemp("mini.csv", tmp);
        try (CsvReader reader = new CsvReader(csv)) {
            reader.forEach(line -> readingLine(line));
        }
    }

    private void readingLine(String[] line) {   // hard-codé
        assertTrue(line.length == 3);
        if (line[0].equals("1")) {
            assertEquals("Berchem Station", line[1]);
            assertEquals("BUS",             line[2]);
        }
        else if (line[0].equals("2")) {
            assertEquals("Aalst", line[1]);
            assertEquals("TRAM",  line[2]);
        }
        else if (line[0].equals("3")) {
            assertEquals("Dilbeek", line[1]);
            assertEquals("TRAIN",   line[2]);
        } else fail("Ligne inattendue");
    }

    // ---------- Test 3 : fichier vide ----------
    @Test
    void emptyFileThrows() throws IOException {
        Path empty = Files.createFile(tmp.resolve("empty.csv"));
        assertThrows(IOException.class, () -> new CsvReader(empty));
    }
}
