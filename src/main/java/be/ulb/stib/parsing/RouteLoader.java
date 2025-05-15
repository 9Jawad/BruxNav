package be.ulb.stib.parsing;

import be.ulb.stib.data.AgencyModel;
import be.ulb.stib.tools.CsvReader;
import java.io.IOException;
import java.nio.file.Path;
import static be.ulb.stib.tools.Utils.idx;
import static be.ulb.stib.tools.Utils.ensureSize;


/* Parse un fichier routes.csv et met à jour son AgencyModel. */
public final class RouteLoader {

    private static final byte BUS   = 0;
    private static final byte TRAM  = 1;
    private static final byte METRO = 2;
    private static final byte TRAIN = 3;
    private static final byte OTHER = 4;

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
            case "BUS":   return BUS;
            case "TRAM":  return TRAM;
            case "METRO": return METRO;
            case "TRAIN": return TRAIN;
            default:      return OTHER;
        }
    }
}
