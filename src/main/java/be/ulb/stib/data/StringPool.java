package be.ulb.stib.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * StringPool permet de stocker et de récupérer efficacement des chaînes en leur attribuant un indice stable.
 * Si la même chaîne est internée plusieurs fois, le même indice est renvoyé.
 * Le pool permet également la recherche inverse de l'indice vers la chaîne.
 */
public final class StringPool {

    private final ArrayList<String> pool = new ArrayList<>();
    private final Map<String,Integer> map = new HashMap<>();

    /** @return l’index (stable) dans le pool. */
    public int intern(String s) {
        return map.computeIfAbsent(s, k -> {
            pool.add(k);
            return pool.size() - 1;
        });
    }

    /* récupération inverse (lecture seule) */
    public String get(int idx) { return pool.get(idx); }

    public int size() { return pool.size(); }
}
