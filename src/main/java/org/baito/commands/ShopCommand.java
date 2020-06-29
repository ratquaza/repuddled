package org.baito.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import org.baito.API.command.Command;
import org.baito.API.image.ImageUtils;
import org.baito.Main;
import org.baito.shop.Shop;
import org.baito.shop.items.ShopItem;

import java.awt.*;
import java.util.Collection;

public class ShopCommand implements Command {
    @Override
    public void execute(Member executor, String[] arguments, MessageChannel channel, Message message) {
        if (arguments.length == 0) {
            EmbedBuilder eb = new EmbedBuilder();

            eb.setTitle("**SHOPS**");
            eb.setColor(Color.GREEN);
            StringBuilder sb = new StringBuilder();
            Collection<Shop> shops = Shop.getShops();
            for (Shop i : shops) {
                sb.append("**" + i.name() + "** *" + i.description() + "*\n");
            }

            eb.addField("AVAILABLE SHOPS", sb.toString(), false);

            ImageUtils.embedImage(channel, ImageUtils.getImage("SHOP.png"), eb, true, "SHOP", "png");
            return;
        }

        if (arguments.length == 1) {
            Shop s = null;
            String query = arguments[0].toUpperCase();

            for (Shop i : Shop.getShops()) {
                if (i.name().toUpperCase().contains(query) || i.name().toUpperCase().equals(query)) {
                    s = i;
                    break;
                }
            }
            if (s == null) {
                channel.sendMessage(new EmbedBuilder().setColor(Color.RED).setAuthor("No shop found.").build()).queue();
                return;
            }

            EmbedBuilder shop = new EmbedBuilder();
            shop.setColor(new Color(255, 180, 0)).setTitle(s.name().toUpperCase() + " SHOP").setDescription(s.description());

            StringBuilder items = new StringBuilder();

            for (ShopItem i : s.getItems()) {
                items.append(":" + (i.canPurchase(executor) ? "green" : "red") + "_circle: **" + i.name() + "** \n*" + i.desc()
                        + "* | " + Main.curr(i.useMaple()) + i.price() + "\n");
            }

            shop.addField("ITEMS", items.toString(), false);

            channel.sendMessage(shop.build()).queue();
            return;
        }
    }
}
