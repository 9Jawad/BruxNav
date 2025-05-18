package be.ulb.stib.core;

public record WalkEdge(String from, String to, int cost) implements Edge {
    @Override public int mode() { return 0; }
}
