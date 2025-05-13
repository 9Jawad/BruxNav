package be.ulb.stib.parsing;

import be.ulb.stib.data.AgencyModel;
import be.ulb.stib.tools.CsvReader;
import java.io.IOException;
import java.nio.file.Path;
import static be.ulb.stib.tools.Utils.idx;
import static be.ulb.stib.tools.Utils.ensureSize;


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
            int idx = agency.idDict.getOrAdd(row[colId]);

            // latitude - longitude
            ensureSize(agency.latList, idx, Double.NaN);
            ensureSize(agency.lonList, idx, Double.NaN);
            agency.latList.set(idx, Double.parseDouble(row[colLat]));
            agency.lonList.set(idx, Double.parseDouble(row[colLon]));

            // pool nom ↔ index
            String name = row[colName];
            int nIdx = agency.stopName2idx.computeIntIfAbsent(name, k -> {
                agency.stopNamePool.add(k);
                return agency.stopNamePool.size() - 1;
            });
            ensureSize(agency.stopNameIdxList, idx, -1);
            agency.stopNameIdxList.set(idx, nIdx);
        });
    }
}
