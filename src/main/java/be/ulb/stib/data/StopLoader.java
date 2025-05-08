package be.ulb.stib.data;

import com.opencsv.exceptions.CsvValidationException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;


/* Parse un fichier stops.csv et met à jour son AgencyModel. */
public final class StopLoader {

    public static void load(Path stopsCsv, AgencyModel a) throws IOException {
        CsvReader r = new CsvReader(stopsCsv);
        int colId   = idx(r.getHeaders(), "stop_id");
        int colName = idx(r.getHeaders(), "stop_name");
        int colLat  = idx(r.getHeaders(), "stop_lat");
        int colLon  = idx(r.getHeaders(), "stop_lon");

        r.forEach(row -> {
            int idx = a.idDict.getOrAdd(row[colId]);
            double lat = Double.parseDouble(row[colLat]);
            double lon = Double.parseDouble(row[colLon]);

            // Append ou overwrite (sécuritaire)
            if (idx == a.latList.size()) {
                a.latList.add(lat);
                a.lonList.add(lon);
            } else {
                a.latList.set(idx, lat);
                a.lonList.set(idx, lon);
            }

            // pool nom ↔ index
            String name = row[colName];
            int nIdx = a.name2idx.computeIntIfAbsent(name, k -> {
                a.names.add(k);
                return a.names.size() - 1;
            }); // (sécuritaire)
            if (idx == a.nameIdxList.size()) a.nameIdxList.add(nIdx);
            else                             a.nameIdxList.set(idx, nIdx);
        });
    }

    private static int idx(String[] hdr, String key) {
        int i = Arrays.asList(hdr).indexOf(key);
        if (i < 0) throw new IllegalStateException("Column '" + key + "' not found");
        return i;
    }
}
