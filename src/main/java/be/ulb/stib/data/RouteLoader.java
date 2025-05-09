package be.ulb.stib.data;

import com.opencsv.exceptions.CsvValidationException;
import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;


/* Parse un fichier routes.csv et met à jour son AgencyModel. */
public final class RouteLoader {

    public static void load(Path routesCsv, AgencyModel a) throws IOException {
        CsvReader r = new CsvReader(routesCsv);
        int colId   = Utils.idx(r.getHeaders(), "route_id");
        int colShrt = Utils.idx(r.getHeaders(), "route_short_name");
        int colLong = Utils.idx(r.getHeaders(), "route_long_name");
        int colType = Utils.idx(r.getHeaders(), "route_type");

        r.forEach(row -> {
            // idx dense
            a.idDict.getOrAdd(row[colId]);

            // type
            a.routeTypeList.add(routeType(row[colType]));

            // short name
            String sName = row[colShrt];
            int sIdx = a.routeShort2idx.computeIntIfAbsent(sName, k -> {
                a.routeShortPool.add(k);
                return a.routeShortPool.size() - 1;
            });
            a.routeShortIdxList.add(sIdx);

            // long name
            String lName = row[colLong];
            int lIdx = a.routeLong2idx.computeIntIfAbsent(lName, k -> {
                a.routeLongPool.add(k);
                return a.routeLongPool.size() - 1;
            });
            a.routeLongIdxList.add(lIdx);
        });
    }

    /* ------------- helpers ------------- */

    private static byte routeType(String s) {
        switch (s.toUpperCase()) {
            case "BUS":   return 0;
            case "TRAM":  return 1;
            case "METRO": return 2;
            case "TRAIN": return 3;
            default:      return 4;
        }
    }
}
