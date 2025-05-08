package be.ulb.stib.data;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;


class StopLoaderTest {

    @TempDir Path tmp;   // dossier temporaire

    // ---------- Test 1 : nombre de stop_id ----------
    @Test
    void stopIdCountMatchesCsvLineCount() throws IOException {
        Path csv = Resources.copyToTemp("mini_stops.csv", tmp);
        AgencyModel agency = new AgencyModel();
        StopLoader.load(csv, agency);
        agency.freeze();

        int expectedStops = (int) Files.lines(csv).skip(1).count(); // sans l’en‑tête
        assertEquals(expectedStops, agency.stopCount(),
                "Chaque stop_id du CSV doit être présent dans le modèle");
    }

    // ---------- Test 2 : premiers noms ----------
    @Test
    void sampleStopNamesAreCorrect() throws IOException {
        Path csv = Resources.copyToTemp("mini_stops.csv", tmp);
        AgencyModel agency = new AgencyModel();
        StopLoader.load(csv, agency);
        agency.freeze();

        assertEquals("MONTGOMERY",   agency.names.get(0));
        assertEquals("SIMONIS",      agency.names.get(1));
        assertEquals("DE BROUCKERE", agency.names.get(2)); // réel idx = 5
    }

    // ---------- Test 3 : mapping nom ↔ index ----------
    @Test
    void nameIndexMappingIsCorrect() throws IOException {
        Path csv = Resources.copyToTemp("mini_stops.csv", tmp);
        AgencyModel agency = new AgencyModel();
        StopLoader.load(csv, agency);
        agency.freeze();

        assertEquals(1,         agency.nameIdxList.get(4));
        assertEquals("SIMONIS", agency.names.get(1));
    }

    // ---------- Test 4: latitudes ----------
    @Test
    void sampleLatitudesAreCorrect() throws IOException {
        Path csv = Resources.copyToTemp("mini_stops.csv", tmp);
        AgencyModel agency = new AgencyModel();
        StopLoader.load(csv, agency);
        agency.freeze();

        assertEquals(50.848086, agency.latList.getDouble(17), 1e-6);
        assertEquals(50.836811, agency.latList.getDouble(42), 1e-6);
        assertEquals(50.851494, agency.latList.getDouble(5),  1e-6);
    }

    // ---------- Test 5 : longitudes ----------
    @Test
    void sampleLongitudesAreCorrect() throws IOException {
        Path csv = Resources.copyToTemp("mini_stops.csv", tmp);
        AgencyModel agency = new AgencyModel();
        StopLoader.load(csv, agency);
        agency.freeze();

        assertEquals(4.3605,  agency.lonList.getDouble(10), 1e-6);
        assertEquals(4.349,   agency.lonList.getDouble(26), 1e-6);
        assertEquals(4.34549, agency.lonList.getDouble(46), 1e-6);
    }
}
