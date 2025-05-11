package be.ulb.stib.tools;

import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.ints.IntArrayList;

import java.util.Arrays;


public class Utils {

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
}
