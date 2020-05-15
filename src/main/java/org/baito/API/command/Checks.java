package org.baito.API.command;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.awt.*;
import java.util.List;

public final class Checks {

    public static final boolean assertUserFound(List<Member> member, MessageChannel channel) {
        if (member.size() != 1) {
            channel.sendMessage(new EmbedBuilder().setAuthor(member.size() == 0 ? "No user found." : "Multiple users found.").setColor(Color.RED).build()).queue();
            return false;
        }
        return true;
    }

}
