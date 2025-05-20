package be.ulb.stib.data;

import be.ulb.stib.core.*;


/**
 * La classe GlobalModel étend AgencyModel et fournit des fonctionnalités
 * pour fusionner les données de plusieurs instances de AgencyModel en un modèle global unique.
 * 
 * La méthode addAgency(AgencyModel) effectue une fusion en trois étapes:
 *   Fusionne les arrêts en internant les noms d'arrêts et en les ajoutant à la collection globale d'arrêts.
 *   Fusionne les lignes en internant les noms courts et longs des lignes, puis en les ajoutant à la collection globale de lignes.
 *   Fusionne les trajets en les associant aux lignes et arrêts globaux, ensuite en ajoutant les horaires d'arrêt correspondants.
 */
public final class GlobalModel extends AgencyModel {

    public void addAgency(AgencyModel agency) {
        // fusion des stops 
        for (Stop s : agency.stops.values()) {
            int globalNameIdx = stopNamePool.intern(agency.stopNamePool.get(s.nameIdx));
            stops.put(s.id, new Stop(s.id, globalNameIdx, s.lat, s.lon));
        }
        // fusion des routes
        for (Route r : agency.routes.values()) {
            int shortIdx = routeShortPool.intern(agency.routeShortPool.get(r.shortIdx));
            int longIdx  = routeLongPool .intern(agency.routeLongPool .get(r.longIdx ));
            routes.put(r.id, new Route(r.id, shortIdx, longIdx, r.type));
        }
        // fusion des trips 
        for (Trip t : agency.trips.values()) {
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
