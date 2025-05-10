package be.ulb.stib.data;

import be.ulb.stib.tools.Utils;

import java.io.IOException;
import java.nio.file.Path;


/* Parse un fichier trips.csv et met à jour son AgencyModel. */
public final class TripLoader {

    public static void load(Path tripsCsv, AgencyModel agency) throws IOException {
        CsvReader reader = new CsvReader(tripsCsv);
        int colTrip  = Utils.idx(reader.getHeaders(), "trip_id");
        int colRoute = Utils.idx(reader.getHeaders(), "route_id");

        int routeBase = agency.stopCount();

        // parsing
        reader.forEach(row -> {
            // idx dense
            agency.idDict.getOrAdd(row[colTrip]);

            // local route_id
            int globalRouteIdx = agency.idDict.get(row[colRoute]);
            if (globalRouteIdx < 0) throw new IllegalStateException("route_id " + row[colRoute] +
                                                                    " n'existe pas (charger routes.csv d'abord)");
            int localRouteIdx = globalRouteIdx - routeBase;
            agency.tripRouteIdxList.add(localRouteIdx);
        });

    }
}
