package org.baito.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import org.baito.API.command.Command;
import org.baito.API.image.ImageUtils;
import org.baito.API.registry.SingularRegistry;
import org.baito.Main;
import org.baito.MasterRegistry;
import org.baito.data.Account;
import org.baito.stonk.Market;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Map;

public class MarketCommand implements Command {

    private static SingularRegistry<String, Market> marketRegistry = MasterRegistry.getSingularRegistry(Market.class);

    @Override
    public void execute(Member executor, String[] arguments, MessageChannel channel, Message message) {
        if (arguments.length == 0) {
            EmbedBuilder eb = new EmbedBuilder();
            eb.setColor(new Color(254, 200, 0)).setTitle(Math.floor(Math.random() * 10) == 0 ? "COCK MARKET" : "STONK MARKET");

            StringBuilder open = new StringBuilder();
            for (Market i : marketRegistry.values()) {
                if (i.canBuy(Main.getCalendar())) {
                    open.append(":green_circle:");
                } else {
                    open.append(":red_circle:");
                }

                open.append(" **" + i.getKey() + "**\u200B \u200B |\u200B \u200B " + (i.usesMaple() ? Main.maple() : Main.gold()) + " " + i.getPrice() + " " +
                        (i.hasIncreased() ? " :small_red_triangle:" : " :small_red_triangle_down:") + "\nLevel Minimum: *" + i.getLevel() + "*\n");
            }

            eb.addField("MARKETS", (open.length() > 0 ? open.toString() : "No Markets are open."), false);
            eb.addField("INFO", ":green_circle: The Market is open.\n:yellow_circle: The Market will close next update.\n:red_circle: The Market is closed.", false);
            eb.addField("SUBCOMMANDS", ".market [market name] | **Info about a Market.**", false);

            ImageUtils.embedImage(channel, ImageUtils.getImage("MARKETICON.png"), eb, "MARKETICON", "png");
        } else {
            ArrayList<Market> possibles = new ArrayList<>();
            for (Map.Entry<String, Market> i : marketRegistry.entrySet()) {
                if (i.getKey().equalsIgnoreCase(arguments[0].toUpperCase()) || i.getKey().contains(arguments[0].toUpperCase())) {
                    possibles.add(i.getValue());
                }
            }

            if (possibles.size() != 1) {
                channel.sendMessage(new EmbedBuilder().setColor(new Color(255, 0, 0)).setAuthor("No Market found, or too many found.").build()).queue();
                return;
            }

            Market m = possibles.get(0);

            Account account = (Account) MasterRegistry.getSerializableRegistry(Account.class).get(executor.getUser());

            EmbedBuilder eb = new EmbedBuilder();
            eb.setColor(m.color);
            eb.setTitle(m.getKey() + " MARKET");
            eb.addField("PRICE", (m.usesMaple() ? Main.maple() : Main.gold()) + " " + m.getPrice() + " " + (m.hasIncreased() ? ":small_red_triangle:" : ":small_red_triangle_down:"), false);
            eb.addField("STATUS", "You **can" + (m.canBuy(Main.getCalendar()) ? "" : "not") + "** buy as of now.", false);
            if (m.canBuy(Main.getCalendar())) {
                eb.addField("STOCK", "**" + m.getStock() + "** " + m.getName() + "s available.", false);
            }
            eb.addField("DATA", "You own **" + account.getMarket(m) + " " + m.getName() + "s**\n" + m.getHighest() + " high " + m.getLowest() + " low " + m.average() + " avg", false);

            BufferedImage img = ImageUtils.getImage("MARKETICONS/" + m.getKey().replace(" ", "_") + ".png");
            ImageUtils.embedImage(channel, img, eb, m.getKey().replace(" ", "_"), "png");
        }
    }
}
