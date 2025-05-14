package be.ulb.stib.data;

import be.ulb.stib.parsing.LoaderPipeline;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import static be.ulb.stib.tools.Utils.loadAgency;


/* Affiche la fusion entre deux agences (Only Debugging Method) */
public final class FusionVisualizer {

    public static void display() throws Exception {

        Path root = Paths.get("src/test/resources/forLoaderPipeline");
        AgencyModel m1 = loadAgency(root.resolve("A"));
        AgencyModel m2 = loadAgency(root.resolve("B"));

        System.out.println("\n=== AGENCY A ===");
        printAgency(m1);

        System.out.println("\n=== AGENCY B ===");
        printAgency(m2);

        System.out.println("\n=== GLOBAL MODEL ===");
        GlobalModel g = LoaderPipeline.fuse(List.of(m1, m2));
        printAgency(g);
    }

    private static void printAgency(AgencyModel m) {
        System.out.println("stops:");
        System.out.println("  latList           = " + Arrays.toString(m.latList.toDoubleArray()));
        System.out.println("  lonList           = " + Arrays.toString(m.lonList.toDoubleArray()));
        System.out.println("  stopNamePool      = " + m.stopNamePool);
        System.out.println("  stopNameIdxList   = " + Arrays.toString(m.stopNameIdxList.toIntArray()));

        System.out.println("routes:");
        System.out.println("  routeTypeList     = " + Arrays.toString(m.routeTypeList.toByteArray()));
        System.out.println("  routeShortPool    = " + m.routeShortPool);
        System.out.println("  routeShortIdxList = " + Arrays.toString(m.routeShortIdxList.toIntArray()));
        System.out.println("  routeLongPool     = " + m.routeLongPool);
        System.out.println("  routeLongIdxList  = " + Arrays.toString(m.routeLongIdxList.toIntArray()));

        System.out.println("trips:");
        System.out.println("  tripRouteIdxList  = " + Arrays.toString(m.tripRouteIdxList.toIntArray()));

        System.out.println("stop_times (dense):");
        System.out.println("  stopIdxByTimeList = " + Arrays.toString(m.stopIdxByTimeList.toIntArray()));
        System.out.println("  depSecList        = " + Arrays.toString(m.depSecList.toIntArray()));
        System.out.println("  tripOfsDense      = " + Arrays.toString(m.tripOfsDense.toIntArray()));

        System.out.println("stop_times (sparse):");
        System.out.println("  tripOfsSparse   = "   + Arrays.toString(m.tripOfsSparse.toIntArray()));
    }
}
