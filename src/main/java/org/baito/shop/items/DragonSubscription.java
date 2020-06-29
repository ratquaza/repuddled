package org.baito.shop.items;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import org.baito.MasterRegistry;
import org.baito.account.Account;
import org.baito.account.Flag;

import java.awt.*;

public class DragonSubscription extends ShopItem {
    public static final int PRICE = 20;

    public DragonSubscription() {
        super("Dragon Market Subscription", "Allows access to the Dragon Market. Charges daily.",
                PRICE, true);
    }

    @Override
    public void onBuy(Member m, TextChannel channel) {
        Account a = MasterRegistry.accountRegistry().get(m.getUser());
        a.setFlag(Flag.DRAGON_SUBSCRIPTION, true);
        channel.sendMessage(new EmbedBuilder().setColor(Color.GREEN)
                .setAuthor("You now have access to the Dragon Market.").build()).queue();
    }

    @Override
    public boolean canPurchase(Member m) {
        Account a = MasterRegistry.accountRegistry().get(m.getUser());
        return a.getLevel() >= 15 && !a.getFlag(Flag.DRAGON_SUBSCRIPTION);
    }

    @Override
    public String unavailableMessage(Member m) {
        Account a = MasterRegistry.accountRegistry().get(m.getUser());
        return a.getLevel() < 15 ? "You must be level 15." : a.getFlag(Flag.DRAGON_SUBSCRIPTION)
                ? "You already have access to the Dragon Market." : "You cannot purchase this.";
    }
}
