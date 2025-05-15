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
            if (model.latList.get(stopIdx) < 0) continue;

            IntArrayList targetTransit = new IntArrayList();
            IntArrayList targetCost = new IntArrayList();

            // Arcs Marche
            IntArrayList walkT = model.walkEdges.get(stopIdx);
            IntArrayList walkC = model.walkEdgesCost.get(stopIdx);
            if (walkT != null) {
                targetTransit.addAll(walkT);
                targetCost.addAll(walkC);
            }
            // Arcs Transit
            IntArrayList trT = model.transitEdges.get(stopIdx);
            IntArrayList trC = model.transitEdgesCost.get(stopIdx);
            if (trT != null) {
                targetTransit.addAll(trT);
                targetCost.addAll(trC);
            }
            // ajout dans le graphe
            if (!targetTransit.isEmpty()) {
                targets.set(stopIdx, targetTransit);
                costs.set(stopIdx, targetCost);
            }
        }
    }
}
