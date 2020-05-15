package org.baito.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import org.baito.API.command.Command;
import org.baito.API.image.ImageUtils;
import org.baito.MasterRegistry;
import org.baito.casino.Casino;
import org.baito.casino.games.multiplayer.TicTacToe;
import org.baito.casino.games.singeplayer.SPCasinoGame;
import org.baito.data.Account;
import org.baito.data.Condition;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Collection;

public class CasinoCommand implements Command {
    @Override
    public void execute(Member executor, String[] arguments, MessageChannel channel, Message message) {
        if (arguments.length == 0) {
            EmbedBuilder eb = new EmbedBuilder();

            eb.setTitle("**CASINO**");
            eb.setColor(new Color(255, 200, 0));
            StringBuilder sb = new StringBuilder();
            Collection<SPCasinoGame> list = Casino.singleplayerGames();
            for (SPCasinoGame i : list) {
                sb.append("**" + i.name() + "** | Level Minimum: *" + i.levelMinimum() + "*\n");
            }
            eb.addField("AVAILABLE GAMES", sb.toString(), false);
            eb.addField("SUBCOMMANDS", "" +
                    ".cas [game] (M/G)[bet] | **Play a game on the list.**\n" +
                    ".cas [query] | **Use an option in a running game.**", false);

            BufferedImage img = ImageUtils.getImage("CASINOICON.png");
            ImageUtils.embedImage(channel, img, eb, "CASINOICON", "png");
        } else {
            if (Casino.hasSPGame(executor)) {
                Casino.parseSPTurn(executor, channel, arguments);
            } else {
                EmbedBuilder eb = new EmbedBuilder().setColor(new Color(255, 0, 0));
                if (arguments.length < 2) {
                    channel.sendMessage(eb.setAuthor("Insufficient arguments.").build()).queue();
                    return;
                }

                String game = arguments[0].toUpperCase();
                SPCasinoGame gameObj = Casino.getRegisteredSPGame(game);

                if (gameObj == null) {
                    channel.sendMessage(eb.setAuthor("Invalid game.").build()).queue();
                    return;
                }

                Integer bet;
                boolean useMaple = false;

                if (Character.toUpperCase(arguments[1].charAt(0)) == 'M' || Character.toUpperCase(arguments[1].charAt(0)) == 'G') {
                    try {
                        bet = Integer.parseInt(arguments[1].substring(1));
                    } catch (NumberFormatException e) {
                        channel.sendMessage(eb.setAuthor("Invalid number.").build()).queue();
                        return;
                    }
                    useMaple = Character.toUpperCase(arguments[1].charAt(0)) == 'M';
                } else {
                    try {
                        bet = Integer.parseInt(arguments[1]);
                    } catch (NumberFormatException e) {
                        channel.sendMessage(eb.setAuthor("Invalid number.").build()).queue();
                        return;
                    }
                }

                Account playerAccount = (Account) MasterRegistry.getSerializableRegistry(Account.class).get(executor.getUser());

                if ((useMaple && playerAccount.condMaple(Condition.EQUALGREATER, bet)) || (!useMaple && playerAccount.condGold(Condition.EQUALGREATER, bet))) {
                   Casino.startSPGame(executor, gameObj, channel, bet, useMaple);
                } else {
                   channel.sendMessage(eb.setAuthor("Insufficient funds.").build()).queue();
                }
            }
        }
    }
}
