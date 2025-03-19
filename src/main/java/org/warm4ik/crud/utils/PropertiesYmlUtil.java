package org.warm4ik.crud.utils;

import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.util.Map;

public final class PropertiesYmlUtil {
    private static final Yaml YAML = new Yaml();
    private static Map<String, Object> properties;

    static {
        loadProperties();
    }

    private static void loadProperties() {
        try (var inputStream = PropertiesYmlUtil.class.getClassLoader().getResourceAsStream("application.yml")) {
            properties = YAML.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String get(String key) {
        String[] keys = key.split("\\.");
        Map<String, Object> current = properties;
        for (int i = 0; i < keys.length - 1; i++) {
            current = (Map<String, Object>) current.get(keys[i]);
        }
        return String.valueOf(current.get(keys[keys.length - 1]));
    }

    public static String getPoolSize(String key){
        return get(key);
    }

    private PropertiesYmlUtil() {
    }
}
