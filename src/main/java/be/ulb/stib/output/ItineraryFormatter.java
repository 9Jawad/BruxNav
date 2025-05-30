package be.ulb.stib.output;

import be.ulb.stib.core.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import be.ulb.stib.data.GlobalModel;


/**
 * ItineraryFormatter fournit une méthode statique pour imprimer une description
 * lisible d'un chemin, incluant les segments à pied et en transport, avec des horaires formatés.
 * 
 * Les segments à pied sont affichés avec les noms et horaires de départ et d'arrivée.
 * Les segments en transport incluent l'agence, le type de ligne, la ligne, et les horaires de départ/arrivée formatés.
 */
public final class ItineraryFormatter {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    public static void print(List<Edge> path, int departSec, GlobalModel model){
        int t = departSec;

        for (Edge e : path){
            Stop from = model.stops.get(e.from());
            Stop to   = model.stops.get(e.to());
            String fromName = model.stopNamePool.get(from.nameIdx);
            String toName   = model.stopNamePool.get(to.nameIdx);

            if (e.mode() == 0) { // Walk
                int arr = t + e.cost();
                System.out.printf("Walk from %s (%s) to %s (%s)%n",
                        fromName, fmt(t), toName, fmt(arr));
                t = arr;
            }
            else {          // Transit
                TransitEdge te = (TransitEdge)e;
                Route r = model.routes.get(te.routeId());
                String agency = e.from().split("-", 2)[0];

                String line = (r.shortIdx >= 0)? model.routeShortPool.get(r.shortIdx) : r.id;
                System.out.printf("Take %s %s %s from %s (%s) to %s (%s)%n",
                        agency, r.type, line,
                        fromName, fmt(te.departureSec()),
                        toName,   fmt(te.arrivalSec()));
                t = te.arrivalSec();
            }
        }
    }
    
    private static String fmt(int sec){
        return LocalTime.ofSecondOfDay(sec%86_400).format(TIME_FORMATTER);
    }
}
