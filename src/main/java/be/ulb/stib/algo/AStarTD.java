package be.ulb.stib.algo;

import be.ulb.stib.core.*;
import be.ulb.stib.core.Edge;
import be.ulb.stib.core.Stop;
import be.ulb.stib.data.GlobalModel;
import be.ulb.stib.graph.MultiModalGraph;
import java.util.*;
import static be.ulb.stib.parsing.StopTimesLoader.toSec;


/** A* time-dependent sur graphe multimodal (clés = stop_id). */
public final class AStarTD {

    private static final double DEG_TO_M = 111_320.0; // 1° ≈ 111,32 km
    private static final double MAX_VEL  = 38.0;      // 38 m/s ≈ 136 km/h

    private final MultiModalGraph G;
    private final GlobalModel M;

    public AStarTD(MultiModalGraph g, GlobalModel m){
        G = g;
        M = m;
    }

    public List<Edge> search(String srcName, String dstName, String hhmmss){
        String srcId = findStopIdByName(srcName);
        String dstId = findStopIdByName(dstName);
        if(srcId==null || dstId==null) throw new IllegalArgumentException("Stop name not found");
        return search(srcId, dstId, toSec(hhmmss));
    }

    /** Recherche et renvoie la liste d’arêtes constituant le chemin optimal. */
    public List<Edge> search(String srcId, String dstId, int departSec){

        record Node(String stopId, int time, int f){}

        Map<String,Integer> bestArr   = new HashMap<>();
        Map<String,Edge>    parentEdg = new HashMap<>();

        PriorityQueue<Node> open = new PriorityQueue<>(Comparator.comparingInt(n->n.f));

        bestArr.put(srcId, departSec);
        open.add(new Node(srcId, departSec, departSec + h(srcId, dstId)));

        while (!open.isEmpty()) {
            Node cur = open.poll();

            if (cur.stopId.equals(dstId)) return reconstruct(parentEdg, dstId);
            if (cur.time > bestArr.get(cur.stopId)) continue; // surclassé

            for (Edge e : G.neighbors(cur.stopId)) {
                int newT;

                if (e.mode()==0) { // marche
                    newT = cur.time + e.cost();
                }
                else {            // transit
                    TransitEdge te = (TransitEdge)e;
                    if (cur.time > te.departureSec()) continue; // raté l'arret
                    newT = te.arrivalSec();
                }

                if (newT < bestArr.getOrDefault(e.to(), Integer.MAX_VALUE)){
                    bestArr.put(e.to(), newT);
                    parentEdg.put(e.to(), e);
                    int f = newT + h(e.to(), dstId);
                    open.add(new Node(e.to(), newT, f));
                }
            }
        }
        return List.of(); // pas de chemin
    }

    /* ============== heuristique =================== */
    private int h(String fromId, String toId){
        Stop a = M.stops.get(fromId);
        Stop b = M.stops.get(toId);
        double dLat = a.lat - b.lat;
        double dLon = a.lon - b.lon;
        double distM = Math.sqrt(dLat*dLat + dLon*dLon) * DEG_TO_M;
        return (int) Math.ceil(distM / MAX_VEL);
    }

    /* ============== reconstruction ================ */
    private List<Edge> reconstruct(Map<String,Edge> parent, String dstId){
        LinkedList<Edge> path = new LinkedList<>();
        String cur = dstId;

        while (parent.containsKey(cur)){
            Edge e = parent.get(cur);
            path.addFirst(e);
            cur = e.from();
        }
        return path;
    }

    /* -------------- utilitaires -------------- */
    private String findStopIdByName(String name){
        for (Stop s : M.stops.values()){
            String n = M.stopNamePool.get(s.nameIdx);
            if (n.equalsIgnoreCase(name)) return s.id;
        }
        return null;
    }
}
