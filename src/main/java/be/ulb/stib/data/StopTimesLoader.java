package be.ulb.stib.data;

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
        IntArrayList denseOfs = agency.tripIdxStopList;
        IntArrayList sparseOfs = agency.tripStopOffsets;

        // base = #stops + #routes (idx globaux)
        final int tripBase = agency.stopCount() + agency.routeCount();

        // sparseOfs remplissage -1
        final int nTrips   = agency.tripCount();
        if (sparseOfs.isEmpty()) for (int i = 0; i < nTrips; i++) sparseOfs.add(-1);


        // buffer trip (mémoire temporaire)
        IntArrayList bufStop   = new IntArrayList();
        IntArrayList bufSec    = new IntArrayList();
        int[] currentGlobalTrip = { -1 };
        int[] prevSeq = { -1 };
        // ========================================================


        // parsing
        reader.forEach(row -> {
            int globalTripIdx = agency.idDict.get(row[colTrip]);
            if (globalTripIdx < 0) throw new IllegalStateException("trip_id inconnu: " + row[colTrip]);

            if (globalTripIdx != currentGlobalTrip[0]) { // changement de trip
                save(currentGlobalTrip[0], bufStop, bufSec, stopIdxL, depSecL, denseOfs, sparseOfs, tripBase);
                // reset la mémoire temporaire
                bufStop.clear();
                bufSec.clear();
                currentGlobalTrip[0] = globalTripIdx;
                prevSeq[0] = -1;
            }

            int seq = Integer.parseInt(row[colSeq]);
            if (seq <= prevSeq[0]) throw new IllegalStateException("stop_sequence non croissant (.csv non trié !!!) "
                                                                    + row[colTrip]);
            prevSeq[0] = seq;

            int stopIdx = agency.idDict.get(row[colStop]);
            if (stopIdx < 0) throw new IllegalStateException("stop_id inconnu: " + row[colStop]);

            bufStop.add(stopIdx);
            bufSec.add(toSec(row[colTime]));
        });
        // traitement du dernier trip
        save(currentGlobalTrip[0], bufStop, bufSec, stopIdxL, depSecL, denseOfs, sparseOfs, tripBase);
        denseOfs.add(stopIdxL.size());
    }

    /* ------------- helpers ------------- */

    private static void save(int tripIdx, IntArrayList bufStop, IntArrayList bufSec,
                             IntArrayList stopIdxL, IntArrayList depSecL, IntArrayList denseOfs,
                             IntArrayList sparseOfs, int tripBase) {

        if (tripIdx < 0)        return; // skip premier appel
        if (bufStop.size() < 2) return; // "timepoint only"

        // ajout des données à l'AgencyModel
        int offset = stopIdxL.size();
        denseOfs.add(offset);
        ensureSize(sparseOfs, tripIdx, -1);
        sparseOfs.set(tripIdx, offset);

        stopIdxL.addAll(bufStop);
        depSecL.addAll(bufSec);
    }

    private static int toSec(String hhmmss) {
        String[] p = hhmmss.split(":");
        int h = Integer.parseInt(p[0]); // heure
        int m = Integer.parseInt(p[1]); // minute
        int s = Integer.parseInt(p[2]); // seconde
        return h * 3600 + m * 60 + s;
    }
}
