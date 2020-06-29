package org.baito.shop.items;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

public abstract class ShopItem {

    protected final String name;
    protected final String description;
    protected final int price;
    protected final boolean useMaple;

    public ShopItem(String name, String description, int price, boolean useMaple) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.useMaple = useMaple;
    }

    public final String name() {
        return name;
    }

    public final String desc() {
        return description;
    }

    public final int price() {
        return price;
    }

    public final boolean useMaple() {
        return useMaple;
    }

    public abstract void onBuy(Member m, TextChannel channel);

    public abstract boolean canPurchase(Member m);

    public abstract String unavailableMessage(Member m);

}
