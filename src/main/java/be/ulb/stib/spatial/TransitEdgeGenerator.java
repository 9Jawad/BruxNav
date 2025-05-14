package be.ulb.stib.spatial;

import be.ulb.stib.data.GlobalModel;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import static be.ulb.stib.tools.Utils.ensureSize;


/**
 * Construit, pour chaque arrêt, les arêtes «transit» (déplacements à bord
 * d’un même véhicule) à partir des tableaux stop_times du GlobalModel.
 * Les arêtes sont stockées dans deux tableaux parallèles du GlobalModel:
 *   transitEdges     : ObjectArrayList<IntArrayList>  – cibles
 *   transitEdgesCost : ObjectArrayList<IntArrayList>  – coût (secondes)
 */
public final class TransitEdgeGenerator {

    public static void build(GlobalModel model) {
        int nStops = model.latList.size();
        ensureSize(model.transitEdges,     nStops, null);
        ensureSize(model.transitEdgesCost, nStops, null);

        IntArrayList ofsDense = model.tripOfsDense;
        for (int j = 0; j < ofsDense.size() - 1; j++) {
            int from = ofsDense.getInt(j);
            int   to = ofsDense.getInt(j + 1);

            // parcours les arrets d'un meme trajet
            for (int i = from; i < to - 1; i++) {
                int stopA = model.stopIdxByTimeList.getInt(i);
                int stopB = model.stopIdxByTimeList.getInt(i + 1);
                int timeA = model.depSecList.getInt(i);
                int timeB = model.depSecList.getInt(i + 1);
                int cost  = Math.max(0, timeB - timeA);   // sécurité

                // stopA → stopB
                addEdge(model, stopA, stopB, cost);
            }
        }
    }

    /**
     * Ajoute une arête de transit (origine → destination, avec un coût en secondes)
     * dans les listes parallèles du modèle global.
     */
    private static void addEdge(GlobalModel model, int stopIdxA, int stopIdxB, int cost) {
        ObjectArrayList<IntArrayList> transitEdges = model.transitEdges;
        ObjectArrayList<IntArrayList> transitEdgesCost = model.transitEdgesCost;

        IntArrayList neighbors = transitEdges.get(stopIdxA);
        IntArrayList costs = transitEdgesCost.get(stopIdxA);

        if (neighbors == null) {
            // Première connexion pour cet arrêt, initialisation des listes
            neighbors = new IntArrayList();
            costs = new IntArrayList();
            transitEdges.set(stopIdxA, neighbors);
            transitEdgesCost.set(stopIdxA, costs);
        }
        neighbors.add(stopIdxB);
        costs.add(cost);
    }
}
