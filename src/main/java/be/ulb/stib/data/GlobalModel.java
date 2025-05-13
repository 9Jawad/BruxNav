package be.ulb.stib.data;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.LinkedHashMap;
import java.util.Map;


/* Conteneur des données fusionnées de toutes les agences, plus index spatial. */
public final class GlobalModel extends AgencyModel {

    // dictionnaire d'ID globaux
    public final Map<String,Integer> idx = new LinkedHashMap<>();

    // index spatiaux
    public final ObjectArrayList<IntArrayList> walkNeighbors = new ObjectArrayList<>();
    public final ObjectArrayList<IntArrayList> transitEdges  = new ObjectArrayList<>();

    public GlobalModel() {
        super();
    }

    public void initSpatial(int nStops) {
        for (int i = 0; i < nStops; i++) {
            walkNeighbors.add(new IntArrayList());
            transitEdges.add(new IntArrayList());
        }
    }
}
