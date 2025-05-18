package be.ulb.stib.core;

public record TransitEdge(String from, String to,
                          int departureSec, int arrivalSec,
                          String routeId, String tripId) implements Edge
{
    @Override public int cost() { return arrivalSec - departureSec; }

    @Override public int mode() { return 1; }
}
