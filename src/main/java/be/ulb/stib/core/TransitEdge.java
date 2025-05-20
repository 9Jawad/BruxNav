package be.ulb.stib.core;


/**
 * Représente une arête de transit entre deux arrêts dans un réseau de transport en commun.
 *
 * @param from         L'identifiant de l'arrêt de départ.
 * @param to           L'identifiant de l'arrêt d'arrivée.
 * @param departureSec L'heure de départ en secondes depuis minuit.
 * @param arrivalSec   L'heure d'arrivée en secondes depuis minuit.
 * @param routeId      L'identifiant de la ligne ou du trajet.
 * @param tripId       L'identifiant du voyage spécifique.
 */
public record TransitEdge(String from, String to,
                          int departureSec, int arrivalSec,
                          String routeId, String tripId) implements Edge
{
    @Override public int cost() { return arrivalSec - departureSec; }

    @Override public int mode() { return 1; }
}
