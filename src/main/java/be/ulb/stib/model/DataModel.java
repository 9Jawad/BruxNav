package be.ulb.stib.model;
import be.ulb.stib.data.IntDictionary;

public final class DataModel {
    // ---- dictionnaire global ----
    public final IntDictionary idDict = new IntDictionary();

    // ---- stops ----
    public double[] lat;       // taille = nStops
    public double[] lon;
    public int[]    nameIdx;   // index dans names[]
    public String[] names;     // pool de noms uniques

    // ---- routes ET trips (à venir) ----
    short[] routeType;
    int[]   routeIdxOfTrip;

    // ---- constructeur vide ----
    public DataModel() {}
}
