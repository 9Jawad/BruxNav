package be.ulb.stib.spatial;

import be.ulb.stib.data.GlobalModel;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


class TransitEdgeGeneratorTest {

    /* Fabrique un GlobalModel avec un seul trip: A→B→C, heures: 8h, 8h05, 8h08. */
    private GlobalModel makeModel() {
        GlobalModel model = new GlobalModel();
        model.latList           = new DoubleArrayList(new double[]{0,0,0});
        model.lonList           = new DoubleArrayList(new double[]{0,0,0});
        model.stopNameIdxList   = new IntArrayList(new int[]{0,1,2});

        model.stopIdxByTimeList = new IntArrayList(new int[]{0,1,2});
        model.depSecList        = new IntArrayList(new int[]{8*3600, 8*3600+300, 8*3600+480});
        model.tripOfsDense      = new IntArrayList(new int[]{0,3});

        return model;
    }

    @Test
    void transitEdgesOk() {
        GlobalModel model = makeModel();
        TransitEdgeGenerator.build(model);

        IntArrayList fromA = model.transitEdges.get(0);
        IntArrayList costA = model.transitEdgesCost.get(0);
        assertEquals(1, fromA.size());
        assertEquals(1, fromA.getInt(0));      // A→B
        assertEquals(300, costA.getInt(0));    // 5 min

        IntArrayList fromB = model.transitEdges.get(1);
        IntArrayList costB = model.transitEdgesCost.get(1);
        assertEquals(1, fromB.size());
        assertEquals(2, fromB.getInt(0));      // B→C
        assertEquals(180, costB.getInt(0));    // 3 min
    }
}
