package org.baito.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import org.baito.API.command.Command;
import org.baito.MasterRegistry;
import org.baito.data.Account;

import java.awt.*;

public class EconomyCommand implements Command {
    @Override
    public void execute(Member executor, String[] arguments, MessageChannel channel, Message message) {
        Account account = (Account) MasterRegistry.getSerializableRegistry(Account.class).get(executor.getUser());
        Color c = executor.getColor() == null ? new Color(254, 254, 254) : executor.getColor();
        channel.sendMessage(new EmbedBuilder().setTitle(executor.getEffectiveName().toUpperCase() + "'S ACCOUNT")
                .setColor(c).addField("BALANCE", account.balance(), false)
                .addField("MARKET", account.marketBalance(), false)
                .setThumbnail(executor.getUser().getEffectiveAvatarUrl()).build()).queue();
    }
}
