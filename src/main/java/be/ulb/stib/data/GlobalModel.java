package be.ulb.stib.data;

import be.ulb.stib.spatial.KDTree;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import static be.ulb.stib.tools.Utils.ensureSize;


/* Conteneur des données fusionnées de toutes les agences, plus index spatial. */
public final class GlobalModel extends AgencyModel {

    // dictionnaire d'ID globaux
    public final Map<String, Integer> idx = new LinkedHashMap<>();

    // index spatiaux
    public final ObjectArrayList<IntArrayList> walkNeighbors = new ObjectArrayList<>();
    public final ObjectArrayList<IntArrayList> transitEdges = new ObjectArrayList<>();

    public GlobalModel() {
        super();
    }

    public void initSpatial(int radius) {
        ensureSize(walkNeighbors, stopNameIdxList.size(), -1);
        KDTree tree = new KDTree(this);

        for (int stopIdx = 0; stopIdx < stopNameIdxList.size(); stopIdx++) {
            int value = stopNameIdxList.get(stopIdx);
            if (value < 0) continue;

            double lat = latList.getDouble(stopIdx);
            double lon = lonList.getDouble(stopIdx);

            IntArrayList neighbors = tree.rangeSearch(lat, lon, radius, stopIdx);
            walkNeighbors.set(stopIdx, neighbors);
        }
    }
}
