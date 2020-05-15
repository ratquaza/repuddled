package org.baito.casino;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import org.baito.MasterRegistry;
import org.baito.Main;
import org.baito.casino.games.multiplayer.MPCasinoGame;
import org.baito.casino.games.singeplayer.SPCasinoGame;
import org.baito.casino.games.singeplayer.Blackjack;
import org.baito.casino.games.singeplayer.Slots;
import org.baito.data.Account;
import org.baito.data.Modify;

import java.awt.*;
import java.util.Collection;
import java.util.HashMap;

public class Casino {

    private static HashMap<String, SPCasinoGame> SPRegistry = new HashMap<>();
    private static HashMap<Member, SPCasinoGame> SPRunning = new HashMap<>();

    private static HashMap<String, MPCasinoGame> MPRegistry = new HashMap<>();
    private static HashMap<Member, MPCasinoGame> MPRunning = new HashMap<>();

    public static void registerSPGame(SPCasinoGame game) {
        SPRegistry.put(game.name(), game);
    }

    public static void registerSPGames(SPCasinoGame... games) {
        for (SPCasinoGame i : games) {
            registerSPGame(i);
        }
    }

    public static void startSPGame(Member m, SPCasinoGame game, MessageChannel channel, int bet, boolean isMaple) {
        SPCasinoGame g = game.newInstance();
        SPRunning.put(m, g);
        Account account = (Account) MasterRegistry.getSerializableRegistry(Account.class).get(m.getUser());
        if (isMaple) {
            account.modifyMaple(Modify.SUBTRACT, bet);
        } else {
            account.modifyGold(Modify.SUBTRACT, bet);
        }
        channel.sendMessage(new EmbedBuilder().setColor(new Color(255, 200, 0))
                .setDescription("**" + m.getEffectiveName() + "'s " + game.name() + " Game** - " + (isMaple ? Main.maple() : Main.gold()) + " " + bet).build()).queue();
        g.setValues(m, bet, isMaple);
        g.setup(channel);
    }

    public static SPCasinoGame getRegisteredSPGame(String name) {
        return SPRegistry.getOrDefault(name, null);
    }

    public static void endSPGame(Member m, MessageChannel channel) {
        SPCasinoGame rg = SPRunning.get(m);
        channel.sendMessage("x" + rg.getMultiplier() + " Multiplier, winning **" + (rg.useMaple() ? Main.maple() : Main.gold()) + " " + ((int)Math.round(rg.getBet() * rg.getMultiplier())) + "**").queue();
        Account account = (Account) MasterRegistry.getSerializableRegistry(Account.class).get(m.getUser());
        if (rg.useMaple()) {
            account.modifyMaple(Modify.ADD, (int) Math.round(rg.getBet() * rg.getMultiplier()));
        } else {
            account.modifyGold(Modify.ADD, (int) Math.round(rg.getBet() * rg.getMultiplier()));
        }
        SPRunning.remove(m);
    }

    public static Collection<SPCasinoGame> singleplayerGames() {
        return SPRegistry.values();
    }

    public static boolean hasSPGame(Member m) {
        return SPRunning.containsKey(m);
    }

    public static void parseSPTurn(Member m, MessageChannel channel, String[] args) {
        if (SPRunning.containsKey(m)) {
            SPRunning.get(m).turn(channel, args);
        }
    }

    public static void endMPGame(Member... members) {
        for (Member i : members) {
            MPRunning.remove(i);
        }
    }

    static {
        registerSPGames(new Slots(), new Blackjack());
    }
}
