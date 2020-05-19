package org.baito.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import org.baito.API.command.Checks;
import org.baito.API.command.Command;
import org.baito.API.command.CommandRegistry;
import org.baito.API.image.ImageUtils;
import org.baito.API.registry.SerializableRegistry;
import org.baito.Main;
import org.baito.MasterRegistry;
import org.baito.casino.Casino;
import org.baito.casino.games.multiplayer.MPCasinoGame;
import org.baito.casino.games.singeplayer.SPCasinoGame;
import org.baito.data.Account;
import org.baito.data.Condition;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

public class CasinoCommand implements Command {
    private static HashMap<Member, Lobby> lobbies = new HashMap<>();

    @Override
    public void execute(Member executor, String[] arguments, MessageChannel channel, Message message) {
        if (arguments.length == 0 && !Casino.hasSPGame(executor) && !Casino.hasMPGame(executor)) {
            EmbedBuilder eb = new EmbedBuilder();

            eb.setTitle("**CASINO**");
            eb.setColor(new Color(255, 200, 0));
            StringBuilder sb = new StringBuilder();
            Collection<SPCasinoGame> list = Casino.singleplayerGames();
            for (SPCasinoGame i : list) {
                sb.append("**" + i.name() + "** | Level Minimum: *" + i.levelMinimum() + "*\n");
            }

            StringBuilder mb = new StringBuilder();
            Collection<MPCasinoGame> mplist = Casino.multiplayerGames();
            for (MPCasinoGame i : mplist) {
                mb.append("**" + i.name() + "** | Level Minimum: *" + i.levelMinimum() + "*\n");
            }
            eb.addField("SINGPLEAYER GAMES", sb.toString(), false);
            eb.addField("MULTIPLAYER GAMES", mb.toString(), false);
            eb.addField("SUBCOMMANDS", "" +
                    ".cas play [game] (M/G)[bet] | **Play a singleplayer game on the list.**\n" +
                    ".cas host [game] (M/G)[bet] | **Host a multiplayer game on the list.**\n" +
                    ".cas join [player name] | **Join a multiplayer game.**\n" +
                    ".cas start | **Starts the multiplayer game you're hosting.**\n" +
                    ".cas [query] | **Use an option in a running game.**", false);

            BufferedImage img = ImageUtils.getImage("CASINOICON.png");
            ImageUtils.embedImage(channel, img, eb, true, "CASINOICON", "png");
        } else {
            if (Casino.hasSPGame(executor) || Casino.hasMPGame(executor)) {
                if (Casino.hasSPGame(executor)) {
                    Casino.parseSPTurn(executor, channel, arguments);
                } else {
                    Casino.parseMPTurn(executor, channel, arguments);
                }
            } else {
                EmbedBuilder eb = new EmbedBuilder().setColor(new Color(255, 0, 0));
                // Playing singleplayer
                if (arguments[0].equalsIgnoreCase("play")) {
                    if (arguments.length < 3) {
                        channel.sendMessage(eb.setAuthor("Insufficient arguments.").build()).queue();
                        return;
                    }

                    String game = arguments[1].toUpperCase();
                    SPCasinoGame gameObj = Casino.getRegisteredSPGame(game);

                    if (gameObj == null) {
                        channel.sendMessage(eb.setAuthor("Invalid game.").build()).queue();
                        return;
                    }

                    int bet;
                    boolean useMaple = false;

                    if (Character.toUpperCase(arguments[2].charAt(0)) == 'M' || Character.toUpperCase(arguments[2].charAt(0)) == 'G') {
                        try {
                            bet = Integer.parseInt(arguments[2].substring(1));
                        } catch (NumberFormatException e) {
                            channel.sendMessage(eb.setAuthor("Invalid number.").build()).queue();
                            return;
                        }
                        useMaple = Character.toUpperCase(arguments[2].charAt(0)) == 'M';
                    } else {
                        try {
                            bet = Integer.parseInt(arguments[2]);
                        } catch (NumberFormatException e) {
                            channel.sendMessage(eb.setAuthor("Invalid number.").build()).queue();
                            return;
                        }
                    }

                    Account playerAccount = MasterRegistry.accountRegistry().get(executor.getUser());

                    if ((useMaple && playerAccount.condMaple(Condition.EQUALGREATER, bet)) || (!useMaple && playerAccount.condGold(Condition.EQUALGREATER, bet))) {
                        Casino.startSPGame(executor, gameObj, channel, bet, useMaple);
                    } else {
                        channel.sendMessage(eb.setAuthor("Insufficient funds.").build()).queue();
                    }
                    // Hosting multiplayer
                } else if (arguments[0].equalsIgnoreCase("host")) {
                    if (arguments.length < 3) {
                        channel.sendMessage(eb.setAuthor("Insufficient arguments.").build()).queue();
                        return;
                    }
                    if (lobbies.containsKey(executor)) {
                        channel.sendMessage(eb.setAuthor("You are already in a lobby.").build()).queue();
                        return;
                    }
                    MPCasinoGame game = Casino.getRegisteredMPGame(arguments[1]);

                    if (game == null) {
                        channel.sendMessage(eb.setAuthor("Invalid game.").build()).queue();
                        return;
                    }

                    int bet;
                    boolean useMaple = false;

                    if (Character.toUpperCase(arguments[2].charAt(0)) == 'M' || Character.toUpperCase(arguments[2].charAt(0)) == 'G') {
                        try {
                            bet = Integer.parseInt(arguments[2].substring(1));
                        } catch (NumberFormatException e) {
                            channel.sendMessage(eb.setAuthor("Invalid number.").build()).queue();
                            return;
                        }
                        useMaple = Character.toUpperCase(arguments[2].charAt(0)) == 'M';
                    } else {
                        try {
                            bet = Integer.parseInt(arguments[2]);
                        } catch (NumberFormatException e) {
                            channel.sendMessage(eb.setAuthor("Invalid number.").build()).queue();
                            return;
                        }
                    }

                    Account playerAccount = MasterRegistry.accountRegistry().get(executor.getUser());

                    if ((useMaple && playerAccount.condMaple(Condition.EQUALGREATER, bet)) || (!useMaple && playerAccount.condGold(Condition.EQUALGREATER, bet))) {
                        channel.sendMessage(eb.setAuthor("Successfully opened a lobby!").setColor(Color.GREEN).build()).queue();
                        Lobby lobby = new Lobby(executor, game, bet, useMaple);
                        lobbies.put(executor, lobby);
                    } else {
                        channel.sendMessage(eb.setAuthor("Insufficient funds.").build()).queue();
                    }
                    // Starting a multiplayer game
                } else if (arguments[0].equalsIgnoreCase("start")) {
                    if (!lobbies.containsKey(executor) || !lobbies.get(executor).getHost().equals(executor)) {
                        channel.sendMessage(eb.setAuthor("You are not in a lobby, or are not the host of the lobby.").build()).queue();
                        return;
                    }

                    Lobby l = lobbies.get(executor);
                    if (l.playerCount() < l.getGame().minPlayers) {
                        channel.sendMessage(eb.setAuthor("Insufficient players. You need atleast " + l.getGame().minPlayers + " to start.").build()).queue();
                        return;
                    }

                    SerializableRegistry<User, Account> registry = MasterRegistry.accountRegistry();
                    ArrayList<Member> toRemove = new ArrayList<>();

                    // Ensure all players have enough money
                    for (Member i : l.players) {
                        Account a = registry.get(i.getUser());
                        if (!((l.useMaple && a.condMaple(Condition.EQUALGREATER, l.bet)) || (!l.useMaple && a.condGold(Condition.EQUALGREATER, l.bet)))) {
                            toRemove.add(i);
                        }
                    }

                    // Everyone needs to be kicked
                    if (toRemove.size() == l.players.size()) {
                        channel.sendMessage(eb.setAuthor("Could not start a lobby, no player has enough funds of " + (l.useMaple ? Main.maple() : Main.gold()) + l.bet + ".")
                                .build()).queue();
                        return;
                    }

                    // Lobby doesn't have enough to reach minimum after kicking
                    if (l.players.size() - toRemove.size() < l.game.minPlayers) {
                        channel.sendMessage(eb.setAuthor("Could not start a lobby, not enough players. Did some players have enough funds before starting?")
                                .build()).queue();
                        return;
                    }

                    // Remove lobby from HashMap
                    for (Member i : l.players) {
                        lobbies.remove(i);
                    }

                    // Remove all players that can't play
                    l.players.remove(toRemove);

                    // Check if host was removed, and assign new host
                    // not really needed, but good to do
                    if (toRemove.contains(l.host)) {
                        l.host = l.players.get((int)(Math.random() * l.players.size()));
                    }

                    // Start the game
                    Casino.startMPGame(l.game, channel, l.bet, l.useMaple, l.players.toArray(new Member[0]));

                    // Joining a multiplayer game
                } else if (arguments[0].equalsIgnoreCase("join")) {
                    if (arguments.length < 2) {
                        channel.sendMessage(eb.setAuthor("Insufficient arguments.").build()).queue();
                        return;
                    }
                    if (lobbies.containsKey(executor)) {
                        channel.sendMessage(eb.setAuthor("You are already in a lobby.").build()).queue();
                        return;
                    }

                    Member m;
                    List<Member> possible = CommandRegistry.searchForMember(message.getGuild(), String.join(" ", Arrays.copyOfRange(arguments, 1, arguments.length)));

                    if (!Checks.assertUserFound(possible, channel)) return;

                    m = possible.get(0);
                    Lobby l;

                    if (!lobbies.containsKey(m) || !lobbies.get(m).host.equals(m)) {
                        channel.sendMessage(eb.setAuthor(m.getEffectiveName() + " is not hosting a game.").build()).queue();
                        return;
                    }

                    l = lobbies.get(m);
                    if (l.playerCount() < l.getGame().maxPLayers) {
                        l.addPlayer(executor);
                        lobbies.put(executor, l);
                        channel.sendMessage(eb.setAuthor("Joined " + m.getEffectiveName() + "'s " + l.getGame().name().toUpperCase() + " lobby.").setColor(Color.GREEN).build()).queue();
                    } else {
                        channel.sendMessage(eb.setAuthor(m.getEffectiveName() + "'s lobby is full.").build()).queue();
                        return;
                    }
                    // Exiting a lobby
                } else if (arguments[0].equalsIgnoreCase("leave")) {
                    if (!lobbies.containsKey(executor)) {
                        channel.sendMessage(eb.setAuthor("You are not in a lobbyy.").build()).queue();
                        return;
                    }
                    Lobby l = lobbies.get(executor);
                    l.players.remove(executor);
                    lobbies.remove(executor);
                    if (l.players.size() > 0) {
                        if (l.host.equals(executor)) {
                            l.host = l.players.get((int)(Math.random() * l.players.size()));
                        }
                    }

                    channel.sendMessage(eb.setAuthor("Successfully left the lobby.").setColor(Color.GREEN).build()).queue();
                }
            }
        }
    }

    public static class Lobby {

        private Member host;
        private ArrayList<Member> players = new ArrayList<>();
        private MPCasinoGame game;
        private int bet;
        private boolean useMaple;

        public Lobby(Member host, MPCasinoGame game, int bet, boolean useMaple) {
            this.host = host;
            this.game = game;
            this.bet = bet;
            this.useMaple = useMaple;
            players.add(host);
        }

        public void addPlayer(Member m) {
            players.add(m);
        }

        public void removePlayer(Member m) {
            players.remove(m);
        }

        public Member getHost() {
            return host;
        }

        public MPCasinoGame getGame() {
            return game;
        }

        public int getBet() {
            return bet;
        }

        public boolean usingMaple() {
            return useMaple;
        }

        public int playerCount() {
            return players.size();
        }

    }
}
