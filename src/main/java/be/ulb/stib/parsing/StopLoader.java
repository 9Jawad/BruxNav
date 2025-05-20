package be.ulb.stib.parsing;

import be.ulb.stib.core.Stop;
import be.ulb.stib.data.AgencyModel;
import be.ulb.stib.tools.CsvReader;
import java.io.IOException;
import java.nio.file.Path;
import static be.ulb.stib.tools.Utils.idx;



/**
 * Rresponsable du chargement des informations d'arrêts depuis un fichier CSV dans un AgencyModel.
 * Le fichier CSV doit contenir les en-têtes : "stop_id", "stop_name", "stop_lat" et "stop_lon".
 * Chaque ligne est analysée et un objet Stop correspondant est créé et ajouté à la collection d'arrêts de l'agence.
 */
public final class StopLoader {

    public static void load(Path stopsCsv, AgencyModel agency) throws IOException {
        try (CsvReader reader = new CsvReader(stopsCsv)) {
            int colId   = idx(reader.getHeaders(), "stop_id");
            int colName = idx(reader.getHeaders(), "stop_name");
            int colLat  = idx(reader.getHeaders(), "stop_lat");
            int colLon  = idx(reader.getHeaders(), "stop_lon");

            // parsing
            reader.forEach(row -> {
                String id   = row[colId];
                String name = row[colName];
                double lat  = Double.parseDouble(row[colLat]);
                double lon  = Double.parseDouble(row[colLon]);
                int nameIdx = agency.stopNamePool.intern(name);
                agency.stops.put(id, new Stop(id, nameIdx, lat, lon));
            });
        }
    }
}
