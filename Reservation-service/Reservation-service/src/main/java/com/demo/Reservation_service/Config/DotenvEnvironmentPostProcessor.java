package com.demo.Reservation_service.Config;

import org.springframework.boot.EnvironmentPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DotenvEnvironmentPostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Map<String, Object> values = new LinkedHashMap<>();
        for (Path file : envFiles()) {
            values.putAll(parse(file));
        }
        if (values.isEmpty()) {
            return;
        }
        MapPropertySource source = new MapPropertySource("dotenv", values);
        MutablePropertySources sources = environment.getPropertySources();
        if (sources.contains("systemEnvironment")) {
            sources.addAfter("systemEnvironment", source);
        } else {
            sources.addFirst(source);
        }
    }

    static List<Path> envFiles() {
        List<Path> found = new ArrayList<>();
        Path dir = Path.of(System.getProperty("user.dir", ".")).toAbsolutePath();
        while (dir != null) {
            Path candidate = dir.resolve(".env");
            if (Files.isRegularFile(candidate)) {
                found.add(candidate);
            }
            dir = dir.getParent();
        }
        Collections.reverse(found);
        return found;
    }

    static Map<String, Object> parse(Path file) {
        Map<String, Object> values = new LinkedHashMap<>();
        List<String> lines;
        try {
            lines = Files.readAllLines(file);
        } catch (IOException ex) {
            return values;
        }
        for (String raw : lines) {
            String line = raw.trim();
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }
            if (line.startsWith("export ")) {
                line = line.substring("export ".length()).trim();
            }
            int split = line.indexOf('=');
            if (split < 1) {
                continue;
            }
            String key = line.substring(0, split).trim();
            String value = stripQuotes(line.substring(split + 1).trim());
            if (!key.isEmpty() && !value.isEmpty()) {
                values.put(key, value);
            }
        }
        return values;
    }

    private static String stripQuotes(String value) {
        if (value.length() >= 2
                && ((value.startsWith("\"") && value.endsWith("\""))
                || (value.startsWith("'") && value.endsWith("'")))) {
            return value.substring(1, value.length() - 1);
        }
        return value;
    }
}
