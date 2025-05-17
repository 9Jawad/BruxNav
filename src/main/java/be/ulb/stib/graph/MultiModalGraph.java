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
    public final ObjectArrayList<IntArrayList> modes   = new ObjectArrayList<>();
    public final ObjectArrayList<IntArrayList> routeIdxPerArc = new ObjectArrayList<>();
    public final int size;

    public MultiModalGraph(GlobalModel model) {
        this.size = model.latList.size();
        ensureSize(targets,        size, null);
        ensureSize(costs,          size, null);
        ensureSize(modes,          size, null);
        ensureSize(routeIdxPerArc, size, null);

        for (int stop = 0; stop < size; stop++) {
            if (model.latList.get(stop) < 0) continue;

            IntArrayList t  = new IntArrayList();
            IntArrayList c  = new IntArrayList();
            IntArrayList m  = new IntArrayList();
            IntArrayList ri = new IntArrayList();

            /* ---- WALK ---- */
            IntArrayList walkT = model.walkEdges.get(stop);
            IntArrayList walkC = model.walkEdgesCost.get(stop);
            if (walkT != null) {
                t.addAll(walkT);
                c.addAll(walkC);
                for (int k = 0; k < walkT.size(); k++) {
                    m .add(0);      // 0 = walk
                    ri.add(-1);     // pas de ligne
                }
            }

            /* ---- TRANSIT ---- */
            IntArrayList trT = model.transitEdges.get(stop);
            IntArrayList trC = model.transitEdgesCost.get(stop);
            IntArrayList trR = model.transitEdgesRouteIdx.get(stop);
            if (trT != null) {
                t.addAll(trT);
                c.addAll(trC);
                ri.addAll(trR);
                for (int k = 0; k < trT.size(); k++) {
                    m .add(1);          // 1 = transit
                }
            }

            if (!t.isEmpty()) {
                targets       .set(stop, t);
                costs         .set(stop, c);
                modes         .set(stop, m);
                routeIdxPerArc.set(stop, ri);
            }
        }
    }
    public int routeIdx(int fromStop, int arcOrd) {
        IntArrayList ri = routeIdxPerArc.get(fromStop);
        return (ri == null || arcOrd >= ri.size()) ? -1
                : ri.getInt(arcOrd);
    }
}
