package org.baito;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import org.baito.API.registry.SerializableRegistry;
import org.baito.account.Account;
import org.baito.account.Condition;
import org.baito.account.Flag;
import org.baito.account.Modify;
import org.baito.shop.items.DragonSubscription;
import org.baito.stonk.DragonMarket;
import org.baito.stonk.Market;

import java.awt.*;
import java.util.Calendar;

public class Events {

    public static void onLevelUp(User e, int oldLevel, int newLevel) {
        Account account = MasterRegistry.accountRegistry().get(e);
        account.modifyLevel(Modify.SET, newLevel);
        account.modifyXP(Modify.SUBTRACT, oldLevel * 75);

        account.modifyGold(Modify.ADD, newLevel * 100);

        if (newLevel % 5 == 0) {
            account.modifyMaple(Modify.ADD, 1);
        }
    }

    public static void onHourly(Calendar now) {
        // Get the previous hour and next hour as easy variables
        Calendar next = (Calendar) now.clone();
        Calendar before = (Calendar) now.clone();
        if (before.get(Calendar.HOUR_OF_DAY) == 0) {
            before.set(Calendar.HOUR_OF_DAY, 23);
        } else {
            before.set(Calendar.HOUR_OF_DAY, before.get(Calendar.HOUR_OF_DAY) - 1);
        }
        if (next.get(Calendar.HOUR_OF_DAY) == 23) {
            next.set(Calendar.HOUR_OF_DAY, 0);
        } else {
            next.set(Calendar.HOUR_OF_DAY, next.get(Calendar.HOUR_OF_DAY) + 1);
        }

        System.out.println("HOURLY " + now.getTime());

        StringBuilder forBuying = new StringBuilder();
        StringBuilder forSelling = new StringBuilder();
        StringBuilder closingSoon = new StringBuilder();

        SerializableRegistry<User, Account> accounts = MasterRegistry.accountRegistry();
        // Market Calculation system
        // When a market closes, players will lose all their stock in the said market.
        // When a market just opens, the stock will be randomized again.
        for (Market i : MasterRegistry.marketRegistry().values()) {
            // If the Market is open right now
            if (i.isOpen(now, null)) {
                // and the Market was closed before
                if (!i.isOpen(before, null)) {
                    // Means it has just opened, therefore roll for new stock.
                    forBuying.append(":green_circle: " + i.getName() + " Market\n");
                    i.newStock();
                } else if (!i.isOpen(next, null)) {
                    // and the Market will close next hour
                    // Its open before and now, but next hour it wont be.
                    closingSoon.append(":yellow_circle: " + i.getName() + " Market\n");
                }
            }
            // If the Market is closed right now, but it was open just an hour ago.
            // Reset the player count for the Market.
            // This does not take into account purchase mode, but opening and closing.

            // If a Market opens and closes at two different times, each closing will count as a reset.
            else if (i.isOpen(before, null)) {
                accounts.values().parallelStream().forEach(a -> a.modifyMarket(i, Modify.SET, 0));
                forSelling.append(":red_circle: " + i.getName() + " Market\n");
            }
        }

        // Message the new status of any Market, if any are closing soon, just opened, or just closed.
        // This does not message if a Market was open before, is open now, and is open next hour nor does
        // it message if a market was closed before, is closed now, and is still closed next hour.
        // This will only message when a Market has moved from one state to another, or if the Market is
        // about to close
        if (forBuying.length() > 0 || forSelling.length() > 0 || closingSoon.length() > 0) {
            EmbedBuilder eb = new EmbedBuilder().setTitle("MARKET UPDATES").setColor(new Color(200, 170, 0));
            if (forBuying.length() > 0) {
                eb.addField("OPEN MARKETS", forBuying.toString(), false);
            }
            if (forSelling.length() > 0) {
                eb.addField("CLOSED MARKETS", forSelling.toString(), false);
            }
            if (closingSoon.length() > 0) {
                eb.addField("CLOSING SOON", closingSoon.toString(), false);
            }
            Main.notifyAll(eb.build());
        }

        // New Real life day
        if (before.get(Calendar.HOUR_OF_DAY) == 23 && now.get(Calendar.HOUR_OF_DAY) == 0) {
            // Dragon Market Subscription handler, charge people or revoke everything
            MasterRegistry.accountRegistry().values().parallelStream().forEach(a -> {
                if (a.getFlag(Flag.DRAGON_SUBSCRIPTION)) {
                    // If they don't have the funds, revoke their subscription and pay them for the rest of their eggs.
                    if (!a.condMaple(Condition.EQUALGREATER, DragonSubscription.PRICE)) {
                        DragonMarket market = (DragonMarket) MasterRegistry.marketRegistry().get("Dragon Egg");
                        int amount = a.getMarket(market);
                        a.modifyMarket(market, Modify.SET, 0);
                        a.modifyMaple(Modify.ADD, amount * market.getPrice());

                        a.getKey().openPrivateChannel().complete().sendMessage("Your Dragon Subscription has ended. "
                                + "We've taken the liberty of selling all your Eggs for a total of :maple:" + (amount * market.getPrice())
                                + ", thanks for working with Dragon Ltd!").queue();
                    } else {
                        // If they do have the funds, just charge them.
                        a.modifyMaple(Modify.SUBTRACT, DragonSubscription.PRICE);
                    }
                }
            });
        }

        // New Puddle Day
        if (now.get(Calendar.HOUR_OF_DAY) % Main.PUDDLE_DAY_INTERVAL == 0) {
            // Market step - Markets update every Puddle day, no matter if opened or closed.
            MasterRegistry.marketRegistry().values().parallelStream().forEach(Market::step);
            // Notify all servers
            EmbedBuilder eb = new EmbedBuilder();
            Main.notifyAll(eb.setAuthor("A new Puddle Day has risen!").setColor(Color.GREEN).build());
        }
    }
}