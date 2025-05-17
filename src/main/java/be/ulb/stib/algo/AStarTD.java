package be.ulb.stib.algo;

import be.ulb.stib.data.GlobalModel;
import be.ulb.stib.graph.MultiModalGraph;
import it.unimi.dsi.fastutil.ints.IntArrayPriorityQueue;
import java.util.Arrays;
import static be.ulb.stib.parsing.StopTimesLoader.toSec;


public final class AStarTD {

    /* heuristique « ligne droite / vitesse max » */
    private static final double DEG_TO_M  = 111_000.0;
    private static final double MAX_SPEED = 15.0;     // mètre par second

    private final GlobalModel model;
    private final MultiModalGraph graph;

    /* labels */
    private final int[]    arrival;       // earliest-arrival
    private final int[]    parentStop;    // reconstruction
    private final byte[]   parentMode;    // 0=walk 1=transit
    private final int[]    parentRouteIdx;
    private final double[] fscore;        // arrival + h

    public AStarTD(GlobalModel gm, MultiModalGraph gr) {
        model  = gm;
        graph = gr;

        int n       = graph.size;
        arrival     = new int[n];
        parentStop  = new int[n];
        parentMode  = new byte[n];
        parentRouteIdx = new int[n];
        fscore      = new double[n];
    }

    /* ------------------------- API ------------------------- */

    public boolean search(String src, String dst, String departureTime) {
        int srcIdx = model.stopNameIdxList.indexOf(model.stopName2idx.getInt(src));
        int dstIdx = model.stopNameIdxList.indexOf(model.stopName2idx.getInt(dst));
        int departureSec = toSec(departureTime);
        return search(srcIdx, dstIdx, departureSec);
    }

    /** recherche src→dst au plus tôt */
    public boolean search(int src, int dst, int departSec) {

        Arrays.fill(arrival,    Integer.MAX_VALUE);
        Arrays.fill(parentStop, -1);
        Arrays.fill(parentMode, (byte)-1);
        Arrays.fill(fscore,     Double.POSITIVE_INFINITY);
        Arrays.fill(parentRouteIdx, -1);

        arrival[src] = departSec;
        fscore [src] = departSec + h(src, dst);

        IntArrayPriorityQueue open =
                new IntArrayPriorityQueue((a,b)->Double.compare(fscore[a], fscore[b]));
        open.enqueue(src);

        boolean[] closed = new boolean[graph.size];

        while (!open.isEmpty()) {
            int u = open.dequeueInt();
            if (closed[u]) continue;
            closed[u] = true;
            if (u == dst) return true;

            var neigh = graph.targets.get(u);
            var cost  = graph.costs  .get(u);
            var mode  = graph.modes  .get(u);
            var trip  = graph.routeIdxPerArc.get(u);
            if (neigh == null) continue;

            for (int k = 0; k < neigh.size(); k++) {
                int v   = neigh.getInt(k);
                int arr = arrival[u] + cost.getInt(k);
                if (arr < arrival[v]) {
                    arrival   [v] = arr;
                    parentStop[v] = u;
                    parentMode[v] = (byte) mode.getInt(k);
                    parentRouteIdx[v] = trip.getInt(k);
                    fscore    [v] = arr + h(v, dst);
                    open.enqueue(v);
                }
            }
        }
        return false;
    }

    /* ------------ heuristique géographique -------------- */
    private double h(int a, int b) {
        double dLat = model.latList.getDouble(b) - model.latList.getDouble(a);
        double dLon = model.lonList.getDouble(b) - model.lonList.getDouble(a);
        return Math.sqrt(dLat*dLat + dLon*dLon) * DEG_TO_M / MAX_SPEED;
    }

    public int[] earliestArrival() { return arrival; }
    public int[] parentStops()     { return parentStop; }
    public byte[] parentModes()    { return parentMode; }
    public int[] parentRoutes()    { return parentRouteIdx; }
}
