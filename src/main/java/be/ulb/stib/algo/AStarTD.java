package be.ulb.stib.algo;

import be.ulb.stib.core.*;
import be.ulb.stib.core.Edge;
import be.ulb.stib.core.Stop;
import be.ulb.stib.data.GlobalModel;
import be.ulb.stib.graph.MultiModalGraph;
import java.util.*;
import static be.ulb.stib.parsing.StopTimesLoader.toSec;


/** 
 * A* time-dependent sur graphe multimodal (clés = stop_id).
 * 
 * Cette classe implémente l'algorithme de recherche A* pour les réseaux de transport multimodaux,
 * en tenant compte des horaires dépendants du temps et des pénalités de correspondance.
 * 
 * Ajoute des pénalités lors des changements de mode, de ligne ou de correspondance.
 * Supporte les arêtes de marche et de transport, avec coûts dépendant du temps pour le transport.
 * L'heuristique utilise la distance à vol d'oiseau divisée par la vitesse maximale.
 * 
 * Exemple d'utilisation:
 *   AStarTD astar = new AStarTD(graph, model);
 *   path = astar.search("SourceStop", "DestinationStop", "08:00:00");
 */
public final class AStarTD {

    // Constantes de pénalité (en secondes)
    private static final int PENALTY_ROUTE  = 120;          // 2 min pour changement de ligne
    private static final int PENALTY_MODE   = 300;          // 5 min pour changement de mode (ex: bus -> métro)

    private static final double DEG_TO_M    = 111_320.0;    // Conversion degré -> mètres (approx.)
    private static final double MAX_VEL     = 17.0;         // Vitesse max (m/s) ≈ 60 km/h

    private final MultiModalGraph G;                        // Graphe multimodal
    private final GlobalModel M;                            // Modèle global (arrêts, etc.)


    /**
     * Constructeur principal.
     * @param g Graphe multimodal
     * @param m Modèle global
     */
    public AStarTD(MultiModalGraph g, GlobalModel m){
        G = g;
        M = m;
    }


    /**
     * Recherche le chemin optimal entre deux arrêts à partir de leur nom et d'une heure de départ.
     * @param srcName Nom de l'arrêt de départ
     * @param dstName Nom de l'arrêt d'arrivée
     * @param hhmmss Heure de départ au format "HH:MM:SS"
     * @return Liste d'arêtes représentant le chemin optimal
     */
    public List<Edge> search(String srcName, String dstName, String hhmmss){
        String srcId = findStopIdByName(srcName);
        String dstId = findStopIdByName(dstName);
        if(srcId==null || dstId==null) throw new IllegalArgumentException("Stop name not found");
        return search(srcId, dstId, toSec(hhmmss));
    }


    /**
     * Recherche et renvoie la liste d’arêtes constituant le chemin optimal.
     * @param srcId id de l'arrêt de départ
     * @param dstId id de l'arrêt d'arrivée
     * @param departSec heure de départ en secondes
     * @return Liste d'arêtes représentant le chemin optimal
     */
    public List<Edge> search(String srcId,String dstId,int departSec){

        // Représente un noeud dans la file de priorité
        record Node(String stopId, int time, Edge prev, int f) {}   // prev = arête par laquelle on arrive

        Map<String,Integer> bestArr   = new HashMap<>(); // Meilleur temps d'arrivée connu pour chaque arrêt
        Map<String,Edge>    parentEdg = new HashMap<>(); // Pour reconstruire le chemin
        PriorityQueue<Node> open = new PriorityQueue<>(Comparator.comparingInt(n->n.f)); // File de priorité

        bestArr.put(srcId, departSec);
        open.add(new Node(srcId, departSec, null, departSec + h(srcId, dstId)));

        while (!open.isEmpty()) {
            Node cur = open.poll();

            // Si on atteint la destination, on reconstruit le chemin
            if (cur.stopId.equals(dstId)) return reconstruct(parentEdg, dstId);
            // Si on a déjà trouvé un meilleur chemin vers ce noeud, on ignore
            if (cur.time > bestArr.get(cur.stopId)) continue;

            // Parcours des voisins (arêtes sortantes)
            for (Edge e : G.neighbors(cur.stopId)){
                // Calcul du coût d'arrivée
                int arrival;
                if (e.mode()==0){ // Marche à pied
                    arrival = cur.time + e.cost();
                }
                else {           // Transport en commun
                    TransitEdge te = (TransitEdge)e;
                    if (cur.time > te.departureSec()) continue;   // On a raté le départ
                    arrival = te.arrivalSec();
                }
                // Calcul des pénalités de correspondance
                int penalty = 0;

                if (cur.prev != null) {

                    boolean modeChange = (cur.prev.mode() != e.mode());
                    if (!modeChange && e.mode() == 0) continue;

                    if (modeChange) penalty += PENALTY_MODE;

                    // Si on reste en mode transport, vérifier le changement de ligne
                    if (e.mode()==1 && cur.prev.mode()==1){           // transit → transit
                        TransitEdge prevTe = (TransitEdge) cur.prev;
                        TransitEdge newTe  = (TransitEdge) e;
                        if (!prevTe.routeId().equals(newTe.routeId()))
                            penalty += PENALTY_ROUTE;
                    }
                }
                // Si on trouve un meilleur chemin vers ce voisin, on l'ajoute à la file
                if (arrival < bestArr.getOrDefault(e.to(), Integer.MAX_VALUE)){
                    bestArr.put(e.to(), arrival);
                    parentEdg.put(e.to(), e);
                    int f = arrival + h(e.to(), dstId) + penalty;
                    open.add(new Node(e.to(), arrival, e, f));
                }
            }
        }
        // Aucun chemin trouvé
        return List.of();
    }


    /* ============== heuristique =================== */
    /**
     * Heuristique basée sur la distance à vol d'oiseau entre deux arrêts.
     * @param fromId id de l'arrêt de départ
     * @param toId id de l'arrêt d'arrivée
     * @return Estimation du temps minimal restant (en secondes)
     */
    private int h(String fromId, String toId){
        Stop a = M.stops.get(fromId);
        Stop b = M.stops.get(toId);
        double dLat = a.lat - b.lat;
        double dLon = a.lon - b.lon;
        double distM = Math.sqrt(dLat*dLat + dLon*dLon) * DEG_TO_M;
        return (int) Math.ceil(distM / MAX_VEL);
    }


    /* ============== reconstruction ================ */
    /**
     * Reconstruit le chemin optimal à partir de la map des parents.
     * @param parent Map des arêtes précédentes pour chaque arrêt
     * @param dstId id de l'arrêt d'arrivée
     * @return Liste d'arêtes du chemin optimal
     */
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


    /* ============== utilitaires ============== */
    /**
     * Trouve l'identifiant d'un arrêt à partir de son nom (insensible à la casse).
     * @param name Nom de l'arrêt
     * @return id de l'arrêt ou null si non trouvé
     */
    private String findStopIdByName(String name){
        for (Stop s : M.stops.values()){
            String n = M.stopNamePool.get(s.nameIdx);
            if (n.equalsIgnoreCase(name)) return s.id;
        }
        return null;
    }
}
