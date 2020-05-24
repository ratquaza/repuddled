package org.baito.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import org.baito.API.command.Command;
import org.baito.API.image.ImageUtils;
import org.baito.shop.Shop;
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
        }
    }
}
