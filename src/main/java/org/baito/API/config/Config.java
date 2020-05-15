package org.baito.API.config;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Config {
    private static File dataLocation = new File(System.getProperty("user.home") + "/Documents/Repuddled");

    private File file;

    public Config(String name) {
        file = new File(dataLocation + "/" + name + ".json");
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public JSONObject load() {
        try {
            return new JSONObject(FolderConfig.readFile(file));
        } catch (JSONException e) {
            return new JSONObject();
        }
    }

    public void save(JSONObject j) {
        FileWriter fr = null;
        try {
            fr = new FileWriter(file, false);
            fr.write(j.toString(4));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
