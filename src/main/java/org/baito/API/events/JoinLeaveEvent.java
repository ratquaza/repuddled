package org.baito.API.events;

import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.baito.Main;

import javax.annotation.Nonnull;

public class JoinLeaveEvent extends ListenerAdapter {

    @Override
    public void onGuildJoin(@Nonnull GuildJoinEvent event) {
        Main.setChannel(event.getGuild(), event.getGuild().getDefaultChannel());
    }

    @Override
    public void onGuildLeave(@Nonnull GuildLeaveEvent event) {
        Main.setChannel(event.getGuild(), null);
    }
}
