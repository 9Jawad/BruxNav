package be.ulb.stib.core;


/**
 * Représente une ligne de transport (route) dans le système GTFS.
 * Cette classe est immuable et contient les informations principales d'une route.
 */
public final class Route {
    /**
     * Identifiant unique de la route selon la spécification GTFS.
     */
    public final String id;        // route_id GTFS

    /**
     * Index court de la route dans le pool des noms courts (routeShortPool).
     * Utilisé pour référencer rapidement le nom court de la route.
     */
    public final int shortIdx;     // index dans routeShortPool

    /**
     * Index long de la route dans le pool des noms longs (routeLongPool).
     * Utilisé pour référencer rapidement le nom long de la route.
     */
    public final int longIdx;      // index dans routeLongPool

    /**
     * Type de transport de la route (par exemple : "BUS", "TRAM", "TRAIN", etc.).
     */
    public final String type;

    /**
     * Construit une nouvelle instance de Route avec les paramètres spécifiés.
     *
     * @param id       Identifiant unique de la route (GTFS).
     * @param shortIdx Index du nom court de la route.
     * @param longIdx  Index du nom long de la route.
     * @param type     Type de transport de la route.
     */
    public Route(String id, int shortIdx, int longIdx, String type) {
        // Initialisation des champs de la route
        this.id = id;
        this.shortIdx = shortIdx;
        this.longIdx = longIdx;
        this.type = type;
    }
}
