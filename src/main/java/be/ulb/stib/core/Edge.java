package be.ulb.stib.core;

/** Arête générique du graphe multimodal. */
public sealed interface Edge permits WalkEdge, TransitEdge {
    String from();   // stop_id source
    String to();     // stop_id destination
    int    cost();   // durée (sec)
    int    mode();   // 0 = walk, 1 = transit
}
