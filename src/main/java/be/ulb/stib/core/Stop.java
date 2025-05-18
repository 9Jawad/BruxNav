package be.ulb.stib.core;

public final class Stop {
    public final String id;        // ex : "STIB-0470F"
    public final int    nameIdx;   // index dans stopNamePool
    public final double lat, lon;  // WGS84

    public Stop(String id, int nameIdx, double lat, double lon) {
        this.id = id;
        this.nameIdx = nameIdx;
        this.lat = lat;
        this.lon = lon;
    }
}
