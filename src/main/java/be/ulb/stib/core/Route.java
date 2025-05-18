package be.ulb.stib.core;

public final class Route {
    public final String id;        // route_id GTFS
    public final int shortIdx;     // index dans routeShortPool
    public final int longIdx;      // index dans routeLongPool
    public final String type;      // "BUS", "TRAM", "TRAIN", …

    public Route(String id, int shortIdx, int longIdx, String type) {
        this.id = id;
        this.shortIdx = shortIdx;
        this.longIdx = longIdx;
        this.type = type;
    }
}
