package be.ulb.stib.graph;

import be.ulb.stib.core.Edge;
import be.ulb.stib.core.TransitEdge;
import be.ulb.stib.core.WalkEdge;
import be.ulb.stib.data.GlobalModel;
import java.util.*;


/**
 * Représente un graphe multimodal où chaque arrêt (identifié par stop_id) est associé
 * à une liste d'arêtes sortantes, incluant à la fois les connexions piétonnes et de transit.
 * 
 * Le graphe est construit à partir d'un modèle global des arrêts, d'une liste d'arêtes piétonnes
 * et d'une liste d'arêtes de transit. Chaque arrêt est associé à ses arêtes sortantes.
 */
public final class MultiModalGraph {

    /** stop_id → liste d’arêtes sortantes (walk + transit). */
    public final Map<String,List<Edge>> graph = new HashMap<>();

    public MultiModalGraph(GlobalModel model, List<WalkEdge> walk, List<TransitEdge> trans) {
        for (String id: model.stops.keySet()) graph.put(id, new ArrayList<>());
        
        walk.forEach(e  -> graph.get(e.from()).add(e));
        trans.forEach(e -> graph.get(e.from()).add(e));
    }
    
    public List<Edge> neighbors(String stopId){ 
        return graph.get(stopId); 
    }
}
