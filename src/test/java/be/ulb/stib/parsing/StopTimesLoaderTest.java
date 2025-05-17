package be.ulb.stib.parsing;

import be.ulb.stib.data.AgencyModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.IOException;
import java.nio.file.Path;
import static be.ulb.stib.tools.Utils.copyToTemp;
import static org.junit.jupiter.api.Assertions.*;


class StopTimesLoaderTest {

    @TempDir Path tmp;

    @Test
    void loadStopTimesMini() throws IOException {
        Path stops  = copyToTemp("forStopTimesLoader/stops.csv",  tmp);
        Path routes = copyToTemp("forStopTimesLoader/routes.csv", tmp);
        Path trips  = copyToTemp("forStopTimesLoader/trips.csv",  tmp);
        Path times = copyToTemp("stop_times.csv", tmp);
        
        AgencyModel agency = new AgencyModel();
        StopLoader.load(stops, agency);
        RouteLoader.load(routes, agency);
        TripLoader.load(trips, agency);
        StopTimesLoader.load(times, agency);
        agency.freeze();

        /**
         * Après parsing :
         *   stopIdxByTime = [0, 1, 0, 2, 0]    (S1,S2,S1,S3,S1)
         *   depSec        = [25200, 25380, 25500, 25680, 25800]
         *   tripOfsDense  = [0, 2, 4, 5]
         *   tripOfsSparse = [-1, -1, -1, -1, 0, 2, 4]
         */

        assertEquals(3, agency.stopCount());
        assertEquals(1, agency.routeCount());
        assertEquals(3, agency.tripCount());

        assertArrayEquals(new int[]{0, 1, 0, 2, 0},                     agency.stopIdxByTimeList.toIntArray());
        assertArrayEquals(new int[]{25200, 25380, 25680, 25500, 25800}, agency.depSecList.toIntArray());
        assertArrayEquals(new int[]{0, 2, 4, 5},                        agency.tripOfsDense.toIntArray());
        assertArrayEquals(new int[]{-1, -1, -1, -1, 0, 2, 4},           agency.tripOfsSparse.toIntArray());
    }
}
