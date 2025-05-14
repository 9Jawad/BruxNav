package be.ulb.stib.spatial;

import be.ulb.stib.data.GlobalModel;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;


class KDTreeTest {

    private GlobalModel makeModel() {
        GlobalModel model = new GlobalModel(); // simplifié ici

        model.latList = new DoubleArrayList(new double[]{
                50.8500, // A - Grand‑Place (référence pour test range)
                50.8510, // B - Bourse              (≈150m)
                50.8600, // C - Madou               (≈1.1km)
                50.9000, // D - Heysel              (≈5.5km)
                50.8495, // E - Place de la Monnaie (≈60m)
                50.8200  // F - ULB Campus          (≈3.5km)
        });
        model.lonList = new DoubleArrayList(new double[]{
                4.3500,  // A
                4.3510,  // B
                4.3600,  // C
                4.4000,  // D
                4.3490,  // E
                4.3000   // F
        });
        // noms d’arrêt
        model.stopNamePool    = new ObjectArrayList<>(new String[]{"A","B","C","D","E","F"});
        model.stopNameIdxList = new IntArrayList(new int[]{0,1,2,3,4,5});

        return model;
    }

    @Test
    void nearestAndRange() {
        GlobalModel model = makeModel();
        KDTree tree = new KDTree(model);

        // nearest: près du stop E
        int n = tree.searchNearest(50.8496, 4.3492);
        assertEquals(4, n);

        // rayon de 1200m autour de A
        int radius = 1200; // mètres
        IntArrayList nb = tree.rangeSearch(50.8500, 4.3500, radius, 0);

        // dans le rayon
        assertTrue(nb.contains(4)); // E
        assertTrue(nb.contains(1)); // B

        // trop loin
        assertFalse(nb.contains(2)); // C
        assertFalse(nb.contains(3)); // D
        assertFalse(nb.contains(5)); // F

        // ne s'inclut pas soit meme
        assertFalse(nb.contains(0)); // A
        assertEquals(2, nb.size());
    }
}
