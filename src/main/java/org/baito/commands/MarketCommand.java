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
import org.baito.account.Account;
import org.baito.account.Condition;
import org.baito.account.Modify;
import org.baito.stonk.Market;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

public class MarketCommand implements Command {

    private static SingularRegistry<String, Market> marketRegistry = MasterRegistry.marketRegistry();

    @Override
    public void execute(Member executor, String[] arguments, MessageChannel channel, Message message) {
        if (arguments.length == 0) {
            EmbedBuilder eb = new EmbedBuilder();
            eb.setColor(new Color(254, 200, 0)).setTitle(Math.floor(Math.random() * 10) == 0 ? "COCK MARKET" : "STONK MARKET");

            Calendar nextHour = (Calendar) Main.getCalendar().clone();
            nextHour.set(Calendar.HOUR_OF_DAY, nextHour.get(Calendar.HOUR_OF_DAY) + 1);

            StringBuilder open = new StringBuilder();
            for (Market i : marketRegistry.values()) {
                if (i.isOpen(Main.getCalendar(), executor.getUser())) {
                    if (i.isOpen(nextHour, executor.getUser())) {
                        open.append(":green_circle:");
                    } else {
                        open.append(":yellow_circle:");
                    }
                } else {
                    open.append(":red_circle:");
                }

                open.append(" **" + i.getKey() + "**\u200B \u200B |\u200B \u200B " + Main.curr(i.usesMaple()) + " " + i.getPrice() + " " +
                        (i.hasIncreased() ? " :small_red_triangle:" : " :small_red_triangle_down:") + "\nLevel Minimum: *" + i.getLevel() + "*\n");
            }

            eb.addField("MARKETS", (open.length() > 0 ? open.toString() : "No Markets are open."), false);
            eb.addField("INFO", ":green_circle: The Market is open.\n:yellow_circle: The Market will close next update.\n:red_circle: The Market is closed.", false);
            eb.addField("SUBCOMMANDS", ".market [market name] | **Info about a Market.**" +
                    "\n.market [market name] [buy/sell] [amount] | **Buy or sell from a Market.**", false);

            ImageUtils.embedImage(channel, ImageUtils.getImage("MARKETICON.png"), eb, true, "MARKETICON", "png");
            return;
        }
        if (arguments.length == 1) {
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

            Account account = MasterRegistry.accountRegistry().get(executor.getUser());

            EmbedBuilder eb = new EmbedBuilder();
            eb.setColor(m.color);
            eb.setTitle(m.getKey() + " MARKET");
            eb.addField("INFO", m.getDescription(), false);
            eb.addField("PRICE", Main.curr(m.usesMaple()) + " " + m.getPrice() + " " + (m.hasIncreased() ? ":small_red_triangle:" : ":small_red_triangle_down:"), false);
            eb.addField("STATUS", "Currently " + (m.isOpen(Main.getCalendar(), null) ? "open for " + m.purchadeMode(Main.getCalendar()).presentVerb : "closed."), false);
            if (m.isOpen(Main.getCalendar(), null)) {
                eb.addField("STOCK", "**" + m.getStock() + "** " + m.getName() + "s available.", false);
            }
            eb.addField("DATA", "You own **" + account.getMarket(m) + " " + m.getName() + "s**\n" + m.getHighest() + " high " + m.getLowest() + " low " + m.average() + " avg", false);

            BufferedImage img = ImageUtils.getImage("MARKETICONS/" + m.getKey().replace(" ", "_") + ".png");
            ImageUtils.embedImage(channel, img, eb, true, m.getKey().replace(" ", "_"), "png");
            return;
        }
        if (arguments.length >= 2) {
            if (arguments.length == 2) {
                channel.sendMessage(new EmbedBuilder().setAuthor("Not enough arguments. Please specify a market, to buy or sell, and amount").setColor(Color.RED).build()).queue();
                return;
            }

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
            Account account = MasterRegistry.accountRegistry().get(executor.getUser());

            boolean buying;
            if (arguments[1].equalsIgnoreCase("buy") || arguments[1].equalsIgnoreCase("b")) {
                buying = true;
            } else if (arguments[1].equalsIgnoreCase("sell") || arguments[1].equalsIgnoreCase("s")) {
                buying = false;
            } else {
                EmbedBuilder eb = new EmbedBuilder();
                channel.sendMessage(eb.setColor(Color.RED).setAuthor("Invalid command.").build()).queue();
                return;
            }

            int amount;
            try {
                amount = Integer.parseInt(arguments[2]);
            } catch (NumberFormatException e) {
                channel.sendMessage(new EmbedBuilder().setColor(Color.RED).setAuthor("Invalid number.").build()).queue();
                return;
            }

            if (amount <= 0) {
                channel.sendMessage(new EmbedBuilder().setColor(Color.RED).setAuthor("Invalid number.").build()).queue();
                return;
            }

            if (buying && (m.purchadeMode() == Market.PurchadeMode.BUY || m.purchadeMode() == Market.PurchadeMode.BOTH)) {
                if ((m.usesMaple() && account.condMaple(Condition.EQUALGREATER, amount * m.getPrice()))
                        || (!m.usesMaple() && account.condGold(Condition.EQUALGREATER, amount * m.getPrice()))) {
                    if (m.usesMaple()) {
                        account.modifyMaple(Modify.SUBTRACT, amount * m.getPrice());
                    } else {
                        account.modifyGold(Modify.SUBTRACT, amount * m.getPrice());
                    }
                    account.modifyMarket(m, Modify.ADD, amount);
                    channel.sendMessage(new EmbedBuilder().setColor(Color.GREEN).setAuthor("Successfully bought **" + amount + "** stock for " +
                            Main.curr(m.usesMaple()) + (amount * m.getPrice())).build()).queue();
                } else {
                    channel.sendMessage(new EmbedBuilder().setColor(Color.RED).setAuthor("Insufficient funds.").build()).queue();
                }
            } else if (!buying && (m.purchadeMode() == Market.PurchadeMode.SELL || m.purchadeMode() == Market.PurchadeMode.BOTH)) {
                if (account.condMarket(m, Condition.EQUALGREATER, amount)) {
                    if (m.usesMaple()) {
                        account.modifyMaple(Modify.ADD, amount * m.getPrice());
                    } else {
                        account.modifyGold(Modify.ADD, amount * m.getPrice());
                    }
                    account.modifyMarket(m, Modify.SUBTRACT, amount);
                    channel.sendMessage(new EmbedBuilder().setColor(Color.GREEN).setAuthor("Successfully sold **" + amount + "** stock for " +
                            Main.curr(m.usesMaple()) + (amount * m.getPrice())).build()).queue();
                } else {
                    channel.sendMessage(new EmbedBuilder().setColor(Color.RED).setAuthor("Insufficient stock.").build()).queue();
                }
            } else {
                channel.sendMessage(new EmbedBuilder().setAuthor("This market is " + m.purchadeMode().presentVerb + ".").setColor(Color.RED).build()).queue();
                return;
            }
        }
    }
}
