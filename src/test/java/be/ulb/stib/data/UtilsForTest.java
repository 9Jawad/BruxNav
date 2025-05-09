package be.ulb.stib.data;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;


public final class UtilsForTest {

    /* Copie une ressource depuis le classpath vers un répertoire temporaire. */
    public static Path copyToTemp(String resourceName, Path targetDir) throws IOException {
        Path dest = targetDir.resolve(resourceName);
        try (InputStream in = UtilsForTest.class.getResourceAsStream("/" + resourceName)) {
            if (in == null) throw new IOException("Resource not found: " + resourceName);
            Files.copy(in, dest);
        }
        return dest;
    }
}
