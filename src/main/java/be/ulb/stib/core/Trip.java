package be.ulb.stib.core;

import java.util.ArrayList;
import java.util.List;


/**
 * Représente un trajet de transport en commun, qui est une séquence d'arrêts le long d'une route spécifique.
 * Chaque Trip est identifié par un identifiant unique et est associé à une Route.
 * Le trajet maintient une liste ordonnée d'objets StopTime, représentant les arrêts dans l'ordre où ils sont visités (triés par stop_sequence).
 */
public final class Trip {
    public final String id;
    public final Route  route;

    /* Étapes ordonnées par stop_sequence croissant. */
    private final List<StopTime> stopTimes = new ArrayList<>();

    public Trip(String id, Route route) {
        this.id = id;
        this.route = route;
    }

    public void addStopTime(StopTime st) {
        stopTimes.add(st);
    }

    public List<StopTime> steps() {
        return stopTimes;
    }
}
