package be.ulb.stib.spatial;

import be.ulb.stib.data.GlobalModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * KD-Tree pour indexer les stops du GlobalModel.
 * Permet de trouver l'arret le plus proche et ceux dans un rayon de x mètres.
 */
public class KDTree {

    private final Node root;

    /* Construit le KD-Tree à partir de tous les stops valides du modèle. */
    public KDTree(GlobalModel model) {
        List<Node> pts = new ArrayList<>();

        for (int i = 0; i < model.stopNameIdxList.size(); i++) {
            int value = model.stopNameIdxList.get(i);
            if (value < 0) continue;
            pts.add(new Node(
                    i, // stopIdx
                    model.latList.getDouble(i),
                    model.lonList.getDouble(i)
            ));
        }
        root = build(pts, 0);
    }

    /* Construit un arbre KD en alternant les dimensions (latitude/longitude). */
    private Node build(List<Node> points, int depth) {
        // Cas de base
        if (points.isEmpty()) { return null; } // enfants d'une feuille

        // Détermine l'axe de comparaison
        boolean isLatitudeAxis = (depth % 2 == 0);

        // Trie les points selon l'axe
        if (isLatitudeAxis) { points.sort(Comparator.comparingDouble(node -> node.lat)); }
        else                { points.sort(Comparator.comparingDouble(node -> node.lon)); }

        // Trouve le point médian qui deviendra la racine du sous-arbre
        int medianIndex = points.size() / 2;
        Node root = points.get(medianIndex);
        root.axis = isLatitudeAxis ? 0 : 1;

        // Construit les sous-arbres
        List<Node> leftPoints = points.subList(0, medianIndex);                    // élèments plus petit
        List<Node> rightPoints = points.subList(medianIndex + 1, points.size());   // élèments plus grand

        root.left = build(leftPoints, depth + 1);
        root.right = build(rightPoints, depth + 1);

        return root;
    }

    /* ====================== RECHERCHE ====================== */

    /* Recherche du stop le plus proche du point (lat, lon). */
    public int searchNearest(double lat, double lon) {
        Nearest best = new Nearest();
        _searchNearest(root, lat, lon, best);
        return best.idx;
    }

    private void _searchNearest(Node node, double lat, double lon, Nearest best) {
        // Cas de base
        if (node == null) return;

        // distance euclidienne (pas sqrt = perf)
        double dLat = node.lat - lat;
        double dLon = node.lon - lon;
        double dist = dLat*dLat + dLon*dLon;

        // trouvé un meilleur point
        if (dist < best.dist) {
            best.dist = dist;
            best.idx = node.stopIdx;
        }
        // détermination de l'ordre d'exploration des sous-arbres
        int axis = node.axis;

        // first est le sous-arbre le plus susceptible de contenir le point le plus proche
        Node first = (axis==0 && node.lat < lat) || (axis==1 && node.lon < lon) ? node.right : node.left;
        Node second = (first == node.left) ? node.right : node.left; // inverse de first

        _searchNearest(first, lat, lon, best);

        // on vérifie si on doit explorer l’autre branche
        // donc on calcule la distance entre le point de référence et l'hyperplan
        double delta = (axis==0 ? dLat : dLon);
        if (delta*delta < best.dist) { _searchNearest(second, lat, lon, best); }
    }

    // -----------------------

    /* Renvoie la liste des stopIdx dont la distance euclidienne au point (lat, lon) ≤ rayon. */
    public List<Integer> rangeSearch(double lat, double lon, int meters) {
        if (meters < 0) throw new IllegalArgumentException("range must be positive");
        double radius = meter2radius(meters);

        List<Integer> result = new ArrayList<>();
        _rangeSearch(root, lat, lon, radius*radius, result);
        return result;
    }

    private void _rangeSearch(Node node, double lat, double lon, double radius, List<Integer> result) {
        // Cas de base
        if (node == null) return;

        // distance euclidienne (pas sqrt = perf)
        double dLat = node.lat - lat;
        double dLon = node.lon - lon;
        double dist = dLat*dLat + dLon*dLon;

        // point dans le rayon
        if (dist <= radius) {
            result.add(node.stopIdx);
        }
        // détermination de l'ordre d'exploration des sous-arbres
        int axis = node.axis;
        double delta = (axis==0 ? dLat : dLon);

        // first est le sous-arbre le plus susceptible d'etre du côté où se trouve le point de recherche
        Node first = (delta > 0) ? node.left : node.right;
        Node second = (first == node.left) ? node.right : node.left;

        _rangeSearch(first, lat, lon, radius, result);

        // vérifier si l'autre sous-arbre peut contenir des points dans le rayon
        if (delta*delta <= radius) { _rangeSearch(second, lat, lon, radius, result); }
    }

    /* ------------- helpers ------------- */

    /* Classe utilitaire pour stocker le résultat de la recherche du point le plus proche */
    private static class Nearest {
        int idx = -1;
        double dist = Double.POSITIVE_INFINITY;
    }

    private double meter2radius(int meters) {
        return meters / 111_000.0; // 1° ≈ 111 km (approximation suffisante)
    }
}
