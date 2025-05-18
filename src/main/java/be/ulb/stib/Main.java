package be.ulb.stib;

import be.ulb.stib.algo.AStarTD;
import be.ulb.stib.core.*;
import be.ulb.stib.graph.MultiModalGraph;
import be.ulb.stib.output.ItineraryFormatter;
import be.ulb.stib.parsing.*;
import be.ulb.stib.data.*;
import be.ulb.stib.spatial.TransitEdgeGenerator;
import be.ulb.stib.spatial.WalkEdgeGenerator;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import static be.ulb.stib.parsing.StopTimesLoader.toSec;
import static be.ulb.stib.tools.Utils.loadAgency;


public final class Main {

    static double parsingSeconds = 0;
    static double fusionSeconds  = 0;
    static double astarSeconds   = 0;
    static double graphSeconds   = 0;

    // Constantes pour la colorisation de l'output
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";


    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("Usage: java -jar stibpath-1.0-SNAPSHOT.jar <gtfs-root>");
            System.err.println("       java -jar stibpath-1.0-SNAPSHOT.jar <gtfs-root> \"<srcName>\" \"<dstName>\" <HH:mm:ss>");
            System.exit(1);
        }
        Path root = Paths.get(args[0]);
        if (!Files.isDirectory(root)) {
            System.err.println("Not a directory: " + root);
            System.exit(1);
        }


        /* ================================= LOADING DES AGENCES ===================================== */
        List<AgencyModel> agencies = new ArrayList<>();
        List<Path> agencyDirectories = Files.list(root).filter(Files::isDirectory).toList();

        System.out.println("\n===========================");
        long startTime = System.nanoTime();

        for (Path agencyDir : agencyDirectories) {
            System.out.println("Loading agency: " + ANSI_YELLOW + agencyDir.getFileName() + ANSI_RESET);

            AgencyModel a = loadAgency(agencyDir);

            System.out.printf("  %d stops, %d routes, %d trips%n",
                    a.stops.size(), a.routes.size(), a.trips.size());

            agencies.add(a);
        }
        long endTime = System.nanoTime();
        parsingSeconds = (endTime - startTime) / 1e9;
        System.out.println("===========================\n");
        System.out.printf("Parsing, completed in " + ANSI_GREEN + "%.2f s\n\n" + ANSI_RESET, parsingSeconds);


        /* ================================= FUSION DES AGENCES ===================================== */
        startTime = System.nanoTime();

        GlobalModel model = new GlobalModel();
        agencies.forEach(model::addAgency);

        endTime = System.nanoTime();
        fusionSeconds = (endTime - startTime) / 1e9;

        System.out.printf("Fusion, completed in " + ANSI_GREEN + "%.2f s\n" + ANSI_RESET, fusionSeconds);
        System.out.printf("Total: %d stops, %d routes, %d trips%n\n",
                model.stops.size(), model.routes.size(), model.trips.size());


        /* ================================= GRAPHE MULTIMODAL ===================================== */
        startTime = System.nanoTime();

        List<WalkEdge> walkEdges = WalkEdgeGenerator.generate(model);
        List<TransitEdge> transitEdges = TransitEdgeGenerator.generate(model);
        MultiModalGraph graph = new MultiModalGraph(model, walkEdges, transitEdges);

        endTime = System.nanoTime();
        graphSeconds = (endTime - startTime) / 1e9;

        System.out.printf("Graph, completed in " + ANSI_GREEN + "%.2f s\n" + ANSI_RESET, graphSeconds);
        System.out.printf("Total:  %d walk edges, %d transit edges%n\n",
                walkEdges.size(), transitEdges.size());


        /* ================================= A* TIME-DEPENDENT ===================================== */
        startTime = System.nanoTime();

        if (args.length >= 4) {
            String srcName = args[1];
            String dstName = args[2];
            String hhmmss = args[3];

            System.out.printf("%nSearching path: %s ->  %s  at %s%n",
                               srcName, dstName, hhmmss);

            AStarTD astar = new AStarTD(graph, model);
            List<Edge> path = astar.search(srcName, dstName, hhmmss);

            if (path.isEmpty())
                System.out.println("No path found.");
            else {
                endTime = System.nanoTime();
                astarSeconds = (endTime - startTime) / 1e9;

                ItineraryFormatter.print(path, toSec(hhmmss), model);
                System.out.printf("PathFind, completed in " + ANSI_GREEN + "%.2f s\n\n" + ANSI_RESET, astarSeconds);

            }
        }
        System.out.printf("TOTAL TIME : " + ANSI_GREEN + "%.2f s\n\n" + ANSI_RESET, fusionSeconds + parsingSeconds + astarSeconds + graphSeconds);
    }
}
