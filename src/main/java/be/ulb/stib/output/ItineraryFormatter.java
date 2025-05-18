package be.ulb.stib.output;

import be.ulb.stib.algo.AStarTD;
import be.ulb.stib.data.GlobalModel;
import be.ulb.stib.graph.MultiModalGraph;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import java.util.ArrayList;
import java.util.List;
import static be.ulb.stib.tools.Utils.reverse;


public final class ItineraryFormatter {

    public static List<String> format(IntArrayList pathStops,
                                      int[] arrivalSec,
                                      byte[] parentMode,
                                      int[] parentRouteIdx,
                                      GlobalModel model,
                                      MultiModalGraph graph)
    {
        List<String> out = new ArrayList<>();
        if (pathStops == null || pathStops.size() < 2) {
            out.add("No journey found.");
            return out;
        }

        for (int i = 1; i < pathStops.size(); i++) {
            int from = pathStops.getInt(i - 1);
            int to = pathStops.getInt(i);

            String fromName = stopName(model, from);
            String toName = stopName(model, to);
            String depTime = hms(arrivalSec[from]);
            String arrTime = hms(arrivalSec[to]);
            int routeIdx = parentRouteIdx[to];
            int parentModeIdx = parentMode[to];

            if (parentMode[to] == 0 && parentRouteIdx[to] == -1) {
                out.add(String.format("Walk from %s (%s) to %s (%s)", // WALK
                        fromName, depTime, toName, arrTime));
            } else {                                         // TRANSIT
                String shortName = model.routeShortPool.get(
                        model.routeShortIdxList.getInt(routeIdx));

                byte type = model.routeTypeList.getByte(routeIdx);

                String modeStr = switch (type) {
                    case 0 -> "BUS";
                    case 1 -> "TRAM";
                    case 2 -> "METRO";
                    case 3 -> "TRAIN";
                    default  -> "TRANSIT";
                };
                out.add(String.format("Take %s %s from %s (%s) to %s (%s)",
                        modeStr, shortName, fromName, depTime, toName, arrTime));

            }
        }
        return out;
    }


    /* Retourne une liste de chaînes "(S1,dep,S2,arr)". */
    public static List<String> formatMinimal(
            IntArrayList path, int[] arrTime, GlobalModel model)
    {
        List<String> out = new ArrayList<>();
        for (int i = 1; i < path.size(); i++) {
            int from = path.getInt(i - 1);
            int to   = path.getInt(i);
            out.add(String.format("(%s, %s, %s, %s)",
                    stopName(model, from), hms(arrTime[from]),
                    stopName(model, to)  , hms(arrTime[to])));
        }
        return out;
    }

    public static IntArrayList reconstruct(int arr, AStarTD astar) {
        IntArrayList path = new IntArrayList();
        for (int cur = arr; cur != -1; cur = astar.parentStops()[cur])
            path.add(cur);
        reverse(path);
        return path;
    }

    /* ----------- helpers ----------- */

    private static String stopName(GlobalModel model, int stopIdx) {
        int poolIdx = model.stopNameIdxList.getInt(stopIdx);
        String raw   = model.stopNamePool.get(poolIdx);
        int us = raw.indexOf('_');
        return us > 0 ? raw.substring(us + 1) : raw;
    }

    private static String agencyFromStopId(GlobalModel model, int idx) {
        String name = stopName(model, idx);
        int dash = name.indexOf('-');
        return (dash > 0) ? name.substring(0, dash) : "TRANSIT";
    }

    private static String hms(int s) {
        int h =  s / 3600;
        int m = (s % 3600) / 60;
        return String.format("%02d:%02d:%02d", h, m, s % 60);
    }
}
