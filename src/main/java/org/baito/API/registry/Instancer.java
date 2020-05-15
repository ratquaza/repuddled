package org.baito.API.registry;

import org.json.JSONObject;

import java.io.File;

public interface Instancer<V extends SerializableRegistryEntry> {

    V newInstance(File f, JSONObject j);

}
