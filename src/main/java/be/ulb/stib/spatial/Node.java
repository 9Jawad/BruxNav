package be.ulb.stib.spatial;

/**
 * Représente un nœud dans une structure de données spatiale, telle qu'un arbre k-d.
 * Chaque nœud contient un identifiant unique, des coordonnées géographiques (latitude et longitude),
 * ainsi que des références vers ses nœuds enfants gauche et droit.
 */
final class Node {
    final String id;
    final double lat, lon;
    final Node left, right;

    Node(String id,double lat,double lon,Node l,Node r){
        this.id = id;
        this.lat = lat; this.lon = lon;
        left = l; right = r;
    }
}