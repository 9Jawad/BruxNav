package be.ulb.stib.data;

import com.opencsv.exceptions.CsvValidationException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import be.ulb.stib.data.Utils;


/* Parse un fichier stops.csv et met à jour son AgencyModel. */
public final class StopLoader {

    public static void load(Path stopsCsv, AgencyModel agency) throws IOException {
        CsvReader reader = new CsvReader(stopsCsv);
        int colId   = Utils.idx(reader.getHeaders(), "stop_id");
        int colName = Utils.idx(reader.getHeaders(), "stop_name");
        int colLat  = Utils.idx(reader.getHeaders(), "stop_lat");
        int colLon  = Utils.idx(reader.getHeaders(), "stop_lon");

        reader.forEach(row -> {
            int idx = agency.idDict.getOrAdd(row[colId]);
            double lat = Double.parseDouble(row[colLat]);
            double lon = Double.parseDouble(row[colLon]);

            // latitude - longitude
            agency.latList.add(lat);
            agency.lonList.add(lon);

            // pool nom ↔ index
            String name = row[colName];
            int nIdx = agency.stopName2idx.computeIntIfAbsent(name, k -> {
                agency.stopNamePool.add(k);
                return agency.stopNamePool.size() - 1;
            });
            agency.stopNameIdxList.add(nIdx);
        });
    }
}
