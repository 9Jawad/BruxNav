package be.ulb.stib.data;

import java.io.IOException;
import java.nio.file.Path;

import static be.ulb.stib.tools.Utils.idx;
import static be.ulb.stib.tools.Utils.ensureSize;


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
            // idx dense
            int idx = agency.idDict.getOrAdd(row[colId]);

            // type
            ensureSize(agency.routeTypeList, idx, (byte)-1);
            agency.routeTypeList.set(idx, routeType(row[colType]));

            // short name
            String sName = row[colShrt];
            int sIdx = agency.routeShort2idx.computeIntIfAbsent(sName, k -> {
                agency.routeShortPool.add(k);
                return agency.routeShortPool.size() - 1;
            });
            ensureSize(agency.routeShortIdxList, idx, -1);
            agency.routeShortIdxList.set(idx, sIdx);

            // long name
            String lName = row[colLong];
            int lIdx = agency.routeLong2idx.computeIntIfAbsent(lName, k -> {
                agency.routeLongPool.add(k);
                return agency.routeLongPool.size() - 1;
            });
            ensureSize(agency.routeLongIdxList, idx, -1);
            agency.routeLongIdxList.set(idx, lIdx);
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
