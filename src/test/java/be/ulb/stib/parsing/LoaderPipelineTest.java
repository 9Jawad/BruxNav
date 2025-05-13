package be.ulb.stib.parsing;

import static be.ulb.stib.tools.Utils.loadAgency;
import static org.junit.jupiter.api.Assertions.*;
import be.ulb.stib.data.AgencyModel;
import be.ulb.stib.data.GlobalModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


class LoaderPipelineTest {

    @TempDir Path tmp;

    @Test
    void fuseTwoAgenciesWholeLists() throws Exception {
         // FusionVisualizer.display();
        Path root = Paths.get("src/test/resources/forLoaderPipeline");
        AgencyModel m1 = loadAgency(root.resolve("A"));
        AgencyModel m2 = loadAgency(root.resolve("B"));

        // Fusion
        GlobalModel g = LoaderPipeline.fuse(List.of(m1, m2));

        /* ==========  STOPS  ========== */
        // latList         = [0.0, 1.0, -1, -1, 2.0, 3.0, -1, -1]
        // lonList         = [0.0, 1.0, -1, -1, 2.0, 3.0, -1, -1]

        // stopNamePool    = ["Stop1", "Stop2", "Stop3", "Stop4"]
        // stopNameIdxList = [0, 1, -1, -1, 2, 3, -1, -1]

        assertArrayEquals(new double[]{0.0, 1.0, -1, -1, 2.0, 3.0, -1, -1}, g.latList.toDoubleArray(), 1e-9);
        assertArrayEquals(new double[]{0.0, 1.0, -1, -1, 2.0, 3.0, -1, -1}, g.lonList.toDoubleArray(), 1e-9);
        assertEquals(List.of("Stop1", "Stop2" ,"Stop3", "Stop4"),           g.stopNamePool);
        assertArrayEquals(new int[]{0, 1, -1, -1, 2, 3, -1, -1},            g.stopNameIdxList.toIntArray());

        /* ==========  ROUTES  ========== */
        // routeTypeList     = [-1, -1, 3, -1, -1, -1, 3, -1]
        // routeShortPool    = ["10", "20"]
        // routeShortIdxList = [-1, -1, 0, -1, -1, -1, 1, -1]
        // routeLongPool     = ["Line 10", "Line 20"]
        // routeLongIdxList  = [-1, -1, 0, -1, -1, -1, 1, -1]

        assertArrayEquals(new byte[]{-1, -1, 3, -1, -1, -1, 3, -1}, g.routeTypeList.toByteArray());
        assertEquals(List.of("10", "20"),                           g.routeShortPool);
        assertArrayEquals(new int[]{-1, -1, 0, -1, -1, -1, 1, -1},  g.routeShortIdxList.toIntArray());
        assertEquals(List.of("Line 10", "Line 20"),                 g.routeLongPool);
        assertArrayEquals(new int[]{-1, -1, 0, -1, -1, -1, 1, -1},  g.routeLongIdxList.toIntArray());

        /* ==========  TRIPS  ========== */
        // tripRouteIdxList = [-1, -1, -1, 2, -1, -1, -1, 6]

        assertArrayEquals(new int[]{-1, -1, -1, 2, -1, -1, -1, 6},  g.tripRouteIdxList.toIntArray());

        /* ==========  STOP_TIMES  ========== */
        // stopIdxByTimeList = [0, 1, 4, 5]
        // depSecList        = [21600, 21900, 25200, 25800]
        // tripOfsDense      = [0, 2, 4]
        // tripOfsSparse     = [-1, -1, -1, 0, -1, -1, -1, 2]

        assertArrayEquals(new int[]{0, 1, 4, 5},                   g.stopIdxByTimeList.toIntArray());
        assertArrayEquals(new int[]{21600, 21900, 25200, 25800},   g.depSecList.toIntArray());
        assertArrayEquals(new int[]{0, 2, 4},                      g.tripOfsDense.toIntArray());
        assertArrayEquals(new int[]{-1, -1, -1, 0, -1, -1, -1, 2}, g.tripOfsSparse.toIntArray());
    }
}
