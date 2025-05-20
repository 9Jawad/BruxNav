package be.ulb.stib.tools;

import be.ulb.stib.data.*;
import be.ulb.stib.parsing.RouteLoader;
import be.ulb.stib.parsing.StopLoader;
import be.ulb.stib.parsing.StopTimesLoader;
import be.ulb.stib.parsing.TripLoader;
import java.nio.file.Path;
import java.util.Arrays;


public final class Utils {
    
    /**
     * Retourne l'indice de la clé spécifiée dans le tableau d'en-têtes donné.
     *
     * @param hdr le tableau des chaînes d'en-tête
     * @param key la clé à rechercher dans le tableau d'en-tête
     * @return l'indice de la clé dans le tableau d'en-tête
     * @throws IllegalStateException si la clé n'est pas trouvée dans le tableau d'en-tête
     */
    public static int idx(String[] hdr, String key) {
        int i = Arrays.asList(hdr).indexOf(key);
        if (i < 0) throw new IllegalStateException("Column '" + key + "' not found");
        return i;
    }


    /**
     * Charge un AgencyModel en lisant et en analysant les fichiers CSV requis depuis le répertoire spécifié.
     * La méthode charge les arrêts, les routes, les trajets et les horaires d'arrêt dans le modèle.
     *
     * @param dir le répertoire contenant les fichiers CSV
     * @return l'AgencyModel chargé
     * @throws Exception si une erreur survient lors du chargement ou de l'analyse des fichiers
     */
    public static AgencyModel loadAgency(Path dir) throws Exception {
        AgencyModel m = new AgencyModel();
        StopLoader.load(dir.resolve("stops.csv"), m);
        RouteLoader.load(dir.resolve("routes.csv"), m);
        TripLoader.load(dir.resolve("trips.csv"), m);
        StopTimesLoader.load(dir.resolve("stop_times.csv"), m);
        return m;
    }
}
