package be.ulb.stib.core;


/**
 * Représente une arête dans un graphe de transport, reliant deux arrêts.
 * Cette interface scellée est implémentée par WalkEdge (arête à pied) et TransitEdge (arête en transport).
 *
 * @method String from()   Retourne l'identifiant de l'arrêt source.
 * @method String to()     Retourne l'identifiant de l'arrêt de destination.
 * @method int cost()      Retourne la durée du trajet en secondes.
 * @method int mode()      Retourne le mode de déplacement : 0 pour la marche, 1 pour le transport en commun.
 */
public sealed interface Edge permits WalkEdge, TransitEdge {
    String from();   // stop_id source
    String to();     // stop_id destination
    int    cost();   // durée (sec)
    int    mode();   // 0 = walk, 1 = transit
}
