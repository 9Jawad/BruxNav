package be.ulb.stib.graph;

import be.ulb.stib.core.Edge;
import be.ulb.stib.core.TransitEdge;
import be.ulb.stib.core.WalkEdge;
import be.ulb.stib.data.GlobalModel;
import java.util.*;


/**
 * Graphe multimodal
 * Pour chaque sommet on fusionne les arcs piétons avec les arcs transit
 */
public final class MultiModalGraph {

    /** stop_id → liste d’arêtes sortantes (walk + transit). */
    public final Map<String,List<Edge>> graph = new HashMap<>();

    public MultiModalGraph(GlobalModel model, List<WalkEdge> walk, List<TransitEdge> trans) {
        for (String id: model.stops.keySet()) graph.put(id, new ArrayList<>());
        
        walk.forEach(e -> graph.get(e.from()).add(e));
        trans.forEach(e -> graph.get(e.from()).add(e));
    }
    
    public List<Edge> neighbors(String stopId){ 
        return graph.get(stopId); 
    }
}
