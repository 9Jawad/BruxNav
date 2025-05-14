package be.ulb.stib.spatial;

import be.ulb.stib.data.GlobalModel;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


class WalkEdgeGeneratorTest {

    /* Six arrêts (coord fictifs) autour de Bruxelles. */
    private GlobalModel makeModel() {
        GlobalModel model = new GlobalModel();

        model.latList = new DoubleArrayList(new double[]{
                50.8500, // 0  A – Grand-Place
                50.8510, // 1  B – Bourse   (≈150 m)
                50.8600, // 2  C – Madou    (≈1 100 m)
                50.9000, // 3  D – Heysel   (≈5 500 m)
                50.8495, // 4  E – Monnaie  (≈60 m)
                50.8200  // 5  F – ULB      (≈3 500 m)
        });
        model.lonList         = new DoubleArrayList(new double[]{4.3500, 4.3510, 4.3600, 4.4000, 4.3490, 4.3000});
        model.stopNamePool    = new ObjectArrayList<>(new String[]{"A","B","C","D","E","F"});
        model.stopNameIdxList = new IntArrayList(new int[]{0,1,2,3,4,5});

        return model;
    }

    @Test
    void walkNeighborsWithin1200m() {
        GlobalModel model = makeModel();

        // génère voisins piéton rayon 1200 m
        WalkEdgeGenerator.build(model, 1200);

        IntArrayList nbA = model.walkEdges.get(0);

        assertFalse(nbA.contains(0));            // Le stop A ne doit pas se référencer lui-même
        assertTrue(nbA.contains(1));             // Doit contenir B (1)
        assertTrue(nbA.contains(4));             //            et E (4)
        assertEquals(2, nbA.size());    // Exactement 2 voisins

        // Les coûts correspondent (≈150 m et 60 m)
        IntArrayList costA = model.walkEdgesCost.get(0);
        assertEquals(2, costA.size());
        for (int c : costA) assertTrue(c > 0 && c < 180); // <3 min de marche
    }
}
