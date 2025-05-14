package be.ulb.stib.parsing;

import be.ulb.stib.data.AgencyModel;
import be.ulb.stib.data.GlobalModel;
import be.ulb.stib.spatial.TransitEdgeGenerator;
import be.ulb.stib.spatial.WalkEdgeGenerator;
import java.util.List;
import static be.ulb.stib.tools.Utils.ensureSize;


/* Fusionne toutes les données des agences */
public final class LoaderPipeline {

    public static GlobalModel fuse(List<AgencyModel> agencies) {
        GlobalModel model = new GlobalModel();

        int totalSize = calculateTotalSize(agencies); // 1) Calcul taille totale listes
        initializeSparseArrays(model, totalSize);     // 2) Initialisation des listes avec "-1"
        mergeDatasFromAgencies(model, agencies);      // 3) Fusion des données de chaque agence
        WalkEdgeGenerator.build(model);               // 4) Initialisation index spatial
        TransitEdgeGenerator.build(model);

        return model;
    }

    /* Calcule la taille totale nécessaire pour les listes. */
    private static int calculateTotalSize(List<AgencyModel> agencies) {
        int totalSize = 0;
        for (AgencyModel agency : agencies) {
            totalSize += agency.tripRouteIdxList.size(); // liste la plus grande
        }
        return totalSize - 1; // Ajustement nécessaire
    }

    /* Initialise les tableaux sparse à leur taille finale. */
    private static void initializeSparseArrays(GlobalModel model, int totalSize) {
        ensureSize(model.lonList,           totalSize, -1);
        ensureSize(model.latList,           totalSize, -1);
        ensureSize(model.routeTypeList,     totalSize, (byte)-1);
        ensureSize(model.routeShortIdxList, totalSize, -1);
        ensureSize(model.routeLongIdxList,  totalSize, -1);
        ensureSize(model.tripRouteIdxList,  totalSize, -1);
        ensureSize(model.tripOfsSparse,     totalSize, -1);
        ensureSize(model.stopNameIdxList,   totalSize, -1);
    }

    /* Fusionne les données de chaque agence dans le modèle global. */
    private static void mergeDatasFromAgencies(GlobalModel model, List<AgencyModel> agencies) {
        int offset = 0;
        int globalDenseOffset = 0;
        boolean firstAgency = true;

        for (AgencyModel agency : agencies) {

            mergeStops(model,     agency, offset); // Fusion des stops
            mergeRoutes(model,    agency, offset); // Fusion des routes
            mergeTrips(model,     agency, offset); // Fusion des trips
            mergeStopTimes(model, agency, offset); // Fusion des stop_times
            mergeOffsets(model,   agency, offset,  // Fusion des offsets
                         globalDenseOffset, firstAgency);

            // Mise à jour des offsets
            firstAgency = false;
            offset += agency.tripRouteIdxList.size();
            globalDenseOffset += agency.tripOfsDense.size();
        }
    }

    // -----------------------

    /* Fusionne les stops d'une agence dans le modèle global. */
    private static void mergeStops(GlobalModel model, AgencyModel agency, int offset) {
        // Fusion des pools partagés pour les stops
        for (String stopName : agency.stopNamePool) {
            model.stopName2idx.computeIntIfAbsent(stopName, key -> {
                model.stopNamePool.add(key);
                return model.stopNamePool.size() - 1;
            });
        }

        // Copie des stops
        for (int i = 0; i < agency.latList.size(); i++) {
            model.latList.set(offset + i, agency.latList.getDouble(i));
            model.lonList.set(offset + i, agency.lonList.getDouble(i));

            String stopName = agency.stopNamePool.get(agency.stopNameIdxList.getInt(i));
            model.stopNameIdxList.set(offset + i, model.stopName2idx.getInt(stopName));

            // Lookup global ID → index
            String stopId = agency.idDict.get(i);
            model.idx.put(stopId, offset + i);
        }
    }

    /* Fusionne les routes d'une agence dans le modèle global. */
    private static void mergeRoutes(GlobalModel model, AgencyModel agency, int offset) {
        // Fusion des pools partagés pour les routes
        for (String shortName : agency.routeShortPool) {
            model.routeShort2idx.computeIntIfAbsent(shortName, key -> {
                model.routeShortPool.add(key);
                return model.routeShortPool.size() - 1;
            });
        }
        for (String longName : agency.routeLongPool) {
            model.routeLong2idx.computeIntIfAbsent(longName, key -> {
                model.routeLongPool.add(key);
                return model.routeLongPool.size() - 1;
            });
        }

        // Copie des routes
        for (int i = agency.latList.size(); i < agency.routeTypeList.size(); i++) {
            byte routeType = agency.routeTypeList.getByte(i);
            if (routeType < 0) continue;  // Ignorer les trous (-1)

            model.routeTypeList.set(offset + i, routeType);

            String shortName = agency.routeShortPool.get(agency.routeShortIdxList.getInt(i));
            model.routeShortIdxList.set(offset + i, model.routeShort2idx.getInt(shortName));

            String longName = agency.routeLongPool.get(agency.routeLongIdxList.getInt(i));
            model.routeLongIdxList.set(offset + i, model.routeLong2idx.getInt(longName));

            // Lookup global ID → index
            String routeId = agency.idDict.get(i);
            model.idx.put(routeId, offset + i);
        }
    }

    /* Fusionne les trips d'une agence dans le modèle global. */
    private static void mergeTrips(GlobalModel model, AgencyModel agency, int offset) {
        for (int i = agency.routeTypeList.size(); i < agency.tripRouteIdxList.size(); i++) {
            int localRouteIdx = agency.tripRouteIdxList.getInt(i);

            int globalRouteIdx = model.idx.get(agency.idDict.get(localRouteIdx));
            model.tripRouteIdxList.set(offset + i, globalRouteIdx);

            String tripId = agency.idDict.get(i);
            model.idx.put(tripId, offset + i);
        }
    }

    /* Fusionne les stop_times d'une agence dans le modèle global. */
    private static void mergeStopTimes(GlobalModel model, AgencyModel agency, int offset) {
        for (int i = 0; i < agency.stopIdxByTimeList.size(); i++) {
            int stopIdx = agency.stopIdxByTimeList.getInt(i);
            model.stopIdxByTimeList.add(stopIdx + offset);
            model.depSecList.add(agency.depSecList.getInt(i));
        }
    }

    /* Fusionne les offsets d'une agence dans le modèle global. */
    private static void mergeOffsets(GlobalModel model, AgencyModel agency, int offset,
                                     int globalDenseOffset, boolean firstAgency) {
        // Fusion des offsets dense
        for (int j = 0; j < agency.tripOfsDense.size(); j++) {
            if (!firstAgency && j == 0) continue; // Ne pas dupliquer le 0 initial
            int localDense = agency.tripOfsDense.getInt(j);
            model.tripOfsDense.add(localDense + globalDenseOffset);
        }
        // Fusion des offsets sparse
        for (int j = 0; j < agency.tripOfsSparse.size(); j++) {
            int localSparse = agency.tripOfsSparse.getInt(j);
            if (localSparse < 0) continue;
            model.tripOfsSparse.set(offset + j, localSparse + globalDenseOffset);
        }
    }
}
