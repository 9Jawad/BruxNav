package be.ulb.stib.tools;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public final class UtilsForTest {

    /* Copie une ressource depuis le classpath vers un répertoire temporaire. */
    public static Path copyToTemp(String resourcePath, Path tmpDir) throws IOException {
        try (InputStream in = UtilsForTest.class.getResourceAsStream("/" + resourcePath)) {
            if (in == null) throw new IllegalArgumentException("Ressource introuvable: " + resourcePath);
            Path dest = tmpDir.resolve(Paths.get(resourcePath).getFileName());
            Files.copy(in, dest);
            return dest;
        }
    }

}
