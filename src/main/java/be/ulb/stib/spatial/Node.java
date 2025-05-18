package be.ulb.stib.spatial;

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