package be.ulb.stib;

import be.ulb.stib.data.*;
import be.ulb.stib.graph.MultiModalGraph;
import be.ulb.stib.parsing.LoaderPipeline;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import static be.ulb.stib.tools.Utils.loadAgency;


public class Main {

    static double parsingSeconds;
    static double fusionSeconds;
    static double graphSeconds;

    // Constantes pour la colorisation de l'output
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";

    public static void main(String[] args) throws Exception {

        Path rootDirectory = validateAndGetRootDirectory(args);        // Validation des arguments
        List<AgencyModel> agencies = loadAgencyData(rootDirectory);    // Chargement des données des agences
        displayLoadingStatistics(agencies);                            // Affichage des statistiques de chargement
        GlobalModel model = fuseAgencyData(agencies);                  // Fusion des données dans un modèle global
        graphCreation(model);
    }

    /* Valide les arguments et retourne le répertoire racine. */
    private static Path validateAndGetRootDirectory(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java -jar stibpath-1.0-SNAPSHOT.jar <gtfs-root>");
            System.exit(1);
        }
        Path rootDirectory = Path.of(args[0]);
        if (!Files.isDirectory(rootDirectory)) {
            System.err.println("Not a directory: " + rootDirectory);
            System.exit(1);
        }
        return rootDirectory;
    }

    /* Charge les données de toutes les agences présentes dans le répertoire racine. */
    private static List<AgencyModel> loadAgencyData(Path rootDirectory) throws Exception {
        System.out.println("\n===========================");
        List<AgencyModel> agencies = new ArrayList<>();

        // Parcourir chaque sous-dossier (une agence)
        List<Path> agencyDirectories = Files.list(rootDirectory).filter(Files::isDirectory).toList();

        long startTime = System.nanoTime();

        for (Path agencyDirectory : agencyDirectories) {
            System.out.println("Parsing agency: " + ANSI_YELLOW + agencyDirectory.getFileName() + ANSI_RESET);

            AgencyModel agency = loadAgency(agencyDirectory);

            // Affichage des statistiques par agence
            System.out.printf("  %d stops, %d routes, %d trips\n", agency.stopCount(), agency.routeCount(),
                                                                   agency.tripCount());
            agencies.add(agency);
        }

        long endTime = System.nanoTime();
        parsingSeconds = (endTime - startTime) / 1e9;
        System.out.println("===========================\n");
        System.out.printf("Parsing, completed in " + ANSI_GREEN + "%.2f s\n" + ANSI_RESET, parsingSeconds);

        return agencies;
    }

    /* Affiche les statistiques globales du chargement des données. */
    private static void displayLoadingStatistics(List<AgencyModel> agencies) {
        int totalStops = agencies.stream().mapToInt(AgencyModel::stopCount).sum();
        int totalRoutes = agencies.stream().mapToInt(AgencyModel::routeCount).sum();
        int totalTrips = agencies.stream().mapToInt(AgencyModel::tripCount).sum();

        System.out.printf("%d stops, %d routes, %d trips across %d agencies\n\n",
                totalStops, totalRoutes, totalTrips, agencies.size());
    }

    /* Fusionne les données des agences dans un modèle global. */
    private static GlobalModel fuseAgencyData(List<AgencyModel> agencies) {
        long startTime = System.nanoTime();
        GlobalModel model = LoaderPipeline.fuse(agencies);
        long endTime = System.nanoTime();
        fusionSeconds = (endTime - startTime) / 1e9;
        System.out.printf("Fusion + Spatial Index, completed in " + ANSI_GREEN + "%.2f s\n\n" + ANSI_RESET, fusionSeconds);
        return model;
    }

    /* Création d'un graphe multi modal à partir des arcs de marche et de transit. */
    private static void graphCreation(GlobalModel model) {
        long startTime = System.nanoTime();
        MultiModalGraph graph = new MultiModalGraph(model);
        long endTime = System.nanoTime();
        graphSeconds = (endTime - startTime) / 1e9;
        System.out.printf("Graph creation, completed in " + ANSI_GREEN + "%.2f s\n\n" + ANSI_RESET, graphSeconds);
        System.out.printf("TOTAL TIME : " + ANSI_GREEN + "%.2f s\n\n" + ANSI_RESET, fusionSeconds + parsingSeconds + graphSeconds);
    }
}
