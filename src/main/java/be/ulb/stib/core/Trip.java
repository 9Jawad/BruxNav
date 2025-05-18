package be.ulb.stib.core;

import java.util.ArrayList;
import java.util.List;


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
