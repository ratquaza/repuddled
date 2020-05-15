package org.baito.stonk;

import net.dv8tion.jda.api.entities.User;
import org.baito.API.registry.SingularRegistryEntry;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.util.Arrays;
import java.util.Calendar;

//https://www.desmos.com/calculator/34s56wjysu
public abstract class Market implements SingularRegistryEntry<String> {

    Market(String s, int level, Color c, boolean useMaple, int min, int max) {
        type = s;
        this.level = level;
        this.color = c;
        this.useMaple = useMaple;
        this.minimum = min;
        this.maximum = max;
    }

    public final Color color; // Color association

    protected String type; // Name of this Market
    protected int level; // Minimum level required for Market

    protected int stock; // Current stock

    protected double x; // Increases every interval. Used for sine()

    protected double height; // Determines height
    protected double width; // Determines width
    protected double tilt; // Determines tilt, is it going up or down?
    protected double offset; // Offset, determines offset for x when calculating

    protected int xMax; // The maximum that x must reach. When reached, b,a,t,o are all randomised;
    protected double xLength; // The value xMax is divided by, producing the value that x is increased by every step

    protected int minimum; // Minimum price
    protected int maximum; // Maximum price

    protected int price; // Current price
    protected int[] history = new int[20]; // Price history, used for graphing
    protected boolean canBuy = false; // Whether players can buy today
    protected boolean useMaple = false;

    public final int[] getHistory() {return history;}

    public final int getHighest() {
        int max = minimum-1;
        for (int i : history) {
            if (i > max) max = i;
        }
        return max;
    }

    public final int getLowest() {
        int min = maximum+1;
        for (int i : history) {
            if (i < min) min = i;
        }
        return min;
    }

    public final int average() {
        return (int) Math.round(Arrays.stream(history).average().getAsDouble());
    }

    public final boolean usesMaple() {
        return useMaple;
    }

    public boolean canBuy(Calendar c) {
        canBuy = c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY;
        return canBuy;
    }

    public boolean canBuy(User u) {
        return canBuy;
    }

    public final int getPrice() {
        return price;
    }

    public final int getLevel() {
        return level;
    }

    public final boolean hasIncreased() {
        return price >= history[1];
    }

    public void step() {
        // Move every history back 1 step
        int[] newHistory = new int[20];
        for (int i = 0; i < history.length-1; i++) {
            newHistory[i+1] = history[i];
        }
        history = newHistory;
        // If x is equal to the maximum value
        if (x >= xMax * xLength) {
            // Generate new values
            newValues();
        } else {
            // Increase x by 1 interval
            x += xLength;
        }
        // Calculate the new price
        calcPrice();
        // Store the new price in the history
        history[0] = price;
    }

    protected abstract void newValues();

    protected abstract void calcPrice();

    public abstract void newStock();

    public int getStock() {
        return stock;
    }

    public String getName() {
        return type;
    }

    @Override
    public String getKey() {
        return type.toUpperCase();
    }

    @Override
    public String fileName() {
        return type.toUpperCase();
    }

    @Override
    public JSONObject onSave() {
        JSONObject j = new JSONObject();
        j.put("price", price);
        j.put("canBuy", canBuy);
        j.put("history", history);

        j.put("xMax", xMax);
        j.put("xLength", xLength);
        j.put("x", x);

        j.put("width", width);
        j.put("height", height);
        j.put("tilt", tilt);
        j.put("offset", offset);
        j.put("minimum", minimum);
        j.put("maximum", maximum);
        return j;
    }

    @Override
    public void onLoad(JSONObject j) {
        newValues();
        calcPrice();
        if (j.has("price")) {
            price = j.getInt("price");
        }
        if (j.has("canBuy")) {
            canBuy = j.getBoolean("canBuy");
        }

        if (j.has("history")) {
            JSONArray his = j.getJSONArray("history");
            for (int i = 0; i < his.length(); i++) {
                history[i] = his.getInt(i);
            }
        } else {
            Arrays.fill(history, price);
        }

        if (j.has("xMax")) {
            xMax = j.getInt("xMax");
        }
        if (j.has("xLength")) {
            xLength = j.getDouble("xLength");
        }
        if (j.has("x")) {
            x = j.getInt("x");
        }

        if (j.has("width")) {
            width = j.getDouble("width");
        }
        if (j.has("height")) {
            height = j.getDouble("height");
        }
        if (j.has("tilt")) {
            tilt = j.getDouble("tilt");
        }
        if (j.has("offset")) {
            offset = j.getDouble("offset");
        }

        if (j.has("minimum") && j.has("maximum")) {
            minimum = j.getInt("minimum");
            maximum = j.getInt("maximum");
        }
    }

    public int hashCode() {
        return type.hashCode();
    }

    public boolean equals(Object o) {
        return o instanceof Market && o.hashCode() == hashCode();
    }

}
