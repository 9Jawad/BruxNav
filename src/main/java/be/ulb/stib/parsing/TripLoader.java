package be.ulb.stib.parsing;

import be.ulb.stib.data.AgencyModel;
import be.ulb.stib.tools.CsvReader;
import java.io.IOException;
import java.nio.file.Path;
import static be.ulb.stib.tools.Utils.idx;
import static be.ulb.stib.tools.Utils.ensureSize;


/* Parse un fichier trips.csv et met à jour son AgencyModel. */
public final class TripLoader {

    public static void load(Path tripsCsv, AgencyModel agency) throws IOException {
        CsvReader reader = new CsvReader(tripsCsv);
        int colTrip  = idx(reader.getHeaders(), "trip_id");
        int colRoute = idx(reader.getHeaders(), "route_id");

        // parsing
        reader.forEach(row -> {
            // idx dense
            int tripIdx = agency.idDict.getOrAdd(row[colTrip]);
            int routeIdx = agency.idDict.get(row[colRoute]);

            if (routeIdx < 0) throw new IllegalStateException("route_id " + row[colRoute] +
                                                              " n'existe pas (charger routes.csv d'abord)");
            ensureSize(agency.tripRouteIdxList, tripIdx, -1);
            agency.tripRouteIdxList.set(tripIdx, routeIdx);
        });

    }
}
