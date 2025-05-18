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

    private static final int PENALTY_ROUTE  = 600;       // 10 min
    private static final int PENALTY_MODE   = 120;       // 2 min
    private static final double DEG_TO_M    = 111_320.0; // 1° ≈ 111,32 km
    private static final double MAX_VEL     = 38.0;      // 38 m/s ≈ 136 km/h

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
    public List<Edge> search(String srcId,String dstId,int departSec){

        record Node(String stopId,int time,Edge prev,int f){}   // prev = arête par laquelle on arrive

        Map<String,Integer> bestArr   = new HashMap<>();
        Map<String,Edge>    parentEdg = new HashMap<>();
        PriorityQueue<Node> open = new PriorityQueue<>(Comparator.comparingInt(n->n.f));

        bestArr.put(srcId, departSec);
        open.add(new Node(srcId, departSec, null, departSec + h(srcId, dstId)));

        while (!open.isEmpty()) {
            Node cur = open.poll();

            if (cur.stopId.equals(dstId)) return reconstruct(parentEdg, dstId);
            if (cur.time > bestArr.get(cur.stopId)) continue;

            for (Edge e : G.neighbors(cur.stopId)){

                // coût
                int arrival;
                if (e.mode()==0){ // Walk
                    arrival = cur.time + e.cost();
                }
                else {           // Transit
                    TransitEdge te = (TransitEdge)e;
                    if (cur.time > te.departureSec()) continue;   // stop raté
                    arrival = te.arrivalSec();
                }

                // pénalités de correspondance
                int penalty = 0;

                if (cur.prev != null) {
                    boolean modeChange = (cur.prev.mode() != e.mode());

                    if (modeChange) penalty += PENALTY_MODE;

                    if (e.mode()==1 && cur.prev.mode()==1){           // transit → transit
                        TransitEdge prevTe = (TransitEdge) cur.prev;
                        TransitEdge newTe  = (TransitEdge) e;
                        if (!prevTe.routeId().equals(newTe.routeId()))
                            penalty += PENALTY_ROUTE;
                    }
                }
                arrival += penalty;

                if (arrival < bestArr.getOrDefault(e.to(), Integer.MAX_VALUE)){
                    bestArr.put(e.to(), arrival);
                    parentEdg.put(e.to(), e);
                    int f = arrival + h(e.to(), dstId);
                    open.add(new Node(e.to(), arrival, e, f));
                }
            }
        }
        return List.of();
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
