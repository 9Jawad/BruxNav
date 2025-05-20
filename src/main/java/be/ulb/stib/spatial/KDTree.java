package be.ulb.stib.spatial;

import be.ulb.stib.core.Stop;
import java.util.ArrayList;
import java.util.List;


/** KD-Tree 2D minimal pour (lat,lon) → stopId. */
public final class KDTree {

    private final Node root;

    public KDTree(List<Stop> stops) {
        var pts = new ArrayList<>(stops);
        this.root = build(pts, 0);
    }

    private static Node build(List<Stop> pts,int depth){
        if(pts.isEmpty()) return null;

        int axis = depth&1;
        pts.sort((a, b)->Double.compare(axis==0? a.lat : a.lon, axis==0? b.lat : b.lon));

        int mid = pts.size()/2;
        Stop s = pts.get(mid);

        return new Node(s.id, s.lat, s.lon,
                build(pts.subList(0, mid), depth+1),
                build(pts.subList(mid+1, pts.size()), depth+1));
    }


    /* =============== Recherche par rayon =============== */

    public List<String> radiusSearch(double qLat, double qLon, double radiusMeters){
        double radiusDeg = radiusMeters / 111_320.0;
        double r2 = radiusDeg * radiusDeg;

        List<String> out = new ArrayList<>();
        search(root, qLat, qLon, r2,0, out);
        return out;
    }

    private static void search(Node n, double qLat, double qLon, double r2, int depth, List<String> out){
        if(n==null) return;

        double dLat = n.lat - qLat;
        double dLon = n.lon - qLon;
        double dist2 = dLat*dLat + dLon*dLon;

        if(dist2 <= r2) out.add(n.id);

        int axis=depth&1;

        double delta = (axis==0? dLat : dLon); // distance avec l'hyperplan

        if (delta>0) { search(n.left, qLat, qLon, r2, depth+1, out);
            if (delta*delta <= r2) search(n.right, qLat, qLon, r2, depth+1, out);
        }
        else { search(n.right, qLat, qLon, r2, depth+1, out);
            if (delta*delta <= r2) search(n.left, qLat, qLon, r2, depth+1, out);
        }
    }
}
