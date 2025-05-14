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

    public static void ensureSize(IntArrayList l, int len, int pad) {
        while (l.size() <= len) l.add(pad);
    }

    public static void ensureSize(DoubleArrayList l, int len, double pad) {
        while (l.size() <= len) l.add(pad);
    }

    public static void ensureSize(ByteArrayList l, int len, byte pad) {
        while (l.size() <= len) l.add(pad);
    }

    public static void ensureSize(ObjectArrayList l, int len, int pad) {
        while (l.size() <= len) l.add(pad);
    }

    public static void ensureSize(ObjectArrayList l, int len, Integer pad) {
        while (l.size() <= len) l.add(pad);
    }

    public static AgencyModel loadAgency(Path dir) throws Exception {
        AgencyModel m = new AgencyModel();
        StopLoader.load(dir.resolve("stops.csv"), m);
        RouteLoader.load(dir.resolve("routes.csv"), m);
        TripLoader.load(dir.resolve("trips.csv"), m);
        StopTimesLoader.load(dir.resolve("stop_times.csv"), m);
        m.freeze();
        return m;
    }

    /* Copie une ressource depuis le classpath vers un répertoire temporaire. */
    public static Path copyToTemp(String resourcePath, Path tmpDir) throws IOException {
        try (InputStream in = Utils.class.getResourceAsStream("/" + resourcePath)) {
            if (in == null) throw new IllegalArgumentException("Ressource introuvable: " + resourcePath);
            Path dest = tmpDir.resolve(Paths.get(resourcePath).getFileName());
            Files.copy(in, dest);
            return dest;
        }
    }

    public static <T> ObjectArrayList<T> subList(ObjectArrayList<T> list, int fromIndex, int toIndex) {
        ObjectArrayList<T> result = new ObjectArrayList<>(toIndex - fromIndex);
        for (int i = fromIndex; i < toIndex; i++) {
            result.add(list.get(i));
        }
        return result;
    }
}
