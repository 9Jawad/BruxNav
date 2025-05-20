package be.ulb.stib.spatial;

import be.ulb.stib.core.StopTime;
import be.ulb.stib.core.TransitEdge;
import be.ulb.stib.core.Trip;
import be.ulb.stib.data.GlobalModel;
import java.util.ArrayList;
import java.util.List;


/**
 * TransitEdgeGenerator fournit une méthode statique permettant de créer une liste
 * d'objets TransitEdge en parcourant tous les trajets du GlobalModel fourni.
 * Pour chaque trajet, elle génère des arêtes entre arrêts consécutifs, en capturant les heures
 * de départ ainsi que les identifiants de la ligne et du trajet associés.
 */
public final class TransitEdgeGenerator {

    public static List<TransitEdge> generate(GlobalModel model){
        List<TransitEdge> edges = new ArrayList<>();
        
        for (Trip t : model.trips.values()) {
            var list = t.steps();

            for (int i=0; i < list.size()-1; i++){
                StopTime a = list.get(i);
                StopTime b = list.get(i+1);

                edges.add(new TransitEdge(
                        a.stop().id, b.stop().id,
                        a.departureSec(), b.departureSec(),
                        t.route.id, t.id));
            }
        }
        return edges;
    }
}
