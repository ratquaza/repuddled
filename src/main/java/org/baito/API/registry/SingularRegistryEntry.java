package org.baito.API.registry;

import org.json.JSONObject;

public interface SingularRegistryEntry<K> {
    K getKey();
    String fileName();

    JSONObject onSave();
    void onLoad(JSONObject j);
}
