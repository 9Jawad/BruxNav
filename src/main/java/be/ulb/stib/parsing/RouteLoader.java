package be.ulb.stib.parsing;

import be.ulb.stib.core.Route;
import be.ulb.stib.data.AgencyModel;
import be.ulb.stib.tools.CsvReader;
import java.io.IOException;
import java.nio.file.Path;
import static be.ulb.stib.tools.Utils.idx;


/**
 * RouteLoader lit un fichier CSV contenant des informations sur les routes et remplit
 * l'AgencyModel fourni avec des objets Route. Le fichier CSV doit contenir les en-têtes :
 * "route_id", "route_short_name", "route_long_name" et "route_type".
 */
public final class RouteLoader {

    public static void load(Path routesCsv, AgencyModel agency) throws IOException {
       try (CsvReader reader = new CsvReader(routesCsv)) {
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
}
