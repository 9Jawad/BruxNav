package be.ulb.stib.spatial;

import be.ulb.stib.data.GlobalModel;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import static be.ulb.stib.spatial.KDTree.squaredNorm;
import static be.ulb.stib.tools.Utils.ensureSize;
import static java.lang.Math.sqrt;


/**
 * Construit, pour chaque arrêt, la liste des voisins accessibles à pied.
 * Les arêtes sont stockées dans deux tableaux parallèles du GlobalModel:
 *   walkEdges  : ObjectArrayList<IntArrayList> – indices cibles
 *   walkEdgesCost    : ObjectArrayList<IntArrayList> – coût (secondes)
 */
public final class WalkEdgeGenerator {

    private static final int    DEFAULT_RADIUS_M = 500;  // rayon maximal de marche en mètres.
    private static final double WALK_SPEED_MPS   = 1.39; // vitesse de marche → 1.39m/s (5km/h).

    /* Ajoute les arcs piétons au GlobalModel. */
    public static void build(GlobalModel model, int radiusMeters) {
        KDTree tree = new KDTree(model);

        // assure que walkEdges / walkEdgesCost ont la bonne taille
        int n = model.latList.size();
        ensureSize(model.walkEdges,     n, -1);
        ensureSize(model.walkEdgesCost, n, -1);

        for (int stopIdx = 0; stopIdx < n; stopIdx++) {
            if (model.stopNameIdxList.getInt(stopIdx) < 0) continue; // slot vide

            double lat = model.latList.getDouble(stopIdx);
            double lon = model.lonList.getDouble(stopIdx);

            IntArrayList neighbors = tree.rangeSearch(lat, lon, radiusMeters, stopIdx);
            IntArrayList costs     = new IntArrayList();

            for (int i : neighbors) {
                double lat2 = model.latList.getDouble(i);
                double lon2 = model.lonList.getDouble(i);
                double dist = distanceInMeters(lat - lat2, lon - lon2);

                costs.add((int) Math.round(dist / WALK_SPEED_MPS));
            }
            model.walkEdges.set(stopIdx, neighbors);
            model.walkEdgesCost.set(stopIdx, costs);
        }
    }

    /* Surcharge pratique avec le rayon par défaut (500m). */
    public static void build(GlobalModel model) {
        build(model, DEFAULT_RADIUS_M);
    }

    /* ------------- helpers ------------- */

    private static double distanceInMeters(double dLat, double dLon) {
        double distance = sqrt(squaredNorm(dLat, dLon));
        return degrees2meters(distance);
    }

    private static double degrees2meters(double degrees) {
        return degrees * 111_000.0; // 1° ≈ 111 km (approximation suffisante)
    }
}
