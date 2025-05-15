package be.ulb.stib.output;

import be.ulb.stib.data.GlobalModel;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import java.util.ArrayList;
import java.util.List;


/**
 * Construit un texte lisible du chemin pour un itinéraire de transport en commun.
 * Cette classe permet de formater les résultats d'un algorithme de recherche d'itinéraire
 * sous forme de texte compréhensible pour l'utilisateur.
 */
public final class ItineraryFormatter {

    // Constantes
    private static final byte BUS   = 0;
    private static final byte TRAM  = 1;
    private static final byte METRO = 2;
    private static final byte TRAIN = 3;

    /* Convertit un chemin et ses métadonnées en lignes de texte lisibles. */
    public static List<String> format(IntArrayList pathStops, int[] arrivalSec, int[] parentEvent,
                                               GlobalModel model)
    {
        List<String> out = new ArrayList<>();

        for (int i = 1; i < pathStops.size(); i++) {
            int from = pathStops.getInt(i - 1);
            int to   = pathStops.getInt(i);

            int departureTime = arrivalSec[from];
            int arrivalTime = arrivalSec[to];

            int event = parentEvent[to];

            String fromStopName = getStopName(model, from);
            String toStopName = getStopName(model, to);

            if (event < 0) { // Déplacement à pied
                out.add(String.format("Walk from %s (%s) to %s (%s)",
                        fromStopName, formatTime(departureTime),
                        toStopName, formatTime(arrivalTime)));
            } else {         // Transport en commun
                int tripIdx = findTripForDenseEvent(model, event);
                int routeIdx = model.tripRouteIdxList.getInt(tripIdx);
                byte transportType = model.routeTypeList.getByte(routeIdx);
                String transportMode = getTransportModeName(transportType);
                String lineNumber = getRouteShortName(model, routeIdx);
                String agencyName = extractAgencyName(model, from);

                out.add(String.format("Take %s %s %s from %s (%s) to %s (%s)",
                        agencyName, transportMode, lineNumber,
                        fromStopName, formatTime(departureTime),
                        toStopName, formatTime(arrivalTime)));
            }
        }
        return out;
    }

    /* ------------- helpers ------------- */

    /* Récupère le nom d'un arrêt à partir de son index. */
    private static String getStopName(GlobalModel model, int stopIndex) {
        int nameIdx = model.stopNameIdxList.getInt(stopIndex);
        return nameIdx >= 0 ? model.stopNamePool.get(nameIdx) : ("Stop#" + stopIndex);
    }

    /* Récupère le nom court (numéro de ligne) d'une route. */
    private static String getRouteShortName(GlobalModel model, int routeIdx) {
        int shortNameIdx = model.routeShortIdxList.getInt(routeIdx);
        return shortNameIdx >= 0 ? model.routeShortPool.get(shortNameIdx) : ("Route#" + routeIdx);
    }

    /* Trouve l'index global du trip (trajet) contenant l'événement dense identifié par idxDense. */
    private static int findTripForDenseEvent(GlobalModel model, int idxDense) {
        IntArrayList tripOffsetsSparse = model.tripOfsSparse;
        IntArrayList tripOffsetsDense = model.tripOfsDense;

        for (int tripIdx = 0; tripIdx < tripOffsetsSparse.size(); tripIdx++) {
            int offset = tripOffsetsSparse.getInt(tripIdx);
            if (offset < 0) continue;  // Slot de remplissage

            // Position dans le tableau dense:
            int densePos = tripOffsetsDense.indexOf(offset);
            int nextOffset = (densePos + 1 < tripOffsetsDense.size())
                    ? tripOffsetsDense.getInt(densePos + 1)
                    : Integer.MAX_VALUE;

            if (idxDense >= offset && idxDense < nextOffset) {
                return tripIdx;
            }
        }
        return -1;  // Non trouvé (sécurité)
    }

    /* Extrait le nom de l'agence de transport à partir de l'ID d'un arrêt. */
    private static String extractAgencyName(GlobalModel model, int stopIdx) {
        String stopId = model.id.get(stopIdx);
        return stopId.split("-")[0];
    }

    /* Convertit un code de type de transport en nom lisible. */
    private static String getTransportModeName(byte transportType) {
        switch (transportType) {
            case BUS:    return "BUS";
            case TRAM:   return "TRAM";
            case METRO:  return "METRO";
            case TRAIN:  return "TRAIN";
            default:     return "TRANSIT";
        }
    }

    /* Formate un temps en secondes au format "HH:MM". */
    private static String formatTime(int seconds) {
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds%60);
    }
}
