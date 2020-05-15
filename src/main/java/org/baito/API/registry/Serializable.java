package org.baito.API.registry;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public interface Serializable {

    JSONObject toJson();
    Serializable fromJson(File file, JSONObject j) throws JSONException;

}
