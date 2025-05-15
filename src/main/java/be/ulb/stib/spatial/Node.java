package be.ulb.stib.spatial;

final class Node {
    int axis;                 // médiane de comparaison (lat = 0, lon = 1)
    final int stopIdx;        // index global du stop
    final double lat, lon;    // coordonnées

    Node left, right;         // Enfants

    Node(int stopIdx, double lat, double lon) {
        this.stopIdx = stopIdx;
        this.lat = lat;
        this.lon = lon;
    }
}
    