package be.ulb.stib.data;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;


class CsvReaderTest {

    @TempDir Path tmp;  // répertoire temporaire

    // ---------- Test 1 : lecture header ----------
    @Test
    void readHeader() throws IOException {
        Path csv = copyResource("mini.csv");
        try (CsvReader reader = new CsvReader(csv)) {
            assertArrayEquals(new String[]{"id","name","age"}, reader.getHeaders());
        }
    }

    // ---------- Test 2 : forEach lecture lignes ----------
    @Test
    void forEachCheckLines() throws IOException {
        Path csv = copyResource("mini.csv");
        try (CsvReader reader = new CsvReader(csv)) {
            reader.forEach(line -> readingLine(line));
        }
    }

    private void readingLine(String[] line) {   // hard-codé
        assertTrue(line.length == 3);
        if (line[0].equals("1")) {
            assertEquals("Nath", line[1]);
            assertEquals("21", line[2]);
        }
        else if (line[0].equals("2")) {
            assertEquals("Jawad", line[1]);
            assertEquals("20", line[2]);
        }
        else if (line[0].equals("3")) {
            assertEquals("Samy", line[1]);
            assertEquals("15", line[2]);
        } else fail("Ligne inattendue");
    }

    // ---------- Test 3 : fichier vide ----------
    @Test
    void emptyFileThrows() throws IOException {
        Path empty = Files.createFile(tmp.resolve("empty.csv"));
        assertThrows(IOException.class, () -> new CsvReader(empty));
    }

    // ---------- util ----------
    private Path copyResource(String name) throws IOException {
        Path dest = tmp.resolve(name);
        Files.copy(getClass().getResourceAsStream("/" + name), dest);
        return dest;
    }
}
