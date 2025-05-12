package be.ulb.stib.data;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;


/* Conteneur des données fusionnées de toutes les agences, plus index spatial. */
public final class GlobalModel extends AgencyModel {

    public final ObjectArrayList<IntArrayList> walkNeighbors = new ObjectArrayList<>();
    public final ObjectArrayList<IntArrayList> transitEdges = new ObjectArrayList<>();

    public GlobalModel() {
        super();
        initSpatial();
    }

    private void initSpatial() {
        int nStops = stopCount();
        for (int i = 0; i < nStops; i++) {
            walkNeighbors.add(new IntArrayList());
            transitEdges.add(new IntArrayList());
        }
    }
}
