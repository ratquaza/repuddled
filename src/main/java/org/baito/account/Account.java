package org.baito.account;

import net.dv8tion.jda.api.entities.User;
import org.baito.API.registry.SerializableRegistryEntry;
import org.baito.Main;
import org.baito.MasterRegistry;
import org.baito.Events;
import org.baito.stonk.Market;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Account implements SerializableRegistryEntry<User> {
    private User user;

    private long gold;
    private long maple;

    private HashMap<Market, Integer> marketItems = new HashMap<>();
    private HashMap<Flag, Boolean> flags = new HashMap<>();

    private int level;
    private int experience;

    public Account() {
    }
    public Account(User u) {
        this.user = u;
    }

    // Flags
    public boolean getFlag(Flag f) {
        return flags.getOrDefault(f, false);
    }
    public void setFlag(Flag f, boolean value) {
        flags.put(f, value);
    }
    public HashMap<Flag, Boolean> getFlags() {
        return flags;
    }

    // Currencies
    public long getGold() {
        return gold;
    }
    public void modifyGold(Modify mod, int amount) {
        gold = (long) mod(gold, mod, amount);
    }
    public boolean condGold(Condition con, int amount) {
        return con(gold, con, amount);
    }

    public long getMaple() {
        return maple;
    }
    public void modifyMaple(Modify mod, long amount) {
        maple = (long) mod(maple, mod, amount);
    }
    public boolean condMaple(Condition con, long amount) {
        return con(maple, con, amount);
    }

    // Market values
    public int getMarket(Market m) {
        return marketItems.getOrDefault(m, 0);
    }
    public boolean condMarket(Market m, Condition con, int amount) {
        return con(marketItems.getOrDefault(m, 0), con, amount);
    }
    public void modifyMarket(Market m, Modify mod, int amount) {
        marketItems.put(m, (int) mod(marketItems.getOrDefault(m, 0), mod, amount));
    }

    // Level & XP system. XP for Level = Level * 75
    public int getLevel() {
        return level;
    }
    public boolean condLevel(Condition con, int amount) {
        return con(level, con, amount);
    }
    public void modifyLevel(Modify mod, int amount) {
        level = (int) mod(level, mod, amount);
    }

    public int getXP() {
        return experience;
    }
    public void modifyXP(Modify mod, int amount) {
        experience = (int) mod(experience, mod, amount);
        if (experience >= (level + 1) * 75) {
            Events.onLevelUp(user, level, level + 1);
        }
    }
    public boolean condXP(Condition con, int amount) {
        return con(experience, con, amount);
    }

    public String balance() {
        return Main.gold() + " " + gold + " " + Main.maple() + " " + maple;
    }

    public String marketBalance() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Market, Integer> i : marketItems.entrySet()) {
            sb.append("**" + i.getValue() + "** " + i.getKey().getName() + "s\n");
        }
        return sb.toString();
    }

    public JSONObject toJson() {
        JSONObject j = new JSONObject();

        // Currencies JSONObject
        JSONObject currencies = new JSONObject();
        currencies.put("gold", gold);
        currencies.put("maple", maple);
        j.put("currencies", currencies);

        // XP
        JSONObject leveldat = new JSONObject();
        leveldat.put("xp", experience);
        leveldat.put("level", level);
        j.put("leveldat", leveldat);

        JSONObject mdj = new JSONObject();
        for (Map.Entry<Market, Integer> i : marketItems.entrySet()) {
            mdj.put(i.getKey().getKey(), i.getValue());
        }

        j.put("marketData", mdj);
        j.put("flags", flags);
        return j;
    }

    @Override
    public Account fromJson(File file, JSONObject j) {
        if (j.has("currencies")) {
            // Currencies JSONObject
            JSONObject currencies = j.getJSONObject("currencies");
            gold = currencies.getInt("gold");
            maple = currencies.getInt("maple");
        }

        user = Main.getBot().retrieveUserById(file.getName().substring(0, file.getName().lastIndexOf('.'))).complete();

        // XP
        if (j.has("leveldat")) {
            JSONObject leveldat = j.getJSONObject("leveldat");
            experience = leveldat.getInt("xp");
            level = leveldat.getInt("level");
        }

        for (Market i : MasterRegistry.marketRegistry().values()) {
            marketItems.put(i, 0);
        }

        if (j.has("marketData")) {
            JSONObject marketDataJ = j.getJSONObject("marketData");
            for (String i : marketDataJ.keySet()) {
                marketItems.put(MasterRegistry.marketRegistry().get(i), marketDataJ.getInt(i));
            }
        }

        if (j.has("flags")) {
            JSONObject f = j.getJSONObject("flags");
            for (String i : f.keySet()) {
                flags.put(Flag.valueOf(i), f.getBoolean(i));
            }
        }
        return this;
    }

    public User getKey() {
        return user;
    }

    public String fileName() {
        return user.getId();
    }

    private static double mod(double original, Modify modification, double amount) {
        switch (modification) {
            case ADD:
                return Math.max(0, original + amount);
            case SUBTRACT:
                return Math.max(0, original - amount);
            case DIVIDE:
                return Math.max(0, original/amount);
            case MULTIPLY:
                return Math.max(0, original * amount);
            case SET:
                return Math.max(0, amount);
            default:
                return Math.max(0, original);
        }
    }

    private static boolean con(double original, Condition cond, double amount) {
        switch (cond) {
            default:
                return true;
            case EQUALS:
                return original == amount;
            case GREATER:
                return original > amount;
            case LESSER:
                return original < amount;
            case EQUALLESSER:
                return original <= amount;
            case EQUALGREATER:
                return original >= amount;
        }
    }
}
