package org.baito.casino;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import org.baito.API.registry.SerializableRegistry;
import org.baito.MasterRegistry;
import org.baito.Main;
import org.baito.casino.games.multiplayer.MPCasinoGame;
import org.baito.casino.games.multiplayer.Roulette;
import org.baito.casino.games.multiplayer.TicTacToe;
import org.baito.casino.games.singeplayer.SPCasinoGame;
import org.baito.casino.games.singeplayer.Blackjack;
import org.baito.casino.games.singeplayer.Slots;
import org.baito.data.Account;
import org.baito.data.Modify;

import java.awt.*;
import java.util.*;

public class Casino {

    private static HashMap<String, SPCasinoGame> SPRegistry = new HashMap<>();
    private static HashMap<Member, SPCasinoGame> SPRunning = new HashMap<>();

    private static HashMap<String, MPCasinoGame> MPRegistry = new HashMap<>();
    private static HashMap<Member, MPCasinoGame> MPRunning = new HashMap<>();

    public static void registerSPGame(SPCasinoGame game) {
        SPRegistry.put(game.name().toUpperCase(), game);
    }

    public static void registerSPGames(SPCasinoGame... games) {
        for (SPCasinoGame i : games) {
            registerSPGame(i);
        }
    }

    public static void startSPGame(Member m, SPCasinoGame game, MessageChannel channel, int bet, boolean isMaple) {
        SPCasinoGame g = game.newInstance();
        SPRunning.put(m, g);
        Account account = MasterRegistry.accountRegistry().get(m.getUser());
        if (isMaple) {
            account.modifyMaple(Modify.SUBTRACT, bet);
        } else {
            account.modifyGold(Modify.SUBTRACT, bet);
        }
        channel.sendMessage(new EmbedBuilder().setColor(new Color(255, 200, 0))
                .setDescription("**" + m.getEffectiveName() + "'s " + game.name() + " Game** - " + Main.curr(isMaple) + " " + bet).build()).queue();
        g.setValues(m, bet, isMaple);
        g.setup(channel);
    }

    public static SPCasinoGame getRegisteredSPGame(String name) {
        return SPRegistry.getOrDefault(name.toUpperCase(), null);
    }

    public static void endSPGame(Member winner, MessageChannel channel) {
        SPCasinoGame rg = SPRunning.get(winner);
        channel.sendMessage("x" + rg.getMultiplier() + " Multiplier, winning **" + Main.curr(rg.useMaple()) + " " + ((int)Math.round(rg.getBet() * rg.getMultiplier())) + "**").queue();
        Account account = MasterRegistry.accountRegistry().get(winner.getUser());
        if (rg.useMaple()) {
            account.modifyMaple(Modify.ADD, (int) Math.round(rg.getBet() * rg.getMultiplier()));
        } else {
            account.modifyGold(Modify.ADD, (int) Math.round(rg.getBet() * rg.getMultiplier()));
        }
        SPRunning.remove(winner);
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

    public static void registerMPGame(MPCasinoGame game) {
        MPRegistry.put(game.name().toUpperCase(), game);
    }

    public static void registerMPGames(MPCasinoGame... games) {
        for (MPCasinoGame i : games) {
            registerMPGame(i);
        }
    }

    public static void startMPGame(MPCasinoGame game, MessageChannel channel, int bet, boolean isMaple, Member... members) {
        MPCasinoGame g = game.newInstance();

        for (Member m : members) {
            MPRunning.put(m, g);
            Account account = MasterRegistry.accountRegistry().get(m.getUser());
            if (isMaple) {
                account.modifyMaple(Modify.SUBTRACT, bet);
            } else {
                account.modifyGold(Modify.SUBTRACT, bet);
            }
        }

        channel.sendMessage(new EmbedBuilder().setColor(new Color(255, 200, 0))
                .setDescription("**" + game.name() + " Game** - " + Main.curr(isMaple) + " " + bet).build()).queue();
        g.setValues(bet, isMaple, members);
        g.setup(channel);
    }

    public static MPCasinoGame getRegisteredMPGame(String name) {
        return MPRegistry.getOrDefault(name.toUpperCase(), null);
    }

    public static Collection<MPCasinoGame> multiplayerGames() {
        return MPRegistry.values();
    }

    public static boolean hasMPGame(Member m) {
        return MPRunning.containsKey(m);
    }

    public static void parseMPTurn(Member m, MessageChannel channel, String[] args) {
        if (MPRunning.containsKey(m)) {
            MPRunning.get(m).turn(m, channel, args);
        }
    }

    public static void endMPGame(MessageChannel channel, boolean silent, Member... members) {
        MPCasinoGame game = null;
        ArrayList<Member> membersList = new ArrayList<>(Arrays.asList(members));

        for (Member i : membersList) {
            if (game == null) game = MPRunning.get(i);
            MPRunning.remove(i);
        }

        SerializableRegistry<User, Account> registry = MasterRegistry.accountRegistry();
        MPCasinoGame finalGame = game;

        if (game.getWinnings() != null || game.getPotPercentage() != null || game.getMultipliers() != null) {
            EmbedBuilder eb = new EmbedBuilder().setColor(new Color(255, 180, 0)).setTitle("THE FINAL WINNINGS");
            StringBuilder sb = new StringBuilder();
            if (game.getWinnings() != null) {
                game.getWinnings().entrySet().parallelStream().forEach(e -> {
                    if (!membersList.contains(e.getKey())) {
                        return;
                    }
                    sb.append("**" + e.getKey().getEffectiveName() + ":** " + Main.curr(finalGame.useMaple()) + e.getValue() + "\n");
                    if (finalGame.useMaple()) {
                        registry.get(e.getKey().getUser()).modifyMaple(Modify.ADD, e.getValue());
                    } else {
                        registry.get(e.getKey().getUser()).modifyGold(Modify.ADD, e.getValue());
                    }
                });
                if (!silent)
                    channel.sendMessage(eb.addField("TOTAL WON", sb.toString(), false).build()).queue();
            } else if (game.getMultipliers() != null) {
                game.getMultipliers().entrySet().parallelStream().forEach(e -> {
                    if (!membersList.contains(e.getKey())) {
                        return;
                    }
                    sb.append("**" + e.getKey().getEffectiveName() + ":** x" + e.getValue() +
                            " -> " + Main.curr(finalGame.useMaple()) + (finalGame.getBet() * e.getValue()) + "\n");
                    if (finalGame.useMaple()) {
                        registry.get(e.getKey().getUser()).modifyMaple(Modify.ADD, (int) (finalGame.getBet() * e.getValue()));
                    } else {
                        registry.get(e.getKey().getUser()).modifyGold(Modify.ADD, (int) (finalGame.getBet() * e.getValue()));
                    }
                });
                if (!silent)
                    channel.sendMessage(eb.addField("MULTIPLIERS", sb.toString(), false).build()).queue();
            } else if (game.getPotPercentage() != null) {
                game.getPotPercentage().entrySet().parallelStream().forEach(e -> {
                    if (!membersList.contains(e.getKey())) {
                        return;
                    }
                    sb.append("**" + e.getKey().getEffectiveName() + ":** " + (e.getValue()*100) + "% -> "
                            + Main.curr(finalGame.useMaple()) + (finalGame.getPot() * e.getValue()) + "\n");
                    if (finalGame.useMaple()) {
                        registry.get(e.getKey().getUser()).modifyMaple(Modify.ADD, (int) (finalGame.getPot() * e.getValue()));
                    } else {
                        registry.get(e.getKey().getUser()).modifyGold(Modify.ADD, (int) (finalGame.getPot() * e.getValue()));
                    }
                });
                if (!silent)
                    channel.sendMessage(eb.addField("POT %", sb.toString(), false).build()).queue();
            }
        }
    }

    public static void onPuddleClose() {

    }

    static {
        registerSPGames(new Slots(), new Blackjack());
        registerMPGames(new TicTacToe(), new Roulette());
    }
}
