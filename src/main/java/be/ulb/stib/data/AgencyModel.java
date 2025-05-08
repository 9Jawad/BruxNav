package be.ulb.stib.data;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;


/* Conteneur des données d’une seule agence GTFS. */
public final class AgencyModel {

    // dictionnaire GTFS
    public final IntDictionary idDict = new IntDictionary();

    // ---------- stops dynamiques ----------
    public final DoubleArrayList latList     = new DoubleArrayList();
    public final DoubleArrayList lonList     = new DoubleArrayList();
    public final IntArrayList    nameIdxList = new IntArrayList();

    // pool de noms d’arrêt + map inverse pour la dé‑duplication
    public final ObjectArrayList<String>       names   = new ObjectArrayList<>();
    public final Object2IntOpenHashMap<String> name2idx = new Object2IntOpenHashMap<>();
    // --------------------------------------

    /* Verouille le dictionnaire. */
    public void freeze() { idDict.freeze(); }

    /* Renvoie la taille du dictionnaire */
    public int stopCount() { return idDict.size(); }
}
