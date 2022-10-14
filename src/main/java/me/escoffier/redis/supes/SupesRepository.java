package me.escoffier.redis.supes;

import io.vertx.core.json.JsonArray;

import javax.enterprise.context.ApplicationScoped;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

@ApplicationScoped
public class SupesRepository {

    private static final List<Supes> SUPES;

    static {
        try {
            var file = SupesRepository.class.getClassLoader().getResource("import.json");
            var array = new JsonArray(read(file));
            List<Supes> list = new ArrayList<>();
            for (int i = 0; i < array.size(); i++) {
                list.add(array.getJsonObject(i).mapTo(Supes.class));
            }
            SUPES = Collections.unmodifiableList(list);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private final Random random = new Random();

    public Supes getRandomSupes() {
        int rnd = random.nextInt(SUPES.size());
        return SUPES.get(rnd);
    }

    public Supes getByIndex(int index) {
        return SUPES.get(index);
    }

    private static String read(URL url) {
        try (Scanner scanner = new Scanner(url.openStream(),
                StandardCharsets.UTF_8)) {
            scanner.useDelimiter("\\A");
            return scanner.hasNext() ? scanner.next() : "";
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
