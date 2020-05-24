package org.baito.shop;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class Shop {
    private static HashMap<String, Shop> registeredShops = new HashMap<>();

    public static void registerShop(Shop s) {
        registeredShops.put(s.name, s);
    }

    public static Collection<Shop> getShops() {
        return registeredShops.values();
    }

    private HashMap<String, ShopItem> items;
    private String name;
    private String description;

    public Shop(String name, String description) {
        this.name = name;
        this.description = description;
        items = new HashMap<>();
    }

    public ShopItem getItem(String s) {
        return items.getOrDefault(s, null);
    }

    public void registerItem(ShopItem i) {
        items.put(i.name(), i);
    }

    public String name() {
        return name;
    }

    public String description() {
        return description;
    }

    static {
        Shop upgrades = new Shop("Upgrades", "Buy upgrades and subscriptions here!");
        registerShop(upgrades);
    }
}
