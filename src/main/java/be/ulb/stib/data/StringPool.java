package be.ulb.stib.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/* Flyweight : stocke chaque chaîne au plus une fois et renvoie son index. */
public final class StringPool {

    private final ArrayList<String> pool = new ArrayList<>();
    private final Map<String,Integer> map = new HashMap<>();

    /** @return l’index (stable) de s dans le pool. */
    public int intern(String s) {
        return map.computeIfAbsent(s, k -> {
            pool.add(k);
            return pool.size() - 1;
        });
    }

    /* récupération inverse (lecture seule) */
    public String get(int idx) { return pool.get(idx); }

    public int size()          { return pool.size();   }
}
