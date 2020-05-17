package org.baito.API;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import org.baito.API.TimerManager;
import org.baito.API.registry.SerializableRegistry;
import org.baito.Main;
import org.baito.MasterRegistry;
import org.baito.data.Account;
import org.baito.data.Modify;
import org.baito.stonk.Market;

import java.awt.*;
import java.util.Calendar;
import java.util.Collection;

public class Events {

    public static void onLevelUp(User e, int oldLevel, int newLevel) {
        Account account = MasterRegistry.accountRegistry().get(e);
        account.modifyLevel(Modify.ADD, 1);
        account.modifyXP(Modify.SUBTRACT, account.getLevel() * 75);

        account.modifyGold(Modify.ADD, account.getLevel() * 100);

        if (account.getLevel() % 5 == 0) {
            account.modifyMaple(Modify.ADD, 1);
        }
    }

    public static void onHourly(Calendar now) {
        Calendar nextHour = (Calendar) now.clone();
        Calendar previousTime = (Calendar) now.clone();
        if (previousTime.get(Calendar.HOUR_OF_DAY) == 0) {
            previousTime.set(Calendar.HOUR_OF_DAY, 23);
        } else {
            previousTime.set(Calendar.HOUR_OF_DAY, previousTime.get(Calendar.HOUR_OF_DAY) - 1);
        }
        if (nextHour.get(Calendar.HOUR_OF_DAY) == 23) {
            nextHour.set(Calendar.HOUR_OF_DAY, 0);
        } else {
            nextHour.set(Calendar.HOUR_OF_DAY, nextHour.get(Calendar.HOUR_OF_DAY) + 1);
        }

        System.out.println("HOURLY " + now.getTime());
        // Puddle Day
        if (now.get(Calendar.HOUR_OF_DAY) % TimerManager.PUDDLE_DAY_INTERVAL == 0) {

        }

        StringBuilder forBuying = new StringBuilder();
        StringBuilder forSelling = new StringBuilder();
        StringBuilder closingSoon = new StringBuilder();

        SerializableRegistry<User, Account> accounts = MasterRegistry.accountRegistry();
        // Market Calculation
        for (Market i : MasterRegistry.marketRegistry().values()) {
            // If the Market is open right now
            if (i.isOpen(now, null)) {
                // and the Market was closed before
                if (!i.isOpen(previousTime, null)) {
                    forBuying.append(":green_circle: " + i.getName() + " Market\n");
                } else if (!i.isOpen(nextHour, null)) {
                    // and the Market will close next hour
                    closingSoon.append(":yellow_circle: " + i.getName() + " Market\n");
                }
            }
            // If the Market is closed right now, but it was open just an hour ago.
            // Reset the player count for the Market.
            // This does not take into account purchase mode, but opening and closing.

            // If a Market opens and closes at two different times, each closing will count as a reset.
            else if (i.isOpen(previousTime, null) && !i.isOpen(now, null)) {
                accounts.values().parallelStream().forEach(a -> a.modifyMarket(i, Modify.SET, 0));
                forSelling.append(":red_circle: " + i.getName() + " Market\n");
            }
        }

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
    }
}