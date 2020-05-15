package org.baito.API.registry;

import org.baito.API.config.FolderConfig;
import org.json.JSONException;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class SerializableRegistry<K, V extends SerializableRegistryEntry<K>> {

    // Folder for where all the files are
    private FolderConfig config;
    // Registry itself
    private HashMap<K, V> registry = new HashMap<>();
    // Default action for when no registered value was found
    private DefaultAction<K, V> defaultCreator;
    // Instancer for creating new objects of type V on load
    private Instancer<V> instancer;

    // Constructor
    public SerializableRegistry(String location, Instancer<V> inst, DefaultAction<K, V> k) {
        config = new FolderConfig(location.toUpperCase());
        this.instancer = inst;
        this.defaultCreator = k;
    }

    // Register an item
    public void register(boolean override, V object) {
        if (!registry.containsKey(object.getKey()) || override) {
            registry.put(object.getKey(), object);
        }
    }

    // Get an item, or create a new one using the DefaultAction
    public V get(K key) {
        V value;
        if (registry.containsKey(key)) {
            value = registry.get(key);
        } else {
            value = defaultCreator.def(key);
            registry.put(key, value);
        }
        return value;
    }

    // Save all items to JSON
    public void save() {
        for (Map.Entry<K, V> entry : registry.entrySet()) {
            try {
                File f = config.getFile(entry.getValue().fileName());
                config.save(f, entry.getValue().toJson());
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    // Load all items from JSON, using the Instancer. The passed arguments are the File and JSONObject in the File
    public void load() {
        for (File i : config.getFiles()) {
            try {
                V object = instancer.newInstance(i, config.load(i));
                registry.put(object.getKey(), object);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
