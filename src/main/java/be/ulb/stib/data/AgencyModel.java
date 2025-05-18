package be.ulb.stib.data;

import be.ulb.stib.core.Route;
import be.ulb.stib.core.Stop;
import be.ulb.stib.core.Trip;
import java.util.HashMap;
import java.util.Map;


/** Données brutes d’une seule agence. */
public final class AgencyModel {

    /* === pools de chaînes (optimisation mémoire) === */
    public final StringPool stopNamePool  = new StringPool();
    public final StringPool routeShortPool= new StringPool();
    public final StringPool routeLongPool = new StringPool();

    /* === dictionnaires GTFS → objets === */
    public final Map<String, Stop> stops  = new HashMap<>();
    public final Map<String, Route> routes = new HashMap<>();
    public final Map<String, Trip> trips  = new HashMap<>();
}
