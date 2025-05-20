package be.ulb.stib.data;

import be.ulb.stib.core.Route;
import be.ulb.stib.core.Stop;
import be.ulb.stib.core.Trip;
import java.util.HashMap;
import java.util.Map;


/** Données brutes d’une seule agence. */
public class AgencyModel {

    /* === pools de chaînes (optimisation mémoire) === */
    public final StringPool stopNamePool  = new StringPool();      // Pool pour les noms d'arrêts
    public final StringPool routeShortPool= new StringPool();      // Pool pour les noms courts de lignes
    public final StringPool routeLongPool = new StringPool();      // Pool pour les noms longs de lignes

    /* === dictionnaires GTFS → objets === */
    public final Map<String, Stop> stops  = new HashMap<>();       // Identifiant GTFS → arrêt
    public final Map<String, Route> routes = new HashMap<>();      // Identifiant GTFS → ligne
    public final Map<String, Trip> trips  = new HashMap<>();       // Identifiant GTFS → trajet
}
