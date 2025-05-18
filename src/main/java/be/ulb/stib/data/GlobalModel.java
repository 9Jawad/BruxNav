package be.ulb.stib.data;

import be.ulb.stib.core.*;
import java.util.HashMap;
import java.util.Map;


/** Union de toutes les agences. */
public final class GlobalModel {

    /* pools fusionnés */
    public final StringPool stopNamePool  = new StringPool();
    public final StringPool routeShortPool= new StringPool();
    public final StringPool routeLongPool = new StringPool();

    /* maps globales */
    public final Map<String, Stop> stops  = new HashMap<>();
    public final Map<String, Route> routes = new HashMap<>();
    public final Map<String, Trip> trips  = new HashMap<>();


    /* ------------ FUSION ------------ */

    public void addAgency(AgencyModel a) {
        /* 1) fusion des stops */
        for (Stop s : a.stops.values()) {
            int globalNameIdx = stopNamePool.intern(a.stopNamePool.get(s.nameIdx));
            stops.put(s.id, new Stop(s.id, globalNameIdx, s.lat, s.lon));
        }
        /* 2) fusion des routes */
        for (Route r : a.routes.values()) {
            int shortIdx = routeShortPool.intern(a.routeShortPool.get(r.shortIdx));
            int longIdx  = routeLongPool .intern(a.routeLongPool .get(r.longIdx ));
            routes.put(r.id, new Route(r.id, shortIdx, longIdx, r.type));
        }
        /* 3) fusion des trips */
        for (Trip t : a.trips.values()) {
            Route globalRoute = routes.get(t.route.id);
            Trip  gTrip       = new Trip(t.id, globalRoute);
            for (StopTime st : t.steps()) {
                Stop gStop = stops.get(st.stop().id);
                gTrip.addStopTime(new StopTime(gStop, st.departureSec(), st.sequence()));
            }
            trips.put(t.id, gTrip);
        }
    }
}
