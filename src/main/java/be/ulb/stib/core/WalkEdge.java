package be.ulb.stib.core;


/**
 * Représente une arête dans un graphe correspondant à un chemin piéton entre deux points.
 *
 * @param from le point de départ de l'arête piétonne
 * @param to   le point d'arrivée de l'arête piétonne
 * @param cost le coût associé à la marche de "from" à "to"
 *
 * Implémente l'interface Edge et définit le mode comme marche (mode 0).
 */
public record WalkEdge(String from, String to, int cost) implements Edge {
    @Override public int mode() { return 0; }
}
