package be.ulb.stib.data;

import com.opencsv.exceptions.CsvValidationException;
import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;


/* Parse un fichier routes.csv et met à jour son AgencyModel. */
public final class RouteLoader {

    public static void load(Path routesCsv, AgencyModel agency) throws IOException {
        CsvReader reader = new CsvReader(routesCsv);
        int colId   = Utils.idx(reader.getHeaders(), "route_id");
        int colShrt = Utils.idx(reader.getHeaders(), "route_short_name");
        int colLong = Utils.idx(reader.getHeaders(), "route_long_name");
        int colType = Utils.idx(reader.getHeaders(), "route_type");

        reader.forEach(row -> {
            // idx dense
            agency.idDict.getOrAdd(row[colId]);

            // type
            agency.routeTypeList.add(routeType(row[colType]));

            // short name
            String sName = row[colShrt];
            int sIdx = agency.routeShort2idx.computeIntIfAbsent(sName, k -> {
                agency.routeShortPool.add(k);
                return agency.routeShortPool.size() - 1;
            });
            agency.routeShortIdxList.add(sIdx);

            // long name
            String lName = row[colLong];
            int lIdx = agency.routeLong2idx.computeIntIfAbsent(lName, k -> {
                agency.routeLongPool.add(k);
                return agency.routeLongPool.size() - 1;
            });
            agency.routeLongIdxList.add(lIdx);
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
