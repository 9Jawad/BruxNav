package be.ulb.stib.parsing;

import be.ulb.stib.core.Stop;
import be.ulb.stib.core.StopTime;
import be.ulb.stib.core.Trip;
import be.ulb.stib.data.AgencyModel;
import be.ulb.stib.tools.CsvReader;
import java.io.IOException;
import java.nio.file.Path;
import static be.ulb.stib.tools.Utils.idx;


/* Parse un fichier stop_times.csv et met à jour son AgencyModel. */
public final class StopTimesLoader {

    public static void load(Path stopTimesCsv, AgencyModel agency) throws IOException {

        CsvReader reader = new CsvReader(stopTimesCsv);
        int colTrip = idx(reader.getHeaders(), "trip_id");
        int colStop = idx(reader.getHeaders(), "stop_id");
        int colTime = idx(reader.getHeaders(), "departure_time");
        int colSeq  = idx(reader.getHeaders(), "stop_sequence");

        final String[] currentTripId = {null};
        final Trip[] currentTrip = {null};

        // parsing
        reader.forEach(row -> {
            String tripId = row[colTrip];
            if (!tripId.equals(currentTripId[0])) {
                currentTripId[0] = tripId;
                currentTrip[0] = agency.trips.get(tripId);
            }
            Stop stop   = agency.stops.get(row[colStop]);
            int depSec  = toSec(row[colTime]);
            int seq     = Integer.parseInt(row[colSeq]);
            currentTrip[0].addStopTime(new StopTime(stop, depSec, seq));
        });
        for (Trip t : agency.trips.values()) {
            t.steps().sort((s1,s2)->Integer.compare(s1.sequence(), s2.sequence()));
        }
    }

    /* ------------- helpers ------------- */

    public static int toSec(String hhmmss) {
        String[] p = hhmmss.split(":");
        int h = Integer.parseInt(p[0]); // heure
        int m = Integer.parseInt(p[1]); // minute
        int s = Integer.parseInt(p[2]); // seconde
        return h * 3600 + m * 60 + s;
    }
}
