package be.ulb.stib.data;

import be.ulb.stib.tools.UtilsForTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;


class TripLoaderTest {

    @TempDir Path tmp; // répertoire temporaire

    // ---------- Test 1 : nombre de route ----------
    @Test
    void routeCountMatchesCsv() throws IOException {
        Path routesCsv = UtilsForTest.copyToTemp("mini_routes_v2.csv", tmp);
        Path tripsCsv = UtilsForTest.copyToTemp("mini_trips.csv", tmp);
        AgencyModel agency = new AgencyModel();
        RouteLoader.load(routesCsv, agency);
        TripLoader.load(tripsCsv, agency);
        agency.freeze();

        assertEquals(2, agency.routeCount());
    }

    // ---------- Test 2 : nombre de trip ----------
    @Test
    void tripCountMatchesCsv() throws IOException {
        Path routesCsv = UtilsForTest.copyToTemp("mini_routes_v2.csv", tmp);
        Path tripsCsv = UtilsForTest.copyToTemp("mini_trips.csv", tmp);
        AgencyModel agency = new AgencyModel();
        RouteLoader.load(routesCsv, agency);
        TripLoader.load(tripsCsv, agency);
        agency.freeze();

        assertEquals(3, agency.tripCount());
    }

    // ---------- Test 3 : mapping trip → route ----------
    @Test
    void tripIDMatchesLocalRouteID() throws IOException {
        Path routesCsv = UtilsForTest.copyToTemp("mini_routes_v2.csv", tmp);
        Path tripsCsv  = UtilsForTest.copyToTemp("mini_trips.csv",  tmp);
        AgencyModel agency = new AgencyModel();
        RouteLoader.load(routesCsv, agency);
        TripLoader.load(tripsCsv, agency);
        agency.freeze();

        assertEquals(0, agency.tripRouteIdxList.getInt(0));  // T1
        assertEquals(0, agency.tripRouteIdxList.getInt(1));  // T2
        assertEquals(1, agency.tripRouteIdxList.getInt(2));  // T3
    }

    // ---------- Test 4 : tripIdx dans IDict ----------
    @Test
    void tripIdAreInIdDict() throws IOException {
        Path routesCsv = UtilsForTest.copyToTemp("mini_routes_v2.csv", tmp);
        Path tripsCsv  = UtilsForTest.copyToTemp("mini_trips.csv",  tmp);
        AgencyModel agency = new AgencyModel();
        RouteLoader.load(routesCsv, agency);
        TripLoader.load(tripsCsv, agency);
        agency.freeze();

        assertTrue(agency.idDict.get("T1") >= 0);
        assertTrue(agency.idDict.get("T3") >= 0);
    }
}
