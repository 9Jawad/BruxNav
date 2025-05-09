package be.ulb.stib.data;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.bytes.ByteArrayList;


/* Conteneur des données d’une seule agence GTFS. */
public final class AgencyModel {

    // dictionnaire GTFS (dense)
    public final IDictionary idDict = new IDictionary();

    /* =========================  STOPS  ========================= */
    public final DoubleArrayList               latList         = new DoubleArrayList();
    public final DoubleArrayList               lonList         = new DoubleArrayList();
    public final IntArrayList                  stopNameIdxList = new IntArrayList();
    // pool de noms d’arrêt
    public final ObjectArrayList<String>       stopNamePool    = new ObjectArrayList<>();
    public final Object2IntOpenHashMap<String> stopName2idx    = new Object2IntOpenHashMap<>();

    /* =========================  ROUTES  ========================= */
    public final ByteArrayList                 routeTypeList     = new ByteArrayList();
    // index → short name (pool)
    public final IntArrayList                  routeShortIdxList = new IntArrayList();
    public final ObjectArrayList<String>       routeShortPool    = new ObjectArrayList<>();
    public final Object2IntOpenHashMap<String> routeShort2idx    = new Object2IntOpenHashMap<>();
    // index → long name (pool)
    public final IntArrayList                  routeLongIdxList  = new IntArrayList();
    public final ObjectArrayList<String>       routeLongPool     = new ObjectArrayList<>();
    public final Object2IntOpenHashMap<String> routeLong2idx     = new Object2IntOpenHashMap<>();

    /* =========================  TRIPS  ========================= */
    public final IntArrayList                  tripRouteIdxList  = new IntArrayList();

    /* =========================  INFO  ========================= */
    // Renvoie la taille d'une liste
    public int stopCount()  { return latList.size(); }
    public int routeCount() { return routeTypeList.size(); }
    public int tripCount()  { return tripRouteIdxList.size(); }

    /* Verouille le dictionnaire. */
    public void freeze() { idDict.freeze(); }
}
