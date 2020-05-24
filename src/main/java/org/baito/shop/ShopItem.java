package org.baito.shop;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import org.baito.MasterRegistry;
import org.baito.account.Account;
import org.baito.account.Condition;

import java.awt.*;

public class ShopItem {

    private int price;
    private boolean useMaple;
    private String name;
    private String description;
    private ShopEvent onBuy;
    private Availability a;

    public ShopItem(String name, String description, int price, boolean useMaple, ShopEvent onBuy, Availability a) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.useMaple = useMaple;
        this.onBuy = onBuy;
        this.a = a;
    }

    public int getPrice() {
        return price;
    }

    public boolean useMaple() {
        return useMaple;
    }

    public String name() {
        return name;
    }

    public String description() {
        return description;
    }

    public void buy(Member m, TextChannel channel) {
        Account account = MasterRegistry.accountRegistry().get(m.getUser());
        if ((useMaple && account.condMaple(Condition.EQUALGREATER, price) || (!useMaple && account.condGold(Condition.EQUALGREATER, price)))) {
            if (a.isAvailable(m)) {
                onBuy.fire(m, channel, this);
            } else {
                channel.sendMessage(new EmbedBuilder().setColor(Color.RED).setAuthor(a.unavailableMessage()).build()).queue();
            }
        } else {
            channel.sendMessage(new EmbedBuilder().setColor(Color.RED).setAuthor("Insufficient funds.").build()).queue();
        }
    }

    public interface ShopEvent {
        void fire(Member member, TextChannel channel, ShopItem item);
    }

    public interface Availability {
        boolean isAvailable(Member m);
        String unavailableMessage();
    }
}
