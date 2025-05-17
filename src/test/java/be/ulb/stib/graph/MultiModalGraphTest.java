package be.ulb.stib.graph;

import be.ulb.stib.data.GlobalModel;
import be.ulb.stib.spatial.WalkEdgeGenerator;
import be.ulb.stib.spatial.TransitEdgeGenerator;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


/**
 * Vérifie que MultiModalGraph fusionne correctement
 * les arcs walk et transit.
 * 
 * Données : 
 *   3 stops A-B-C (≈111 m de distance chacun)
 *   1 trip  A→B→C (5 min entre chacun)
 *   rayon 150 m
 */
class MultiModalGraphTest {

    private GlobalModel makeModel() {
        GlobalModel model = new GlobalModel();

        model.latList           = new DoubleArrayList(new double[]{0.000, 0.001, 0.002});
        model.lonList           = new DoubleArrayList(new double[]{0.000, 0.000, 0.000});
        model.stopNamePool      = new ObjectArrayList<>(new String[]{"A","B","C"});
        model.stopNameIdxList   = new IntArrayList(new int[]{0,1,2});
        model.stopIdxByTimeList = new IntArrayList(new int[]{0,1,2});
        model.depSecList        = new IntArrayList(new int[]{0, 300, 600});
        model.tripOfsDense      = new IntArrayList(new int[]{0,3});

        return model;
    }

    @Test
    void fusionWalkAndTransit() {
        GlobalModel model = makeModel();
        WalkEdgeGenerator.build(model, 150);
        TransitEdgeGenerator.build(model);
        MultiModalGraph graph = new MultiModalGraph(model);

        // ================ A ================
        IntArrayList targetTransitA = graph.targets.get(0);
        IntArrayList targetCostA = graph.costs.get(0);

        assertTrue(targetTransitA.contains(1)); // vers B

        // Récupérer les deux coûts (walk ~89 s, transit 300 s)
        int cost1 = targetCostA.get(0);
        int cost2 = targetCostA.get(1);
        int min = Math.min(cost1, cost2);
        int max = Math.max(cost1, cost2);
        assertEquals(89, min);   // marche
        assertEquals(300, max);  // transit

        // ================ B ================
        IntArrayList targetTransitB = graph.targets.get(1);
        IntArrayList targetCostB = graph.costs.get(1);

        assertTrue(targetTransitB.contains(2));         // vers C
        assertTrue(targetTransitB.contains(0));         // vers A
        assertEquals(300, targetCostB.get(2)); // transit B→C
    }
}
