package be.ulb.stib.parsing;

import be.ulb.stib.core.Route;
import be.ulb.stib.core.Trip;
import be.ulb.stib.data.AgencyModel;
import be.ulb.stib.tools.CsvReader;
import java.io.IOException;
import java.nio.file.Path;
import static be.ulb.stib.tools.Utils.idx;


/* Parse un fichier trips.csv et met à jour son AgencyModel. */
public final class TripLoader {

    public static void load(Path tripsCsv, AgencyModel agency) throws IOException {
        try (CsvReader reader = new CsvReader(tripsCsv)) {
            int colTrip  = idx(reader.getHeaders(), "trip_id");
            int colRoute = idx(reader.getHeaders(), "route_id");

            // parsing
            reader.forEach(row -> {
                String tripId  = row[colTrip];
                String routeId = row[colRoute];
                Route route   = agency.routes.get(routeId);
                agency.trips.put(tripId, new Trip(tripId, route));
            });
        }
    }
}
