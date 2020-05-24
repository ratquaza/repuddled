package org.baito.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import org.baito.API.command.Checks;
import org.baito.API.command.Command;
import org.baito.API.command.CommandRegistry;
import org.baito.MasterRegistry;
import org.baito.account.Account;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class EconomyCommand implements Command {
    @Override
    public void execute(Member executor, String[] arguments, MessageChannel channel, Message message) {
        if (arguments.length == 0) {
            Account account = MasterRegistry.accountRegistry().get(executor.getUser());
            Color c = executor.getColor() == null ? new Color(254, 254, 254) : executor.getColor();
            channel.sendMessage(new EmbedBuilder().setTitle(executor.getEffectiveName().toUpperCase() + "'S ACCOUNT | LEVEL " + account.getLevel())
                    .setColor(c).addField("BALANCE", account.balance(), false)
                    .addField("MARKET", account.marketBalance(), false)
                    .setThumbnail(executor.getUser().getEffectiveAvatarUrl()).build()).queue();
            return;
        } else {
            if (arguments[0].equalsIgnoreCase("pay")) {

            } else {
                List<Member> possible = CommandRegistry.searchForMember(message.getGuild(), String.join(" ", Arrays.copyOfRange(arguments, 0, arguments.length)));

                if (!Checks.assertUserFound(possible, channel)) return;

                Member m = possible.get(0);

                Account account = MasterRegistry.accountRegistry().get(m.getUser());
                Color c = m.getColor() == null ? new Color(254, 254, 254) : m.getColor();
                channel.sendMessage(new EmbedBuilder().setTitle(executor.getEffectiveName().toUpperCase() + "'S ACCOUNT | LEVEL " + account.getLevel())
                        .setColor(c).addField("BALANCE", account.balance(), false)
                        .addField("MARKET", account.marketBalance(), false)
                        .setThumbnail(m.getUser().getEffectiveAvatarUrl()).build()).queue();
            }
        }
    }
}
