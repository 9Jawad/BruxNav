package be.ulb.stib.parsing;

import be.ulb.stib.core.Route;
import be.ulb.stib.data.AgencyModel;
import be.ulb.stib.tools.CsvReader;
import java.io.IOException;
import java.nio.file.Path;
import static be.ulb.stib.tools.Utils.idx;


/* Parse un fichier routes.csv et met à jour son AgencyModel. */
public final class RouteLoader {

    public static void load(Path routesCsv, AgencyModel agency) throws IOException {
        CsvReader reader = new CsvReader(routesCsv);
        int colId   = idx(reader.getHeaders(), "route_id");
        int colShrt = idx(reader.getHeaders(), "route_short_name");
        int colLong = idx(reader.getHeaders(), "route_long_name");
        int colType = idx(reader.getHeaders(), "route_type");

        // parsing
        reader.forEach(row -> {
            String id   = row[colId];
            String shortName  = row[colShrt];
            String longName   = row[colLong];
            String type       = row[colType];
            int shortIdx = agency.routeShortPool.intern(shortName);
            int longIdx  = agency.routeLongPool .intern(longName );
            agency.routes.put(id, new Route(id, shortIdx, longIdx, type));
        });
    }
}
