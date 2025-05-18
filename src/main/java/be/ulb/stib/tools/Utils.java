package be.ulb.stib.tools;

import be.ulb.stib.data.*;
import be.ulb.stib.parsing.RouteLoader;
import be.ulb.stib.parsing.StopLoader;
import be.ulb.stib.parsing.StopTimesLoader;
import be.ulb.stib.parsing.TripLoader;
import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;


public final class Utils {

    public static int idx(String[] hdr, String key) {
        int i = Arrays.asList(hdr).indexOf(key);
        if (i < 0) throw new IllegalStateException("Column '" + key + "' not found");
        return i;
    }

    public static AgencyModel loadAgency(Path dir) throws Exception {
        AgencyModel m = new AgencyModel();
        StopLoader.load(dir.resolve("stops.csv"), m);
        RouteLoader.load(dir.resolve("routes.csv"), m);
        TripLoader.load(dir.resolve("trips.csv"), m);
        StopTimesLoader.load(dir.resolve("stop_times.csv"), m);
        return m;
    }
}
