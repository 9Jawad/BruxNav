package be.ulb.stib.parsing;

import be.ulb.stib.data.AgencyModel;
import be.ulb.stib.data.GlobalModel;
import java.util.List;
import static be.ulb.stib.tools.Utils.ensureSize;


/* Fusionne toutes les données des agences */
public final class LoaderPipeline {

    public static GlobalModel fuse(List<AgencyModel> agencies) {
        GlobalModel globalModel = new GlobalModel();

        int totalSize = calculateTotalSize(agencies);     // 1) Calcul taille totale listes
        initializeSparseArrays(globalModel, totalSize);   // 2) Initialisation des listes avec "-1"
        mergeDatasFromAgencies(globalModel, agencies);    // 3) Fusion des données de chaque agence
        globalModel.initSpatial(globalModel.stopCount()); // 4) Initialisation des listes d'index spatial

        return globalModel;
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
    private static void initializeSparseArrays(GlobalModel globalModel, int totalSize) {
        ensureSize(globalModel.lonList,           totalSize, -1);
        ensureSize(globalModel.latList,           totalSize, -1);
        ensureSize(globalModel.routeTypeList,     totalSize, (byte)-1);
        ensureSize(globalModel.routeShortIdxList, totalSize, -1);
        ensureSize(globalModel.routeLongIdxList,  totalSize, -1);
        ensureSize(globalModel.tripRouteIdxList,  totalSize, -1);
        ensureSize(globalModel.tripOfsSparse,     totalSize, -1);
        ensureSize(globalModel.stopNameIdxList,   totalSize, -1);
    }

    /* Fusionne les données de chaque agence dans le modèle global. */
    private static void mergeDatasFromAgencies(GlobalModel globalModel, List<AgencyModel> agencies) {
        int offset = 0;
        int globalDenseOffset = 0;
        boolean firstAgency = true;

        for (AgencyModel agency : agencies) {

            mergeStops(globalModel,     agency, offset); // Fusion des stops
            mergeRoutes(globalModel,    agency, offset); // Fusion des routes
            mergeTrips(globalModel,     agency, offset); // Fusion des trips
            mergeStopTimes(globalModel, agency, offset); // Fusion des stop_times
            mergeOffsets(globalModel,   agency, offset,  // Fusion des offsets
                         globalDenseOffset, firstAgency);

            // Mise à jour des offsets
            firstAgency = false;
            offset += agency.tripRouteIdxList.size();
            globalDenseOffset += agency.tripOfsDense.size();
        }
    }

    /* Fusionne les stops d'une agence dans le modèle global. */
    private static void mergeStops(GlobalModel globalModel, AgencyModel agency, int offset) {
        // Fusion des pools partagés pour les stops
        for (String stopName : agency.stopNamePool) {
            globalModel.stopName2idx.computeIntIfAbsent(stopName, key -> {
                globalModel.stopNamePool.add(key);
                return globalModel.stopNamePool.size() - 1;
            });
        }

        // Copie des stops
        for (int i = 0; i < agency.latList.size(); i++) {
            globalModel.latList.set(offset + i, agency.latList.getDouble(i));
            globalModel.lonList.set(offset + i, agency.lonList.getDouble(i));

            String stopName = agency.stopNamePool.get(agency.stopNameIdxList.getInt(i));
            globalModel.stopNameIdxList.set(offset + i, globalModel.stopName2idx.getInt(stopName));

            // Lookup global ID → index
            String stopId = agency.idDict.get(i);
            globalModel.idx.put(stopId, offset + i);
        }
    }

    /* Fusionne les routes d'une agence dans le modèle global. */
    private static void mergeRoutes(GlobalModel globalModel, AgencyModel agency, int offset) {
        // Fusion des pools partagés pour les routes
        for (String shortName : agency.routeShortPool) {
            globalModel.routeShort2idx.computeIntIfAbsent(shortName, key -> {
                globalModel.routeShortPool.add(key);
                return globalModel.routeShortPool.size() - 1;
            });
        }
        for (String longName : agency.routeLongPool) {
            globalModel.routeLong2idx.computeIntIfAbsent(longName, key -> {
                globalModel.routeLongPool.add(key);
                return globalModel.routeLongPool.size() - 1;
            });
        }

        // Copie des routes
        for (int i = agency.latList.size(); i < agency.routeTypeList.size(); i++) {
            byte routeType = agency.routeTypeList.getByte(i);
            if (routeType < 0) continue;  // Ignorer les trous (-1)

            globalModel.routeTypeList.set(offset + i, routeType);

            String shortName = agency.routeShortPool.get(agency.routeShortIdxList.getInt(i));
            globalModel.routeShortIdxList.set(offset + i, globalModel.routeShort2idx.getInt(shortName));

            String longName = agency.routeLongPool.get(agency.routeLongIdxList.getInt(i));
            globalModel.routeLongIdxList.set(offset + i, globalModel.routeLong2idx.getInt(longName));

            // Lookup global ID → index
            String routeId = agency.idDict.get(i);
            globalModel.idx.put(routeId, offset + i);
        }
    }

    /* Fusionne les trips d'une agence dans le modèle global. */
    private static void mergeTrips(GlobalModel globalModel, AgencyModel agency, int offset) {
        for (int i = agency.routeTypeList.size(); i < agency.tripRouteIdxList.size(); i++) {
            int localRouteIdx = agency.tripRouteIdxList.getInt(i);

            int globalRouteIdx = globalModel.idx.get(agency.idDict.get(localRouteIdx));
            globalModel.tripRouteIdxList.set(offset + i, globalRouteIdx);

            String tripId = agency.idDict.get(i);
            globalModel.idx.put(tripId, offset + i);
        }
    }

    /* Fusionne les stop_times d'une agence dans le modèle global. */
    private static void mergeStopTimes(GlobalModel globalModel, AgencyModel agency, int offset) {
        for (int i = 0; i < agency.stopIdxByTimeList.size(); i++) {
            int stopIdx = agency.stopIdxByTimeList.getInt(i);
            globalModel.stopIdxByTimeList.add(stopIdx + offset);
            globalModel.depSecList.add(agency.depSecList.getInt(i));
        }
    }

    /* Fusionne les offsets d'une agence dans le modèle global. */
    private static void mergeOffsets(GlobalModel globalModel, AgencyModel agency, int offset,
                                     int globalDenseOffset, boolean firstAgency) {
        // Fusion des offsets dense
        for (int j = 0; j < agency.tripOfsDense.size(); j++) {
            if (!firstAgency && j == 0) continue; // Ne pas dupliquer le 0 initial
            int localDense = agency.tripOfsDense.getInt(j);
            globalModel.tripOfsDense.add(localDense + globalDenseOffset);
        }
        // Fusion des offsets sparse
        for (int j = 0; j < agency.tripOfsSparse.size(); j++) {
            int localSparse = agency.tripOfsSparse.getInt(j);
            if (localSparse < 0) continue;
            globalModel.tripOfsSparse.set(offset + j, localSparse + globalDenseOffset);
        }
    }
}
