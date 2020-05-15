package org.baito.API.config;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;

public class FolderConfig {
    private static File dataLocation = new File(System.getProperty("user.home") + "/Documents/Repuddled");

    private File folder;

    public FolderConfig(String name) {
        folder = new File(dataLocation + "/" + name);
        folder.mkdirs();
    }

    public File getFile(String name) {
        File file = new File(folder + "/" + name + ".json");
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    public boolean fileExists(String name) {
        return new File(folder + "/" + name + ".json").exists();
    }

    public File[] getFiles() {
        return folder.listFiles();
    }

    public JSONObject load(File file) {
        String data = readFile(file);
        try {
            return new JSONObject(data);
        } catch (JSONException e) {
            return new JSONObject();
        }
    }

    public void save(File file, JSONObject json) {
        FileWriter fr = null;
        try {
            fr = new FileWriter(file, false);
            fr.write(json.toString(4));
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

    public static String readFile(File file) {
        try {
            InputStream is = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            String line = reader.readLine();
            StringBuilder sb = new StringBuilder();

            while (line != null) {
                sb.append(line).append("\n");
                line = reader.readLine();
            }
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
