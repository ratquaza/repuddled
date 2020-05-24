package org.baito.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import org.baito.API.command.Checks;
import org.baito.API.command.Command;
import org.baito.API.command.CommandRegistry;
import org.baito.Main;
import org.baito.MasterRegistry;
import org.baito.account.Account;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class AdminCommand implements Command {
    @Override
    public void execute(Member executor, String[] arguments, MessageChannel channel, Message message) {
        if (!executor.getPermissions().contains(Permission.ADMINISTRATOR)) {
            message.delete().queue();
            return;
        }

        if (arguments.length == 0) {

        } else {
            if (arguments[0].equalsIgnoreCase("setnotif")) {
                EmbedBuilder eb = new EmbedBuilder();
                eb.setAuthor("Set this channel as the default notification channel.").setColor(Color.GREEN);
                channel.sendMessage(eb.build()).queue();
                Main.setChannel(message.getGuild(), (TextChannel) channel);
                return;
            } else if (arguments[0].equalsIgnoreCase("info")) {
                Member m;
                List<Member> possible = CommandRegistry.searchForMember(message.getGuild(), String.join(" ", Arrays.copyOfRange(arguments, 1, arguments.length)));

                if (!Checks.assertUserFound(possible, channel)) return;

                m = possible.get(0);
                Account account = MasterRegistry.accountRegistry().get(m.getUser());

                EmbedBuilder eb = new EmbedBuilder();
                eb.setTitle(m.getUser().getName().toUpperCase() +
                        (m.getEffectiveName().equalsIgnoreCase(m.getUser().getName()) ? " " : "/" + m.getEffectiveName().toUpperCase() + " ") + "INFO");
                eb.setColor(m.getColor() == null ? Color.WHITE : m.getColor());
                eb.addField("FLAGS", account.getFlags().toString(), false);
                channel.sendMessage(eb.build()).queue();
            }
        }
    }
}
