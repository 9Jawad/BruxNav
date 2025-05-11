package be.ulb.stib.data;

import be.ulb.stib.tools.StopTimesSorter;
import be.ulb.stib.tools.UtilsForTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;


class StopTimesLoaderTest {

    @TempDir Path tmp;

    @Test
    void loadStopTimesMini() throws IOException {
        Path stops  = UtilsForTest.copyToTemp("mini_stops_v2.csv",  tmp);
        Path routes = UtilsForTest.copyToTemp("mini_routes_v3.csv", tmp);
        Path trips  = UtilsForTest.copyToTemp("mini_trips_v2.csv",  tmp);
        Path times = UtilsForTest.copyToTemp("mini_stop_times.csv", tmp);
        
        AgencyModel agency = new AgencyModel();
        StopLoader.load(stops, agency);
        RouteLoader.load(routes, agency);
        TripLoader.load(trips, agency);
        StopTimesLoader.load(times, agency);
        agency.freeze();

        // TEST : hard-codé
        /**
         * Après parsing :
         *   stopIdxByTime = [0,1,0,2]    (S1,S2,S1,S3)
         *   depSec        = [25200,25380,25500,25680]
         *   tripOfsDense  = [0,2,4]
         *   tripOfsSparse = [0,2,-1]     (T3 ignoré)
         */

        assertEquals(3, agency.stopCount());
        assertEquals(1, agency.routeCount());
        assertEquals(3, agency.tripCount());

        assertArrayEquals(new int[]{0,1,0,2},                    agency.stopIdxByTimeList.toIntArray());
        assertArrayEquals(new int[]{25200, 25380, 25680, 25500}, agency.depSecList.toIntArray());
        assertArrayEquals(new int[]{0,2,4},                      agency.tripIdxStopList.toIntArray());
        assertArrayEquals(new int[]{-1, -1, -1, -1, 0, 2},       agency.tripStopOffsets.toIntArray());
    }
}
