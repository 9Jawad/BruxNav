package be.ulb.stib.data;

import be.ulb.stib.spatial.KDTree;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import static be.ulb.stib.tools.Utils.ensureSize;


/* Conteneur des données fusionnées de toutes les agences, plus index spatial. */
public final class GlobalModel extends AgencyModel {

    // dictionnaire globaux
    public final Map<String, Integer> idx = new LinkedHashMap<>();
    public final Map<Integer, String> id  = new LinkedHashMap<>();

    // index spatiaux
    public final ObjectArrayList<IntArrayList> walkEdges        = new ObjectArrayList<>();
    public final ObjectArrayList<IntArrayList> walkEdgesCost    = new ObjectArrayList<>();
    public final ObjectArrayList<IntArrayList> transitEdges     = new ObjectArrayList<>();
    public final ObjectArrayList<IntArrayList> transitEdgesCost = new ObjectArrayList<>();

    public GlobalModel() {
        super();
    }
}
