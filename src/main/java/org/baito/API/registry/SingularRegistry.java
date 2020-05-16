package org.baito.API.registry;

import org.baito.API.config.FolderConfig;
import org.json.JSONObject;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SingularRegistry<K, V extends SingularRegistryEntry<K>> {
    private HashMap<K, V> registry = new HashMap<>();
    private FolderConfig config;

    public void register(V item) {
        registry.put(item.getKey(), item);
    }

    public void register(V... item) {
        for (V i : item) {
            register(i);
        }
    }

    public V get(K key) {
        return registry.getOrDefault(key, null);
    }

    public Set<K> keys() {
        return registry.keySet();
    }

    public Collection<V> values() {
        return registry.values();
    }

    public Set<Map.Entry<K, V>> entrySet() {
        return registry.entrySet();
    }

    public SingularRegistry(String name) {
        config = new FolderConfig(name.toUpperCase());
    }

    public void load() {
        for (Map.Entry<K, V> i : registry.entrySet()) {
            i.getValue().onLoad(getConfig(i.getKey()));
        }
    }

    public void save() {
        for (Map.Entry<K, V> i : registry.entrySet()) {
            saveConfig(i.getKey(), i.getValue().onSave());
        }
    }

    private JSONObject getConfig(K key) {
        return config.load(config.getFile(key.toString()));
    }

    private void saveConfig(K key, JSONObject j) {
        config.save(config.getFile(key.toString()), j);
    }
}
