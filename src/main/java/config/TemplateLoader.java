package config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TemplateLoader {

    public static String loadTemplate(String filename){

        try {
            return Files.readString(
                    Paths.get((TemplateLoader.class.getClassLoader()
                            .getResource("template/" + filename).toURI())),
                    StandardCharsets.UTF_8
            );
        }
        catch (IOException | NullPointerException | java.net.URISyntaxException e) {
            throw new RuntimeException("Не удалось загрузить шаблон: " + filename, e);
        }

    }

}
