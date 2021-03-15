package api.Helper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class UtilsNew {
   public String generateStringFromResource(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)));
    }
}
