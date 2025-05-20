package be.ulb.stib.core;


/**
 * Représente un horaire de passage à un arrêt spécifique.
 *
 * @param stop         L'arrêt concerné.
 * @param departureSec L'heure de départ en secondes depuis minuit.
 * @param sequence     L'ordre de passage dans l'itinéraire.
 */
public record StopTime(Stop stop, int departureSec, int sequence) {}
