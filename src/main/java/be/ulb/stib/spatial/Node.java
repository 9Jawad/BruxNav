package be.ulb.stib.spatial;

final class Node {
    final int stopIdx;        // Valeur  index global du stop
    final double lat, lon;    //         coordonnées
    Node left, right;         // Enfants
    int axis;                 // Médiane de comparaison (lat = 0, lon = 1)

    Node(int stopIdx, double lat, double lon) {
        this.stopIdx = stopIdx;
        this.lat = lat;
        this.lon = lon;
    }
}
    