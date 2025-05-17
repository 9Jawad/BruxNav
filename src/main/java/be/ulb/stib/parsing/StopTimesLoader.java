package be.ulb.stib.parsing;

import be.ulb.stib.data.AgencyModel;
import be.ulb.stib.tools.CsvReader;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import java.io.IOException;
import java.nio.file.Path;
import static be.ulb.stib.tools.Utils.idx;
import static be.ulb.stib.tools.Utils.ensureSize;
import static be.ulb.stib.tools.StopTimesSorter.sortAndReplace;


/* Parse un fichier stop_times.csv et met à jour son AgencyModel. */
public final class StopTimesLoader {

    public static void load(Path stopTimesCsv, AgencyModel agency) throws IOException {

        // ========================= DATA =========================
        sortAndReplace(stopTimesCsv);
        CsvReader reader = new CsvReader(stopTimesCsv);
        int colTrip = idx(reader.getHeaders(), "trip_id");
        int colStop = idx(reader.getHeaders(), "stop_id");
        int colTime = idx(reader.getHeaders(), "departure_time");
        int colSeq  = idx(reader.getHeaders(), "stop_sequence");

        // alias
        IntArrayList stopIdxL = agency.stopIdxByTimeList;
        IntArrayList depSecL  = agency.depSecList;
        IntArrayList denseOfs = agency.tripOfsDense;
        IntArrayList sparseOfs = agency.tripOfsSparse;

        // buffer trip (mémoire temporaire)
        IntArrayList bufStop   = new IntArrayList();
        IntArrayList bufSec    = new IntArrayList();
        int[] currentGlobalTrip = { -1 };
        int[] prevSeq = { -1 };
        // ========================================================


        // parsing
        reader.forEach(row -> {
            int tripIdx = agency.idDict.get(row[colTrip]);
            if (tripIdx < 0) throw new IllegalStateException("unknown trip_id : " + row[colTrip]);

            if (tripIdx != currentGlobalTrip[0]) { // changement de trip
                save(currentGlobalTrip[0], bufStop, bufSec, stopIdxL, depSecL, denseOfs, sparseOfs);
                // reset la mémoire temporaire
                bufStop.clear();
                bufSec.clear();
                currentGlobalTrip[0] = tripIdx;
                prevSeq[0] = -1;
            }

            int seq = Integer.parseInt(row[colSeq]);
            if (seq <= prevSeq[0]) throw new IllegalStateException("stop_sequence not increasing : " + row[colTrip]);
            prevSeq[0] = seq;

            int stopIdx = agency.idDict.get(row[colStop]);
            if (stopIdx < 0) throw new IllegalStateException("unknown stop_id : " + row[colStop]);

            bufStop.add(stopIdx);
            bufSec.add(toSec(row[colTime]));
        });
        // traitement du dernier trip
        save(currentGlobalTrip[0], bufStop, bufSec, stopIdxL, depSecL, denseOfs, sparseOfs);
        denseOfs.add(stopIdxL.size());
    }

    private static void save(int tripIdx, IntArrayList bufStop, IntArrayList bufSec,
                             IntArrayList stopIdxL, IntArrayList depSecL, IntArrayList denseOfs,
                             IntArrayList sparseOfs) {

        if (tripIdx < 0)        return; // skip premier appel

        // ajout des données à l'AgencyModel
        int offset = stopIdxL.size();
        denseOfs.add(offset);
        ensureSize(sparseOfs, tripIdx, -1);
        sparseOfs.set(tripIdx, offset);

        stopIdxL.addAll(bufStop);
        depSecL.addAll(bufSec);
    }

    /* ------------- helpers ------------- */

    public static int toSec(String hhmmss) {
        String[] p = hhmmss.split(":");
        int h = Integer.parseInt(p[0]); // heure
        int m = Integer.parseInt(p[1]); // minute
        int s = Integer.parseInt(p[2]); // seconde
        return h * 3600 + m * 60 + s;
    }
}
