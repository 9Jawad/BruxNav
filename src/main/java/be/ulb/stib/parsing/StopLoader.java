package be.ulb.stib.parsing;

import be.ulb.stib.core.Stop;
import be.ulb.stib.data.AgencyModel;
import be.ulb.stib.tools.CsvReader;
import java.io.IOException;
import java.nio.file.Path;
import static be.ulb.stib.tools.Utils.idx;


/* Parse un fichier stops.csv et met à jour son AgencyModel. */
public final class StopLoader {

    public static void load(Path stopsCsv, AgencyModel agency) throws IOException {
        CsvReader reader = new CsvReader(stopsCsv);
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
