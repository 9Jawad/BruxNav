package be.ulb.stib.graph;

import be.ulb.stib.data.GlobalModel;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import static be.ulb.stib.tools.Utils.ensureSize;


/**
 * Graphe multimodal
 *
 * Pour chaque sommet on fusionne:
 * - les arcs piétons   (walkEdges/walkEdgesCost)
 * - les arcs transit   (transitEdges/transitEdgesCost)
 */
public final class MultiModalGraph {
    
    public final ObjectArrayList<IntArrayList> targets = new ObjectArrayList<>();
    public final ObjectArrayList<IntArrayList> costs   = new ObjectArrayList<>();

    public MultiModalGraph(GlobalModel model) {

        int nStops = model.latList.size();
        ensureSize(this.targets, nStops, null);
        ensureSize(this.costs,   nStops, null);

        for (int stopIdx = 0; stopIdx < nStops; stopIdx++) {

            IntArrayList target_transit = new IntArrayList();
            IntArrayList target_cost = new IntArrayList();

            // Arcs Marche
            IntArrayList walkT = model.walkEdges.get(stopIdx);
            IntArrayList walkC = model.walkEdgesCost.get(stopIdx);
            if (walkT != null) {
                target_transit.addAll(walkT);
                target_cost.addAll(walkC);
            }
            // Arcs Transit
            IntArrayList trT = model.transitEdges.get(stopIdx);
            IntArrayList trC = model.transitEdgesCost.get(stopIdx);
            if (trT != null) {
                target_transit.addAll(trT);
                target_cost.addAll(trC);
            }
            // ajout dans le graphe
            if (!target_transit.isEmpty()) {
                targets.set(stopIdx, target_transit);
                targets.set(stopIdx, target_cost);
            }
        }
    }
}
