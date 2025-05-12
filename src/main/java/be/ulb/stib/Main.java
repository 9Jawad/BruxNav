package be.ulb.stib;

import be.ulb.stib.data.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;


public class Main {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("Usage: java -jar target stibpath-1.0-SNAPSHOT.jar <gtfs-root>");
            System.exit(1);
        }
        Path root = Path.of(args[0]);
        if (!Files.isDirectory(root)) {
            System.err.println("Not a directory: " + root);
            System.exit(1);
        }

        // PARSING
        System.out.println("\n===========================");
        List<AgencyModel> agencies = new ArrayList<>();
        long t0 = System.nanoTime();

        // parcourir chaque sous-dossier (une agence)
        for (Path agencyDir : Files.list(root).filter(Files::isDirectory).toList()) {
            System.out.println("Parsing agency: " + ANSI_YELLOW + agencyDir.getFileName() + ANSI_RESET);

            Path stops  = agencyDir.resolve("stops.csv");
            Path routes = agencyDir.resolve("routes.csv");
            Path trips  = agencyDir.resolve("trips.csv");
            Path times  = agencyDir.resolve("stop_times.csv");

            AgencyModel agency = new AgencyModel();
            StopLoader.load(stops, agency);
            RouteLoader.load(routes, agency);
            TripLoader.load(trips, agency);
            StopTimesLoader.load(times, agency);
            agency.freeze();

            System.out.printf("  %d stops, %d routes, %d trips\n",
                              agency.stopCount(), agency.routeCount(), agency.tripCount());
            agencies.add(agency);
        }
        long t1 = System.nanoTime();
        double secs = (t1 - t0) / 1e9;
        System.out.println("===========================\n");

        int totalStops = agencies.stream().mapToInt(AgencyModel::stopCount).sum();
        int totalRoutes = agencies.stream().mapToInt(AgencyModel::routeCount).sum();
        int totalTrips = agencies.stream().mapToInt(AgencyModel::tripCount).sum();

        System.out.printf("Parsing completed in " + ANSI_GREEN + "%.2f s\n" + ANSI_RESET, secs);
        System.out.printf("%d stops, %d routes, %d trips across %d agencies\n\n",
                totalStops, totalRoutes, totalTrips, agencies.size());

        // FUSION
        GlobalModel model = LoaderPipeline.fuse(agencies);
        long t2 = System.nanoTime();
        secs = (t2 - t1) / 1e9;
        System.out.printf("Fusion completed in " + ANSI_GREEN + "%.2f s\n\n" + ANSI_RESET, secs);
        secs = (t2 - t0) / 1e9;
        System.out.printf("TOTAL TIME : " + ANSI_GREEN + "%.2f s\n\n" + ANSI_RESET, secs);
    }
}
