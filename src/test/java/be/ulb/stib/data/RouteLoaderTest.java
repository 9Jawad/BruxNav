package be.ulb.stib.data;

import be.ulb.stib.tools.UtilsForTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;


class RouteLoaderTest {

    @TempDir Path tmp;   // dossier temporaire

    // ---------- Test 1 : nombre de route_id ----------
    @Test
    void routeIdCountMatchesCsvLineCount() throws IOException {
        Path csv = UtilsForTest.copyToTemp("routes.csv", tmp);
        AgencyModel agency = new AgencyModel();
        RouteLoader.load(csv, agency);
        agency.freeze();

        int expectedRoutes = (int) Files.lines(csv).skip(1).count(); // sans l’en‑tête
        assertEquals(expectedRoutes, agency.routeCount());
    }

    // ---------- Test 2 : type route ----------
    @Test
    void sampleRouteTypesAreCorrect() throws IOException {
        Path csv = UtilsForTest.copyToTemp("routes.csv", tmp);
        AgencyModel agency = new AgencyModel();
        RouteLoader.load(csv, agency);
        agency.freeze();

        assertEquals(0,  agency.routeTypeList.getByte(8));  // BUS
        assertEquals(1,  agency.routeTypeList.getByte(41)); // TRAM
        assertEquals(2,  agency.routeTypeList.getByte(22)); // METRO
        assertEquals(3,  agency.routeTypeList.getByte(34)); // TRAIN
        assertEquals(4,  agency.routeTypeList.getByte(45)); // OTHER
    }

    // ---------- Test 3 : premiers short_name ----------
    @Test
    void sampleRouteSNamesAreCorrect() throws IOException {
        Path csv = UtilsForTest.copyToTemp("routes.csv", tmp);
        AgencyModel agency = new AgencyModel();
        RouteLoader.load(csv, agency);
        agency.freeze();

        assertEquals("92",  agency.routeShortPool.get(0));
        assertEquals("209", agency.routeShortPool.get(1));
        assertEquals("716", agency.routeShortPool.get(2));
        assertEquals("R28", agency.routeShortPool.get(8)); // réel idx = 11
    }

    // ---------- Test 4 : premiers long_name ----------
    @Test
    void sampleRouteLNamesAreCorrect() throws IOException {
        Path csv = UtilsForTest.copyToTemp("routes.csv", tmp);
        AgencyModel agency = new AgencyModel();
        RouteLoader.load(csv, agency);
        agency.freeze();

        assertEquals("Mol station - Lidwina",       agency.routeLongPool.get(1));
        assertEquals("Perk - Kampenhout - Tildonk", agency.routeLongPool.get(5));
        assertEquals("Zonhoven - Genk",             agency.routeLongPool.get(7));
        assertEquals("Genk - Lanaken",              agency.routeLongPool.get(14)); // réel idx = 15
    }

    // ---------- Test 5 : mapping short name ↔ index ----------
    @Test
    void shortNameIndexMappingIsCorrect() throws IOException {
        Path csv = UtilsForTest.copyToTemp("routes.csv", tmp);
        AgencyModel agency = new AgencyModel();
        RouteLoader.load(csv, agency);
        agency.freeze();

        assertEquals(3,     agency.routeShortIdxList.get(6));
        assertEquals("583", agency.routeShortPool.get(3));
        assertEquals(19,    agency.routeShortIdxList.get(23));
        assertEquals("212", agency.routeShortPool.get(19));
    }

    // ---------- Test 6 : mapping long name ↔ index ----------
    @Test
    void longNameIndexMappingIsCorrect() throws IOException {
        Path csv = UtilsForTest.copyToTemp("routes.csv", tmp);
        AgencyModel agency = new AgencyModel();
        RouteLoader.load(csv, agency);
        agency.freeze();

        assertEquals(13,                     agency.routeLongIdxList.get(14));
        assertEquals("Diest - Leopoldsburg", agency.routeLongPool.get(13));
        assertEquals(7,                      agency.routeLongIdxList.get(7));
        assertEquals("Zonhoven - Genk",      agency.routeLongPool.get(7));
    }
}
