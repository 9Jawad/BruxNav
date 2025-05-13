package be.ulb.stib.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/*
 * Mapping bidirectionnel <String id ⇄ int idx> utilisé pour compresser les identifiants GTFS.
 * Méthode "freeze" pour verrouiller le dictionnaire.
 */
public final class IDictionary implements Serializable {

    private final Map<String, Integer> s2i = new HashMap<>(32_768);
    private final List<String> i2s = new ArrayList<>(32_768);
    private boolean frozen = false;

    /*
     * Retourne l'index du string ; l'ajoute s'il est absent et non gelé.
     * Provoque une exception si le dictionnaire est gelé et que l'identifiant est nouveau.
     */
    public int getOrAdd(String id) {
        int existing = get(id);
        if (existing != -1) return existing;
        if (frozen) throw new IllegalStateException("IDictionary is frozen, cannot add id=" + id);
        int newIdx = size();
        i2s.add(id);
        s2i.put(id, newIdx);
        return newIdx;
    }

    /* Renvoie l'index du string ou -1 s'il est absent (n'ajoute PAS). */
    public int get(String id) {
        Integer idx = s2i.get(id);
        return idx == null ? -1 : idx;
    }

    /* Renvoie le string de l'ID. */
    public String get(int idx) {
        return i2s.get(idx);
    }

    /* Renvoie le nombre d'identifiants stockés. */
    public int size() {
        return i2s.size();
    }

    /* Verrouille le dictionnaire. */
    public void freeze() {
        frozen = true;
    }

    /* Renvoie true si le dictionnaire est verrouillé sinon false. */
    public boolean isFrozen() {
        return frozen;
    }
}
