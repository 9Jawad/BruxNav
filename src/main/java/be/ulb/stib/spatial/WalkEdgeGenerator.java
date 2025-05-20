package be.ulb.stib.spatial;

import be.ulb.stib.core.Stop;
import be.ulb.stib.core.WalkEdge;
import be.ulb.stib.data.GlobalModel;
import java.util.ArrayList;
import java.util.List;
import static java.lang.Math.sqrt;


/**
 * Construit, pour chaque arrêt, la liste des voisins accessibles à pied.
 * Les arêtes sont stockées dans deux tableaux parallèles du GlobalModel :
 *   walkEdges     : ObjectArrayList<IntArrayList> – indices cibles
 *   walkEdgesCost : ObjectArrayList<IntArrayList> – coût (secondes)
 */
public final class WalkEdgeGenerator {

    private static final double DEFAULT_RADIUS_M = 1000; // rayon maximal de marche en mètres.
    private static final double WALK_SPEED       = 1.25; // vitesse de marche (4,5km/h).


    /* Ajoute les arcs piétons au GlobalModel. */
    public static List<WalkEdge> generate(GlobalModel model, double radiusMeters) {
        KDTree kd = new KDTree(new ArrayList<>(model.stops.values()));

        List<WalkEdge> edges = new ArrayList<>();

        for (Stop s : model.stops.values()) {
            List<String> neigh = kd.radiusSearch(s.lat, s.lon, radiusMeters);

            for (String nId : neigh) {
                if (nId.equals(s.id)) continue;

                Stop n = model.stops.get(nId);
                int cost = (int) Math.round(distanceInMeters(s, n) / WALK_SPEED);
                edges.add(new WalkEdge(s.id, nId, cost));
            }
        }
        return edges;
    }
    /* Surcharge pratique avec le rayon par défaut (1km). */
    public static List<WalkEdge> generate(GlobalModel model) {
        return generate(model, DEFAULT_RADIUS_M);
    }


    /* ============= helpers ============= */

    private static double distanceInMeters(Stop a, Stop b) {
        double dLat = a.lat - b.lat;
        double dLon = a.lon - b.lon;
        double distance = sqrt(squaredNorm(dLat, dLon));
        return degrees2meters(distance);
    }

    private static double degrees2meters(double degrees) {
        return degrees * 111_320.0; // 1° ≈ 111 km (approximation suffisante)
    }

    public static double squaredNorm(double dLat, double dLon) {
        return dLat*dLat + dLon*dLon;
    }
}
