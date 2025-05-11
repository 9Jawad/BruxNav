package be.ulb.stib;

import be.ulb.stib.data.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;


public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("Usage: java -jar \"target stibpath-1.0-SNAPSHOT.jar\" \"<gtfs-root>\"");
            System.exit(1);
        }
        Path root = Path.of(args[0]);
        if (!Files.isDirectory(root)) {
            System.err.println("Not a directory: " + root);
            System.exit(1);
        }

        List<AgencyModel> agencies = new ArrayList<>();
        long t0 = System.nanoTime();

        // parcourir chaque sous-dossier (une agence)
        for (Path agencyDir : Files.list(root).filter(Files::isDirectory).toList()) {
            System.out.println("Parsing agency: " + agencyDir.getFileName());
            AgencyModel a = new AgencyModel();

            Path stops  = agencyDir.resolve("stops.csv");
            Path routes = agencyDir.resolve("routes.csv");
            Path trips  = agencyDir.resolve("trips.csv");
            Path times  = agencyDir.resolve("stop_times.csv");

            StopLoader.load(stops, a);
            RouteLoader.load(routes, a);
            TripLoader.load(trips, a);
            StopTimesLoader.load(times, a);
            a.freeze();

            System.out.printf("  %d stops, %d routes, %d trips\n",
                    a.stopCount(), a.routeCount(), a.tripCount());
            agencies.add(a);
        }

        long t1 = System.nanoTime();
        double secs = (t1 - t0) / 1e9;
        int totalStops = agencies.stream().mapToInt(AgencyModel::stopCount).sum();
        int totalRoutes = agencies.stream().mapToInt(AgencyModel::routeCount).sum();
        int totalTrips = agencies.stream().mapToInt(AgencyModel::tripCount).sum();

        System.out.printf("\n=== Parsing completed in %.2f s ===\n", secs);
        System.out.printf("Total: %d stops, %d routes, %d trips across %d agencies\n",
                totalStops, totalRoutes, totalTrips, agencies.size());
    }
}
