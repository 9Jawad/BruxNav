package be.ulb.stib.core;


/**
 * Représente un arrêt de transport en commun.
 * 
 * Cette classe est immuable et contient les informations essentielles d'un arrêt,
 * telles que son identifiant unique, l'index de son nom dans un pool de noms,
 * ainsi que ses coordonnées géographiques (latitude et longitude).
 */
public final class Stop {
    /**
     * Identifiant unique de l'arrêt (exemple : "STIB-0470F").
     */
    public final String id;

    /**
     * Index du nom de l'arrêt dans le pool de noms (stopNamePool).
     */
    public final int nameIdx;

    /**
     * Latitude de l'arrêt selon le système de coordonnées WGS84.
     */
    public final double lat;

    /**
     * Longitude de l'arrêt selon le système de coordonnées WGS84.
     */
    public final double lon;

    /**
     * Construit un nouvel arrêt avec les informations spécifiées.
     *
     * @param id      Identifiant unique de l'arrêt.
     * @param nameIdx Index du nom de l'arrêt dans le pool de noms.
     * @param lat     Latitude de l'arrêt.
     * @param lon     Longitude de l'arrêt.
     */
    public Stop(String id, int nameIdx, double lat, double lon) {
        // Initialisation des champs de l'arrêt
        this.id = id;
        this.nameIdx = nameIdx;
        this.lat = lat;
        this.lon = lon;
    }
}
